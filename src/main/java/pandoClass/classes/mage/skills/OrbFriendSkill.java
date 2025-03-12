package pandoClass.classes.mage.skills;

import pandoClass.Skill;

import java.util.UUID;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class OrbFriendSkill extends Skill {
    protected OrbFriendSkill(int lvl, UUID player) {
        super(lvl, player);
        description = "Tienes un amigo orbe que te ayudará de distintas formas";
        displayValue = "";
    }

    @Override
    public String getName() {
        return "Amigo Orbe";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {

    }

    @Override
    public void reset() {

    }
}
