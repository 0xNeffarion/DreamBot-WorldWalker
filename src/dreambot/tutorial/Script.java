package dreambot.tutorial;

import dreambot.tutorial.api.walking.WorldWalking;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

@ScriptManifest(name = "DreamBot World Walking Tutorial", version = 1.0, author = "Neffarion", category = Category.MISC)
public final class Script extends AbstractScript {

    private static final Tile TARGET = new Tile(3098, 9908, 0);

    @Override
    public int onLoop() {
        if (WorldWalking.shouldWalk(8)) {
            WorldWalking.walk(TARGET);
        }

        return 600;
    }
}
