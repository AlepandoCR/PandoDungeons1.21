package pandoClass.assasin.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.UUID;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class SpeedBoostSkill extends Skill {
    float speedControl = 0;

    public SpeedBoostSkill(int lvl, Player player) {
        super(lvl, player);
    }

    @Override
    protected boolean canActivate() {
        return isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        float boostedSpeed = 2f + 1.2f * (lvl * 0.02f);
        if(boostedSpeed != speedControl){
            owner.setWalkSpeed(boostedSpeed);
            speedControl = boostedSpeed;
        }
    }

    @Override
    public void reset() {
        owner.setWalkSpeed(2f);
    }
}
