package pandoClass.classes.assasin.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class SpeedBoostSkill extends Skill {
    float speedControl = 0;

    public SpeedBoostSkill(int lvl, Player player) {
        super(lvl, player);
        description = "Aumento de la velocidad base";
        displayValue = "890c6ce0574b6b909a94ab3aa0afab95e6b93561b4ea255eb70117d84e523383";
    }

    @Override
    public String getName() {
        return "Boost de Velocidad";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        float boostedSpeed = 0.2f + 0.4f * (lvl * 0.02f);
        if(boostedSpeed != speedControl){
            owner.setWalkSpeed(boostedSpeed);
            speedControl = boostedSpeed;
        }
    }

    @Override
    public void reset() {
    }
}
