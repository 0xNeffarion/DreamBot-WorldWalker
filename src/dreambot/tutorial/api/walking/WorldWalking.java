package dreambot.tutorial.api.walking;


import dreambot.tutorial.api.walking.pathfinder.factory.WorldNodeFactory;
import dreambot.tutorial.api.walking.pathfinder.WorldPathFinder;
import dreambot.tutorial.api.walking.pathfinder.nodes.AbstractWorldNode;
import org.dreambot.api.Client;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.map.impl.CollisionMap;

import java.util.List;

public final class WorldWalking extends Walking {

    private WorldWalking() {
    }

    private static final WorldPathFinder worldPathFinder = WorldNodeFactory.createNodes(new WorldPathFinder());

    public static WorldPathFinder getWorldPathFinder() {
        return worldPathFinder;
    }

    public static boolean walk(Entity entity) {
        Tile tile = entity.getTile();
        if (CollisionMap.isBlocked(Map.getFlag(tile))) {
            tile = Map.getWalkable(tile);
        }

        return walk(tile);
    }

    public static boolean walk(Tile target) {
        Tile current = Client.getLocalPlayer().getTile();

        if (current == null || target == null) {
            return false;
        }

        if (worldPathFinder.canNativeWalk(current, target)) {
            return Walking.walk(target);
        }

        List<AbstractWorldNode> path = worldPathFinder.calculate(current, target);
        if (path != null && !path.isEmpty()) {
            AbstractWorldNode n = path.get(0);
            if (n.canTraverse()) {
                return n.traverse();
            } else {
                return n.walkTo();
            }
        }

        return false;
    }

    public static boolean walk(int x, int y, int z) {
        return walk(new Tile(x, y, z));
    }

    public static boolean walk(int x, int y) {
        return walk(new Tile(x, y));
    }


}
