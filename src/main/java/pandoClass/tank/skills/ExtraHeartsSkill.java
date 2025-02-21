package pandoClass.tank.skills;

import com.sk89q.worldedit.jlibnoise.module.combiner.Max;
import org.apache.logging.log4j.spi.CopyOnWrite;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import pandoClass.Skill;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class ExtraHeartsSkill extends Skill {
    double extraHealthControl = 0;

    public ExtraHeartsSkill(int lvl, Player player) {
        super(lvl, player);
    }

    @Override
    protected boolean canActivate() {
        return isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        double extraHealth = this.lvl * 1.0;
        if(extraHealthControl != extraHealth){
            double currentMaxHealth = owner.getAttribute(Attribute.MAX_HEALTH).getValue();
            double newMaxHealth = currentMaxHealth + extraHealth;
            owner.setMaxHealth(newMaxHealth);
            extraHealthControl = extraHealth;
        }
    }

    @Override
    public void reset() {
        owner.setMaxHealth(20.0);
    }
}
