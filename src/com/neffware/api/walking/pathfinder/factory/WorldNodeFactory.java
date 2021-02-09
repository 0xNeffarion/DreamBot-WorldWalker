package com.neffware.api.walking.pathfinder.factory;

import com.neffware.api.walking.pathfinder.WorldPathFinder;
import com.neffware.api.walking.pathfinder.nodes.AbstractWorldNode;
import com.neffware.api.walking.pathfinder.nodes.impl.CompoundNode;
import com.neffware.api.walking.pathfinder.nodes.impl.ShipTravelNode;
import com.neffware.api.walking.pathfinder.nodes.impl.SimpleNode;
import com.neffware.api.walking.pathfinder.reqs.InventoryRequirement;
import com.neffware.api.walking.pathfinder.reqs.SkillRequirement;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.GameObject;

public final class WorldNodeFactory {

    private static final int COINS = 995;

    private WorldNodeFactory() {
    }

    public static WorldPathFinder createNodes(WorldPathFinder pathFinder) {

        connectNodes(pathFinder,
                new SimpleNode<>("Dwarven Mine to Falador", new Tile(3058, 9777, 0), "Staircase", "Climb-up", GameObject.class),
                new SimpleNode<>("Falador to Dwarven Mine", new Tile(3061, 3377, 0), "Staircase", "Climb-down", GameObject.class)
        );

        connectNodes(pathFinder,
                new SimpleNode<>("Dwarven Mine to Motherlode", new Tile(3060, 9765, 0), "Cave", "Enter", GameObject.class, new SkillRequirement(Skill.MINING, 30)),
                new SimpleNode<>("Motherlode to Dwarven Mine", new Tile(3728, 5692, 0), "Tunnel", "Exit", GameObject.class)
        );

        connectNodes(pathFinder,
                new SimpleNode<>("Mining Guild to Motherlode", new Tile(3054, 9744, 0), "Cave", "Enter", GameObject.class, new SkillRequirement(Skill.MINING, 30)),
                new SimpleNode<>("Motherlode to Mining Guild", new Tile(3718, 5678, 0), "Tunnel", "Exit", GameObject.class, new SkillRequirement(Skill.MINING, 60))
        );

        connectNodes(pathFinder,
                new SimpleNode<>("Mining Guild to Falador", new Tile(3021, 9739, 0), "Ladder", "Climb-up", GameObject.class),
                new SimpleNode<>("Falador to Mining Guild", new Tile(3021, 3339, 0), "Ladder", "Climb-down", GameObject.class, new SkillRequirement(Skill.MINING, 60))
        );

        connectNodes(pathFinder,
                new SimpleNode<>("Mining Guild to Falador", new Tile(3021, 9739, 0), "Ladder", "Climb-up", GameObject.class),
                new SimpleNode<>("Falador to Mining Guild", new Tile(3021, 3339, 0), "Ladder", "Climb-down", GameObject.class, new SkillRequirement(Skill.MINING, 60))
        );

        connectNodes(pathFinder,
                new ShipTravelNode("Port Sarim to Karamja", new Tile(3029, 3217, 0),
                        "Seaman Lorris", "Talk-to",
                        new String[]{"Yes please."},
                        new InventoryRequirement(COINS, 30)
                ),
                new ShipTravelNode("Karamja to Port Sarim", new Tile(2954, 3146, 0),
                        "Customs officer", "Talk-to",
                        new String[]{"Can I journey on this ship?", "Ok."},
                        new InventoryRequirement(COINS, 30)
                )
        );

        connectNodes(pathFinder,
                new ShipTravelNode("Port Piscarilius to Port Sarim", new Tile(1824, 3691, 0),
                        "Veos", "Talk-to",
                        new String[]{"Can you take me somewhere?", "Travel to Port Sarim."}
                ),
                new ShipTravelNode("Port Sarim to Port Piscarilius", new Tile(3055, 3245, 0),
                        "Veos", "Talk-to",
                        new String[]{"Can you take me somewhere?", "Travel to Port Piscarilius."}
                )
        );

        Tile trapdoorTile = new Tile(3095, 3469, 0);
        SimpleNode<GameObject> openTrapdoor = new SimpleNode<>("Edgeville Trapdoor to Edgeville Cave", trapdoorTile, "Trapdoor", "Open", GameObject.class);
        SimpleNode<GameObject> useTrapdoor = new SimpleNode<>("Edgeville Trapdoor to Edgeville Cave", trapdoorTile, "Trapdoor", "Climb-down", GameObject.class);
        CompoundNode compoundNode = new CompoundNode("Edgeville Trapdoor to Edgeville Cave", trapdoorTile, new AbstractWorldNode[]{openTrapdoor, useTrapdoor});

        connectNodes(pathFinder,
                compoundNode,
                new SimpleNode<>("Edgeville Dungeoun to Edgeville", new Tile(3097, 9869, 0), "Ladder", "Climb-up", GameObject.class));

        return pathFinder;
    }

    private static void connectNodes(WorldPathFinder pathFinder, AbstractWorldNode a1, AbstractWorldNode a2) {
        a1.connect(a2);
        a2.connect(a1);
        pathFinder.addNode(a1);
        pathFinder.addNode(a2);
    }

}
