package pandoClass.classes.assasin;

import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.classes.assasin.skills.LifeStealSkill;
import pandoClass.classes.assasin.skills.SilentStepSkill;
import pandoClass.classes.assasin.skills.SpeedBoostSkill;
import pandodungeons.PandoDungeons;

public class Assasin extends ClassRPG {
    public Assasin(RPGPlayer player, PandoDungeons plugin) {
        super("AssassinClass", player, plugin);
    }

    @Override
    protected String getName() {
        return "Asesino";
    }

    @Override
    protected void setSkills() {
        setFirstSkill(new LifeStealSkill(rpgPlayer.getFirstSkilLvl(), rpgPlayer.getPlayer()));
        setSecondSkill(new SilentStepSkill(rpgPlayer.getSecondSkilLvl(), rpgPlayer.getPlayer()));
        setThirdSkill(new SpeedBoostSkill(rpgPlayer.getThirdSkillLvl(), rpgPlayer.getPlayer()));
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
