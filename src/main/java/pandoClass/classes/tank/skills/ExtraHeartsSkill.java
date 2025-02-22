package pandoClass.classes.tank.skills;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import pandoClass.Skill;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class ExtraHeartsSkill extends Skill {
    boolean reseted = false;
    double extraHealthControl = 0;

    public ExtraHeartsSkill(int lvl, Player player) {
        super(lvl, player);
        description = "Aumenta la vida máxima del jugador";
        displayValue = "c3576f5e5dda52938195830ac4c1fd16f3e0f0e48706b4ef1202035cc330b599";
    }

    @Override
    public String getName() {
        return "Vida Extra";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        double extraHealth = this.lvl * 1.0;
        // Se asegura de actualizar solo si la cantidad extra de vida cambió o se ha reseteado
        if(extraHealthControl != extraHealth || reseted){
            // Fijar la vida máxima al valor base (20.0) más la vida extra
            double newMaxHealth = 20.0 + extraHealth;
            owner.setMaxHealth(newMaxHealth);
            extraHealthControl = extraHealth;
            reseted = false;
        }
    }

    @Override
    public void reset() {
        reseted = true;
    }
}
