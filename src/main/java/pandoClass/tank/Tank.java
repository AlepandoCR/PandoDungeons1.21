package pandoClass.tank;

import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.tank.skills.ExtraHeartsSkill;
import pandoClass.tank.skills.HealTeammatesSkill;
import pandoClass.tank.skills.MagicShieldSkill;

public class Tank extends ClassRPG {
    public Tank(RPGPlayer player) {
        super("TankClass", player);
    }

    @Override
    public void setSkills(){
        setFirstSkill(new ExtraHeartsSkill(player.getFirstSkilLvl(), player.getPlayer()));
        setSecondSkill(new HealTeammatesSkill(player.getSecondSkilLvl(), player.getPlayer()));
        setThirdSkill(new MagicShieldSkill(player.getThirdSkillLvl(), player.getPlayer()));
    }


    protected void skillsToTrigger() {
        firstSkill.action();
        secondSkill.action();
        thirdSkill.action();
    }

    @Override
    protected void toReset() {
        firstSkill.reset();
    }
}
