package pandoClass.classes.farmer;

import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


// Runnable para manejar el comportamiento de las entidades controladas
public class ControlledEntityBehavior extends BukkitRunnable {
    private final Map<LivingEntity, LivingEntity> controlledEntities = new HashMap<>();
    private final LivingEntity entity;
    private final LivingEntity target;
    private final double ogHealth;

    public ControlledEntityBehavior(LivingEntity entity, LivingEntity target, double ogHealth) {
        this.entity = entity;
        this.target = target;
        this.ogHealth = ogHealth;
    }

    @Override
    public void run() {
        if (entity.isDead() || target.isDead() || !entity.isValid() || !target.isValid()) {
            controlledEntities.remove(entity.getUniqueId());
            entity.setMaxHealth(ogHealth);
            this.cancel();
            return;
        }

        if (entity.getLocation().distanceSquared(target.getLocation()) <= 4) { // 2 bloques de distancia
            // Si la entidad puede atacar, asigna el objetivo
            if (entity instanceof Creature) {
                ((Creature) entity).setTarget(target);
            } else {
                // Si no puede atacar, inflige daño directamente
                target.damage(2.0, entity); // Daño de 2 puntos como ejemplo
            }
        } else {
            moveEntityTowards(entity, target.getLocation());
        }
    }

    public void moveEntityTowards(LivingEntity entity, Location targetLocation) {
        if (entity instanceof CraftMob craftMob) {
            net.minecraft.world.entity.Mob nmsMob = craftMob.getHandle();
            PathNavigation navigation = nmsMob.getNavigation();
            if(!navigation.isInProgress()){
                navigation.moveTo(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(), 1.0);
            }
        }
    }
}