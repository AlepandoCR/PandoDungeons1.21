package pandoClass.tank.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class HealTeammatesSkill extends Skill {
    public HealTeammatesSkill(int lvl, Player player) {
        super(lvl, player);
    }

    @Override
    protected boolean canActivate() {
        return isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        double radius = 20 * (lvl * 0.2);
        List<Entity> entities = owner.getNearbyEntities(radius,radius,radius);
        if(!entities.isEmpty()){
            for(Entity entity : entities){
                if(entity instanceof Player player){
                    player.heal(0.2 * (lvl * 0.2));
                }
            }
        }
    }

    @Override
    public void reset() {

    }
}
