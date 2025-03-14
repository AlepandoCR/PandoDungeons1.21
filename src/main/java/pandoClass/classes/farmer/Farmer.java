package pandoClass.classes.farmer;

import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.classes.farmer.skils.ExtraHarvestSkill;
import pandoClass.classes.farmer.skils.GolemSkill;
import pandoClass.classes.farmer.skils.TameSkill;
import pandodungeons.pandodungeons.PandoDungeons;

public class Farmer extends ClassRPG {
    public Farmer(RPGPlayer player, PandoDungeons plugin) {
        super("FarmerClass", player,plugin);
    }

    @Override
    protected String getName() {
        return "Granjero";
    }

    @Override
    protected void setSkills() {
        setFirstSkill(new ExtraHarvestSkill(rpgPlayer.getFirstSkilLvl(), player));
        setSecondSkill(new TameSkill(rpgPlayer.getFirstSkilLvl(), player));
        setThirdSkill(new GolemSkill(rpgPlayer.getFirstSkilLvl(), player));
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
