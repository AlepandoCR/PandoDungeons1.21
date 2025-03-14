package pandoClass.classes.tank;

import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.classes.tank.skills.ExtraHeartsSkill;
import pandoClass.classes.tank.skills.HealTeammatesSkill;
import pandoClass.classes.tank.skills.MagicShieldSkill;
import pandodungeons.pandodungeons.PandoDungeons;

public class Tank extends ClassRPG {
    public Tank(RPGPlayer player, PandoDungeons plugin) {
        super("TankClass", player, plugin);
    }

    @Override
    protected String getName() {
        return "Tanque";
    }

    @Override
    public void setSkills(){
        setFirstSkill(new ExtraHeartsSkill(rpgPlayer.getFirstSkilLvl(), rpgPlayer.getPlayer()));
        setSecondSkill(new HealTeammatesSkill(rpgPlayer.getSecondSkilLvl(), rpgPlayer.getPlayer()));
        setThirdSkill(new MagicShieldSkill(rpgPlayer.getThirdSkillLvl(), rpgPlayer.getPlayer()));
    }


    protected void skillsToTrigger() {
        firstSkill.action();
        secondSkill.action();
        thirdSkill.action();
    }

    @Override
    protected void toReset() {

    }
}
