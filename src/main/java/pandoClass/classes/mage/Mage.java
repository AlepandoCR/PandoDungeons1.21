package pandoClass.classes.mage;

import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;

public class Mage extends ClassRPG {
    public Mage(RPGPlayer player) {
        super("MageClass", player);
    }

    @Override
    protected String getName() {
        return "Mago";
    }

    @Override
    protected void setSkills() {

    }

    @Override
    protected void skillsToTrigger() {

    }

    @Override
    protected void toReset() {

    }
}
