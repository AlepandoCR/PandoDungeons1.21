package pandoClass.classes.mage;

import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.classes.mage.skills.GravityAlterationSkill;
import pandoClass.classes.mage.skills.OrbFriendSkill;
import pandoClass.classes.mage.skills.TimeRewindSkill;
import pandodungeons.PandoDungeons;

public class Mage extends ClassRPG {
    public Mage(RPGPlayer player, PandoDungeons plugin) {
        super("MageClass", player, plugin);
    }

    @Override
    protected String getName() {
        return "Mago";
    }

    @Override
    protected void setSkills() {
        setFirstSkill(new OrbFriendSkill(rpgPlayer.getFirstSkilLvl(), player, plugin));
        setSecondSkill(new TimeRewindSkill(rpgPlayer.getSecondSkilLvl(),player,plugin));
        setThirdSkill(new GravityAlterationSkill(rpgPlayer.getThirdSkillLvl(),player,plugin));
    }

    @Override
    protected void skillsToTrigger() {
        firstSkill.action();
        secondSkill.action();
        thirdSkill.action();
    }

    @Override
    protected void toReset() {

    }
}
