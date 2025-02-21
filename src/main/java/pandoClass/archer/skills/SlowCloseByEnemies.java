package pandoClass.archer.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class SlowCloseByEnemies extends Skill {
    public SlowCloseByEnemies(int lvl, Player player) {
        super(lvl, player);
    }

    @Override
    protected boolean canActivate() {
        return isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        double radius = 2 * (lvl * 0.025);
        int slowAmount = (int) (1 * (lvl * 0.1));

        @NotNull List<Entity> near = owner.getNearbyEntities(radius,radius,radius);
        if(!near.isEmpty()){
            for(Entity entity : near){
                if(entity instanceof LivingEntity livingEntity){
                    if(entity instanceof Player){
                        return;
                    }
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,3,slowAmount,false,false,false));
                }
            }
        }
    }

    @Override
    public void reset() {

    }
}
