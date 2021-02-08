package dreambot.tutorial.api.walking.pathfinder.nodes;

import dreambot.tutorial.api.walking.pathfinder.reqs.Requirement;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Locatable;
import org.dreambot.api.wrappers.map.TileReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractWorldNode implements Locatable {

    private static final int DISTANCE_TRIGGER = 25;

    private final Tile location;
    private final String name;
    private final List<Requirement> requirements = new ArrayList<>();

    private AbstractWorldNode connection = null;

    public AbstractWorldNode(String name, Tile location) {
        this.name = name;
        this.location = location;
    }

    public AbstractWorldNode(String name, Tile location, Requirement... reqs) {
        this(name, location);

        if (reqs != null) {
            Collections.addAll(requirements, reqs);
        }
    }

    public final String getName() {
        return name;
    }

    public final AbstractWorldNode getConnection() {
        return connection;
    }

    public final void connect(AbstractWorldNode node) {
        this.connection = node;
    }

    public final boolean hasConnection() {
        return this.connection != null;
    }

    @Override
    public final Tile getTile() {
        return this.location;
    }

    @Override
    public final double distance(Tile tile) {
        return getTile().distance(tile);
    }

    @Override
    public final double walkingDistance(Tile tile) {
        return getTile().walkingDistance(tile);
    }

    @Override
    public final int getX() {
        return getTile().getX();
    }

    @Override
    public final int getY() {
        return getTile().getY();
    }

    @Override
    public final int getGridX() {
        return getTile().getGridX();
    }

    @Override
    public final int getGridY() {
        return getTile().getGridY();
    }

    @Override
    public final int getZ() {
        return getTile().getZ();
    }

    @Override
    public final TileReference getTileReference() {
        return getTile().getTileReference();
    }

    public boolean walkTo() {
        return Walking.walk(getTile());
    }

    public abstract boolean traverse();

    public boolean canTraverse() {
        return getTile().distance() <= DISTANCE_TRIGGER && Map.isLocal(getTile());
    }

    public final List<Requirement> getRequirements() {
        return this.requirements;
    }

    public final boolean hasRequirements() {
        return !getRequirements().isEmpty();
    }

    public final boolean meetsRequirements() {
        if (!hasRequirements()) {
            return true;
        }

        for (Requirement r : getRequirements()) {
            if (r != null && !r.isMet()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof AbstractWorldNode)) {
            return false;
        }

        AbstractWorldNode n = ((AbstractWorldNode) obj);

        return n.getTile().equals(getTile()) && n.getName().equals(getName());
    }

    @Override
    public String toString() {
        return getName() + " - " + getTile().toString();
    }

    @Override
    public int hashCode() {
        return getTile().hashCode() * getName().hashCode();
    }

}
