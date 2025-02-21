package pandoClass.assasin;

import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.assasin.skills.LifeStealSkill;
import pandoClass.assasin.skills.SilentStepSkill;
import pandoClass.assasin.skills.SpeedBoostSkill;

public class Assasin extends ClassRPG {
    public Assasin(RPGPlayer player) {
        super("AssassinClass", player);
    }

    @Override
    protected void setSkills() {
        setFirstSkill(new LifeStealSkill(player.getFirstSkilLvl(), player.getPlayer()));
        setSecondSkill(new SilentStepSkill(player.getSecondSkilLvl(), player.getPlayer()));
        setThirdSkill(new SpeedBoostSkill(player.getThirdSkillLvl(), player.getPlayer()));
    }

    @Override
    protected void skillsToTrigger() {
        firstSkill.action();
        secondSkill.action();
        thirdSkill.action();
    }

    @Override
    protected void toReset() {
        thirdSkill.reset();
    }
}
