package pandoClass.classes.archer;

import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.classes.archer.skills.ArrowExplotionSkill;
import pandoClass.classes.archer.skills.SaveAmmoSkill;
import pandoClass.classes.archer.skills.DoubleJumSkill;
import pandodungeons.pandodungeons.PandoDungeons;

public class Archer extends ClassRPG {
    public Archer(RPGPlayer player, PandoDungeons plugin) {
        super("ArcherClass", player, plugin);
    }

    @Override
    protected String getName() {
        return "Arquero";
    }

    @Override
    protected void setSkills() {
        setFirstSkill(new SaveAmmoSkill(rpgPlayer.getFirstSkilLvl(), rpgPlayer.getPlayer()));
        setSecondSkill(new DoubleJumSkill(rpgPlayer.getSecondSkilLvl(), rpgPlayer.getPlayer()));
        setThirdSkill(new ArrowExplotionSkill(rpgPlayer.getThirdSkillLvl(), rpgPlayer.getPlayer()));
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
