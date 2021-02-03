package dreambot.tutorial.api.walking.pathfinder.factory;

import dreambot.tutorial.api.walking.pathfinder.WorldPathFinder;
import dreambot.tutorial.api.walking.pathfinder.nodes.AbstractWorldNode;
import dreambot.tutorial.api.walking.pathfinder.nodes.impl.TeleportNode;
import dreambot.tutorial.api.walking.pathfinder.reqs.SkillRequirement;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.GameObject;

public final class WorldNodeFactory {

    private WorldNodeFactory() {
    }

    public static WorldPathFinder createNodes(WorldPathFinder pathFinder) {

        connectNodes(pathFinder,
                new TeleportNode<>("Dwarven Mine to Falador", new Tile(3058, 9777, 0), "Staircase", "Climb-up", GameObject.class),
                new TeleportNode<>("Falador to Dwarven Mine", new Tile(3061, 3377, 0), "Staircase", "Climb-down", GameObject.class)
        );

        connectNodes(pathFinder,
                new TeleportNode<>("Dwarven Mine to Motherlode", new Tile(3060, 9765, 0), "Cave", "Enter", GameObject.class, new SkillRequirement(Skill.MINING, 30)),
                new TeleportNode<>("Motherlode to Dwarven Mine", new Tile(3728, 5692, 0), "Tunnel", "Exit", GameObject.class)
        );

        connectNodes(pathFinder,
                new TeleportNode<>("Mining Guild to Motherlode", new Tile(3054, 9744, 0), "Cave", "Enter", GameObject.class, new SkillRequirement(Skill.MINING, 30)),
                new TeleportNode<>("Motherlode to Mining Guild", new Tile(3718, 5678, 0), "Tunnel", "Exit", GameObject.class, new SkillRequirement(Skill.MINING, 60))
        );

        connectNodes(pathFinder,
                new TeleportNode<>("Mining Guild to Falador", new Tile(3021, 9739, 0), "Ladder", "Climb-up", GameObject.class),
                new TeleportNode<>("Falador to Mining Guild", new Tile(3021, 3339, 0), "Ladder", "Climb-down", GameObject.class, new SkillRequirement(Skill.MINING, 60))
        );


        return pathFinder;
    }

    private static void connectNodes(WorldPathFinder pathFinder, AbstractWorldNode a1, AbstractWorldNode a2) {
        a1.connect(a2);
        a2.connect(a1);
        pathFinder.addNode(a1);
        pathFinder.addNode(a2);
    }

}
