package com.neffware.api.walking.pathfinder.nodes.impl;

import com.neffware.api.walking.pathfinder.nodes.AbstractWorldNode;
import com.neffware.api.walking.pathfinder.reqs.Requirement;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.map.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Nodes which have multiple stages to be able to interact.
// Example: A trapdoor which needs to be opened before being able to use it
public class CompoundNode extends AbstractWorldNode {

    private final List<AbstractWorldNode> stages = new ArrayList<>();

    public CompoundNode(String name, Tile location, AbstractWorldNode[] stages) {
        this(name, location, stages, (Requirement) null);
    }

    public CompoundNode(String name, Tile location, AbstractWorldNode[] stages, Requirement... reqs) {
        super(name, location, reqs);
        if (stages != null) {
            Collections.addAll(this.stages, stages);
        }
    }

    public List<AbstractWorldNode> getStages() {
        return stages;
    }

    @Override
    public boolean traverse() {
        for (AbstractWorldNode n : getStages()) {
            if (n.canTraverse() && n.traverse()) {
                MethodProvider.sleep(800, 1600);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canTraverse() {
        for (AbstractWorldNode n : getStages()) {
            if (n.canTraverse()) {
                return true;
            }
        }

        return false;
    }

}
