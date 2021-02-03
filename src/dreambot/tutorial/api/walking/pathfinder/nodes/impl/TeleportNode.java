package dreambot.tutorial.api.walking.pathfinder.nodes.impl;

import dreambot.tutorial.api.walking.pathfinder.nodes.AbstractWorldNode;
import dreambot.tutorial.api.walking.pathfinder.reqs.Requirement;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;

import java.lang.reflect.Type;

// GameObjects or NPCs that make you move to a different location with a single simple interaction
// i.e: Dungeon ladders, Obstacles, Entrances, etc..
public final class TeleportNode<T extends Entity> extends AbstractWorldNode {

    private static final int DISTANCE_TRAVERSE_TRIGGER = 12;

    private final Filter<Entity> entityFilter = i -> i != null && i.exists() && i.getName() != null
            && i.getName().equalsIgnoreCase(getEntityName()) && i.hasAction(getAction()) && i.distance() <= DISTANCE_TRAVERSE_TRIGGER;

    private final Filter<NPC> npcFilter = entityFilter::match;
    private final Filter<GameObject> gameObjectFilter = entityFilter::match;

    private final String entityName;
    private final String entityAction;
    private final Type entityType;

    public TeleportNode(String name, Tile location, String entityName, String entityAction, Class<T> entityType) {
        this(name, location, entityName, entityAction, entityType, (Requirement[]) null);
    }

    public TeleportNode(String name, Tile location, String entityName, String entityAction, Class<T> entityType, Requirement... reqs) {
        super(name, location, reqs);
        this.entityName = entityName;
        this.entityAction = entityAction;
        this.entityType = entityType.getGenericSuperclass();
    }

    public final String getEntityName() {
        return entityName;
    }

    public final String getAction() {
        return entityAction;
    }

    @Override
    public boolean traverse() {
        Entity e = getEntity();

        if (e == null || !e.exists()) {
            return false;
        }

        if (!Map.canReach(getTile()) && Walking.canWalk(getTile())) {
            return Walking.walk(getTile());
        }

        return e.interact(getAction());
    }

    @Override
    public boolean canTraverse() {
        if (!super.canTraverse()) {
            return false;
        }

        Entity e = getEntity();
        return e != null && e.exists() && e.getTile().distance() <= DISTANCE_TRAVERSE_TRIGGER;
    }

    private Entity getEntity() {
        if (entityType.equals(NPC.class.getGenericSuperclass())) {
            return NPCs.closest(npcFilter);
        } else if (entityType.equals(GameObject.class.getGenericSuperclass())) {
            return GameObjects.closest(gameObjectFilter);
        }

        return null;
    }

}
