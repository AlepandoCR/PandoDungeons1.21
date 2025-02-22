package pandoClass.classes.tank.skills;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.List;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class HealTeammatesSkill extends Skill {
    int ticks = 0;
    int ticks1 = 0;

    public HealTeammatesSkill(int lvl, Player player) {
        super(lvl, player);
        description = "Cura a todos los jugadores pasivamente en un area";
        displayValue = "ac3a4b51a48002890fb271cdc1517eaf41ab00253c1b7d824272736bb41663d5";
    }

    @Override
    public String getName() {
        return "Curación a Aliados";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        double radius = 10 + 4 * (lvl * 0.2);
        List<Entity> entities = owner.getNearbyEntities(radius,radius,radius);
        if(!entities.isEmpty()){
            for(Entity entity : entities){
                if(entity instanceof Player player){
                    player.heal(0.2 * (lvl * 0.02));
                    spawnHeartCircle(player);
                }
            }
        }
    }

    @Override
    public void reset() {

    }

    public void spawnHeartCircle(Player player) {
        ticks++;
        Location location = player.getLocation();
        double radius = 0.5; // Radio del círculo en bloques
        int particlesCount = 3; // Número de partículas a generar

        if(ticks % 16 == 0){
            for (int i = 0; i < particlesCount; i++) {
                double angle = 2 * Math.PI * i / particlesCount;
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);
                Location particleLocation = location.clone().add(x, 0, z);
                player.getWorld().spawnParticle(Particle.HEART, particleLocation, 1);
            }
        }
    }
}
