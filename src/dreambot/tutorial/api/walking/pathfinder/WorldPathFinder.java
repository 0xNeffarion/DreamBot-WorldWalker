package dreambot.tutorial.api.walking.pathfinder;

import dreambot.tutorial.api.walking.pathfinder.nodes.AbstractWorldNode;
import dreambot.tutorial.api.walking.util.Pair;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.path.impl.GlobalPath;
import org.dreambot.api.methods.walking.path.impl.LocalPath;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.WebNodeType;

import java.util.*;

public final class WorldPathFinder {

    private static final int LOCAL_DISTANCE = 50;

    private final Set<AbstractWorldNode> worldNodes = new HashSet<>();
    private final Map<Tile, List<AbstractWorldNode>> visitableCache = new WeakHashMap<>();

    public WorldPathFinder() {
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

    public final List<AbstractWorldNode> calculate(Tile start, Tile target) {
        List<AbstractWorldNode> path = new ArrayList<>();
        if (canNativeWalk(start, target)) {
            return path;
        }

        Map<AbstractWorldNode, Pair<AbstractWorldNode, Double>> result = dijkstra(start);
        List<AbstractWorldNode> endNodes = visitable(target);
        List<AbstractWorldNode> startNodes = visitable(start);

        if (endNodes.isEmpty() || startNodes.isEmpty()) {
            return null;
        }

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

    private List<AbstractWorldNode> visitable(Tile start) {
        if (visitableCache.containsKey(start)) {
            return visitableCache.get(start);
        }

        List<AbstractWorldNode> nodes = new ArrayList<>();
        for (AbstractWorldNode n : worldNodes) {
            if (!n.meetsRequirements()) {
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

    public final AbstractWorldNode nearest(Tile tile) {
        return visitable(tile).stream()
                .min(Comparator.comparing(i -> i.getTile().distance(tile))).orElse(null);
    }

    public final AbstractWorldNode nearest() {
        return nearest(Players.localPlayer().getTile());
    }

    public final boolean canReach(Tile start, Tile target) {
        return this.hops(start, target) >= 0;
    }

    public final int hops(Tile start, Tile target) {
        List<AbstractWorldNode> path = calculate(start, target);
        if (path != null) {
            return path.size();
        }

        return -1;
    }

}
