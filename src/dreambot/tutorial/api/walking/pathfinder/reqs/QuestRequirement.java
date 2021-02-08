package dreambot.tutorial.api.walking.pathfinder.reqs;

import org.dreambot.api.methods.quest.book.Quest;

public final class QuestRequirement implements Requirement {

    private final Quest quest;

    public QuestRequirement(Quest quest) {
        this.quest = quest;
    }

    public Quest getQuest() {
        return quest;
    }

    @Override
    public boolean isMet() {
        return quest.isFinished();
    }

}
