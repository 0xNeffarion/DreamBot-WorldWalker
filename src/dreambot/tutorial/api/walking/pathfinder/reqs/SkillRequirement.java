package dreambot.tutorial.api.walking.pathfinder.reqs;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public final class SkillRequirement implements Requirement {

    private final Skill skill;
    private final int level;

    public SkillRequirement(Skill skill, int level) {
        this.skill = skill;
        this.level = level;
    }

    public final Skill getSkill() {
        return skill;
    }

    public final int getLevel() {
        return level;
    }

    @Override
    public final boolean isMet() {
        return Skills.getRealLevel(getSkill()) >= getLevel();
    }

}
