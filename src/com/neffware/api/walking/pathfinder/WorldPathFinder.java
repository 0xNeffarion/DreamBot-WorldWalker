package com.neffware.api.walking.pathfinder;

import com.neffware.api.walking.pathfinder.nodes.AbstractWorldNode;
import com.neffware.api.walking.util.Pair;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.path.impl.GlobalPath;
import org.dreambot.api.methods.walking.path.impl.LocalPath;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.WebNodeType;

import java.util.*;

/**
 * World Path Finder for DreamBot
 * Used to walk around the OSRS World via places that the Web cannot get you through
 * <p>
 * The nodes are not all connected to each other, instead the DreamBot Web itself is used to connect to the pair of nodes you create.
 */
public final class WorldPathFinder {

    private static final int LOCAL_DISTANCE = 50;

    private boolean ignoreRequirements = false;

    private final Set<AbstractWorldNode> worldNodes = new HashSet<>();
    private final Map<Tile, List<AbstractWorldNode>> visitableCache = new WeakHashMap<>();

    public WorldPathFinder() {
        // Removes any web conflicts
        Walking.getWebPathFinder().removeAllowedTypes(
                WebNodeType.ENTRANCE_NODE,
                WebNodeType.STATIC_TELEPORT_NODE,
                WebNodeType.DYNAMIC_TELEPORT_NODE,
                WebNodeType.TOLL_NODE
        );
    }

    public final void addNode(AbstractWorldNode node) {
        worldNodes.add(node);
    }

    public final void addNodes(AbstractWorldNode[] nodes) {
        Collections.addAll(worldNodes, nodes);
    }

    public final void addNodes(List<AbstractWorldNode> nodes) {
        worldNodes.addAll(nodes);
    }

    public void setIgnoreRequirements(boolean ignoreRequirements) {
        this.ignoreRequirements = ignoreRequirements;
    }

    /**
     * Calculates a path using the world nodes from one location to another
     *
     * @param start  starting location tile
     * @param target end location tile
     * @return List of AbstractWorldNode's that represent the path to take. If empty then the path can be taken using the default DreamBot Web. Returns null if no path is found
     */
    public final List<AbstractWorldNode> calculate(Tile start, Tile target) {
        List<AbstractWorldNode> path = new ArrayList<>();
        if (canNativeWalk(start, target)) {
            return path;
        }

        List<AbstractWorldNode> endNodes = visitable(target);
        List<AbstractWorldNode> startNodes = visitable(start);

        if (endNodes.isEmpty() || startNodes.isEmpty()) {
            return null;
        }

        Map<AbstractWorldNode, Pair<AbstractWorldNode, Double>> result = dijkstra(start);
        AbstractWorldNode endNode = null;
        for (AbstractWorldNode n : endNodes) {
            if (result.containsKey(n)) {
                endNode = n;
                break;
            }
        }

        if (endNode == null) {
            return null;
        }

        AbstractWorldNode node = endNode.getConnection();
        path.add(node);

        while (!startNodes.contains(node)) {
            if (result.containsKey(node)) {
                Pair<AbstractWorldNode, Double> n = result.get(node);
                if (n != null) {
                    node = n.getKey();
                    path.add(node);
                }
            }
        }

        Collections.reverse(path);
        return path;
    }

    /**
     * Runs dijkstra algorithm over all the existing world nodes starting from the start tile
     *
     * @param start starting tile to begin search
     * @return Map containing the following structure:
     * Map<x, Pair<y, d>>
     * x: World node
     * y: Parent World node that has shortest path to x from start tile
     * d: Distance to y (shortest linear distance)
     */
    public final Map<AbstractWorldNode, Pair<AbstractWorldNode, Double>> dijkstra(Tile start) {
        List<AbstractWorldNode> temp = visitable(start);
        Queue<AbstractWorldNode> queue = new PriorityQueue<>(Comparator.comparingDouble(i -> i.distance(start)));
        queue.addAll(temp);

        Map<AbstractWorldNode, Pair<AbstractWorldNode, Double>> distances = new HashMap<>();

        worldNodes.forEach(x -> distances.put(x, new Pair<>(null, Double.MAX_VALUE)));

        temp.forEach(x -> {
            distances.put(x, new Pair<>(x, x.getTile().distance(start)));
            distances.put(x.getConnection(), new Pair<>(x, x.getTile().distance(start)));
        });

        Set<AbstractWorldNode> visited = new HashSet<>(temp);

        while (!queue.isEmpty()) {
            AbstractWorldNode node = queue.poll();

            List<AbstractWorldNode> visitable = visitable(node.getConnection().getTile());

            for (AbstractWorldNode n : visitable) {
                if (!visited.contains(n)) {
                    double distance = distances.get(n).getValue();
                    double newDistance = node.getTile().distance(start) + n.distance(node.getConnection().getTile());
                    if (newDistance < distance) {
                        distances.put(n, new Pair<>(node, newDistance));
                        queue.add(n);
                    }
                }
            }

            visited.add(node);
            visited.add(node.getConnection());
        }

        return distances;
    }

