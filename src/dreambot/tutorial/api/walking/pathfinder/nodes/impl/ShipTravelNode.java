package dreambot.tutorial.api.walking.pathfinder.nodes.impl;

import dreambot.tutorial.api.walking.pathfinder.reqs.Requirement;
import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;

public class ShipTravelNode extends DialogNode<NPC> {

    private static final String GANGPLANK_NAME = "Gangplank";
    private static final String GANGPLANK_ACTION = "Cross";

    public ShipTravelNode(String name, Tile location, String entityName,
                          String entityAction, String[] dialog) {
        super(name, location, entityName, entityAction, dialog, NPC.class);
    }

    public ShipTravelNode(String name, Tile location, String entityName,
                          String entityAction, String[] dialog, Requirement... reqs) {
        super(name, location, entityName, entityAction, dialog, NPC.class, reqs);
    }

    @Override
    public boolean traverse() {
        if (super.traverse()) {
            if(MethodProvider.sleepUntil(this::hasTraveled, 10000, 500)){
                MethodProvider.sleep(1500, 2500);
                return crossGangplank();
            }
        }

        return false;
    }

    private static boolean crossGangplank() {
        GameObject gp = getGangplank();
        return gp != null && gp.exists() && gp.interact(GANGPLANK_ACTION);
    }

    private static GameObject getGangplank() {
        return GameObjects.closest(i -> i != null && i.exists() && i.getName().equalsIgnoreCase(GANGPLANK_NAME));
    }

    private boolean hasTraveled() {
        return getTile().distance() > 50
                && Client.getGameState() != GameState.LOADING
                && Client.getGameStateID() == 30
                && Client.getLocalPlayer() != null
                && Client.getLocalPlayer().exists()
                && getGangplank() != null;
    }

}
