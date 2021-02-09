package com.neffware.api.walking.pathfinder.nodes.impl;

import com.neffware.api.walking.pathfinder.reqs.Requirement;
import org.dreambot.api.Client;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.Entity;

import java.util.Arrays;

public class DialogNode<T extends Entity> extends SimpleNode<T> {

    private final String[] dialog;

    public DialogNode(String name, Tile location, String entityName, String entityAction,
                      String[] dialog, Class<T> entityType) {
        this(name, location, entityName, entityAction, dialog, entityType, (Requirement) null);
    }

    public DialogNode(String name, Tile location, String entityName, String entityAction,
                      String[] dialog, Class<T> entityType, Requirement... reqs) {
        super(name, location, entityName, entityAction, entityType, reqs);
        this.dialog = dialog;
    }

    public final String[] getDialog() {
        return dialog;
    }

    @Override
    public boolean traverse() {
        if (super.traverse() && MethodProvider.sleepUntil(Dialogues::inDialogue, 8000, 1000)) {
            completeDialogue();
            return true;
        }

        return false;
    }

    @Override
    public boolean canTraverse() {
        return super.canTraverse();
    }

    protected void completeDialogue() {
        if (!Dialogues.inDialogue()) {
            return;
        }

        int option = 0;
        while (Dialogues.inDialogue()) {
            if (!Client.getInstance().getScriptManager().isRunning() || Client.getInstance().getRandomManager().isSolving()) {
                break;
            }

            String npcText = Dialogues.getNPCDialogue();
            String[] options = Dialogues.getOptions();

            if (Dialogues.canContinue()) {
                Dialogues.spaceToContinue();
            } else {
                int fop = option;
                if (Arrays.stream(options).anyMatch(i -> i != null && i.equals(dialog[fop]))) {
                    if (Dialogues.chooseOption(dialog[option])) {
                        option++;
                    }
                } else {
                    MethodProvider.logError("Dialog not found");
                    break;
                }
            }

            MethodProvider.sleepUntil(() -> (!Dialogues.inDialogue()) ||
                    (Dialogues.getNPCDialogue() != null && !Dialogues.getNPCDialogue().equalsIgnoreCase(npcText)), 4500);
        }
    }

}
