package pandoClass.archer;

import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.archer.skills.ArrowExplotionSkill;
import pandoClass.archer.skills.SaveAmmoSkill;
import pandoClass.archer.skills.SlowCloseByEnemies;

public class Archer extends ClassRPG {
    public Archer(RPGPlayer player) {
        super("ArcherClass", player);
    }

    @Override
    protected void setSkills() {
        setFirstSkill(new SaveAmmoSkill(player.getFirstSkilLvl(), player.getPlayer()));
        setSecondSkill(new SlowCloseByEnemies(player.getSecondSkilLvl(), player.getPlayer()));
        setThirdSkill(new ArrowExplotionSkill(player.getThirdSkillLvl(), player.getPlayer()));
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