    /**
     * Checks if DreamBot Web finder or Local path finders can plot a path to a tile
     *
     * @param start  Starting tile to start a path
     * @param target End tile of path
     * @return true if path can be walked without the use of World nodes, otherwise false
     */
    public final boolean canNativeWalk(Tile start, Tile target) {
        if (start.distance(target) < LOCAL_DISTANCE && org.dreambot.api.methods.map.Map.isLocal(target)) {
            LocalPath<Tile> path;

            if (target.getZ() == start.getZ()) {
                path = Walking.getAStarPathFinder().calculate(start, target);
            } else {
                path = Walking.getDijPathFinder().calculate(start, target);
            }

            if (path != null) {
                return !path.isEmpty() || !(start.distance(target) > 2);
            }
        }


        GlobalPath<AbstractWebNode> path = Walking.getWebPathFinder().calculate(start, target);
        return path != null && !path.hasSpecialNode();
    }

    /**
     * Function to return the possible visitable world nodes from a tile.
     * Uses visitableCache for caching queries.
     *
     * @param start Starting tile to start search
     * @return List of AbstractWorldNode's that can be reached and used (walked to/traversed) from the start tile. World node's which requirements fail are not added.
     * Returns an empty list if nothing is found
     */
    private List<AbstractWorldNode> visitable(Tile start) {
        if (visitableCache.containsKey(start)) {
            return visitableCache.get(start);
        }

        List<AbstractWorldNode> nodes = new ArrayList<>();
        for (AbstractWorldNode n : worldNodes) {
            if (!ignoreRequirements && !n.meetsRequirements()) {
                continue;
            }

            Tile tile = n.getTile();

            if (start.distance(tile) < LOCAL_DISTANCE && org.dreambot.api.methods.map.Map.isLocal(tile)) {
                LocalPath<Tile> path;
                if (tile.getZ() == start.getZ()) {
                    path = Walking.getAStarPathFinder().calculate(start, tile);
                } else {
                    path = Walking.getDijPathFinder().calculate(start, tile);
                }

                if (path != null) {
                    if (path.isEmpty() && start.distance() > 2) {
                        continue;
                    }

                    nodes.add(n);
                }
            } else {
                GlobalPath<AbstractWebNode> path = Walking.getWebPathFinder().calculate(start, tile);
                if (path != null && !path.hasSpecialNode()) {
                    nodes.add(n);
                }
            }
        }

        visitableCache.put(start, nodes);

        return nodes;
    }

    /**
     * Nearest visitable node from given tile
     *
     * @param tile Tile to start search from
     * @return null if nothing is found. Otherwise, the nearest visitable World node that is near the given tile (linear distance)
     */
    public final AbstractWorldNode nearest(Tile tile) {
        return visitable(tile).stream()
                .min(Comparator.comparing(i -> i.getTile().distance(tile))).orElse(null);
    }

    /**
     * Nearest visitable node from the local player tile
     *
     * @return null if nothing is found. Otherwise, the nearest visitable World node that is near the player (linear distance)
     */
    public final AbstractWorldNode nearest() {
        return nearest(Players.localPlayer().getTile());
    }

    public final boolean canReach(Tile start, Tile target) {
        return this.hops(start, target) >= 0;
    }

    /**
     * Total hops needed to get through from one tile to another in world nodes
     *
     * @param start  starting tile
     * @param target ending tile
     * @return -1 if no path is found. Otherwise, it returns the number of world nodes that need to be used to get from start to target
     */
    public final int hops(Tile start, Tile target) {
        List<AbstractWorldNode> path = calculate(start, target);
        if (path != null) {
            return path.size();
        }

        return -1;
    }

}
