package pandodungeons.pandodungeons.bossfights.bossEntities.triton.attacks;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pandodungeons.pandodungeons.bossfights.bossEntities.triton.entities.Triton;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class WaterBlastAttack {

    private final Triton triton;

    public WaterBlastAttack(Triton triton) {
        this.triton = triton;
    }

    public void execute() {
        World world = triton.getDrowned().getLocation().getWorld();
        Player target = LocationUtils.findNearestPlayer(world, triton.getDrowned().getLocation());
        if (target == null) return;

        Location tritonLocation = triton.getDrowned().getLocation();

        // Crear el ArmorStand para el proyectil
        ArmorStand waterBlast = world.spawn(tritonLocation.add(0, 1, 0), ArmorStand.class, stand -> {
            stand.setInvisible(true);
            stand.setInvulnerable(true);
            stand.setGravity(false);
            stand.setHelmet(new ItemStack(Material.BLUE_STAINED_GLASS));
        });

        // Animar el proyectil
        new BukkitRunnable() {
            int projectileLife = 80;

            @Override
            public void run() {
                if (waterBlast.isDead() || !waterBlast.isValid() || projectileLife < 1) {
                    waterBlast.remove();
                    cancel();
                    return;
                }

                Location currentLocation = waterBlast.getLocation();
                Location targetLocation = target.getLocation();
                Vector direction = targetLocation.toVector().subtract(currentLocation.toVector()).normalize();

                currentLocation.add(direction);
                waterBlast.teleport(currentLocation);
                waterBlast.getWorld().spawnParticle(Particle.SPLASH, currentLocation, 10);

                if (currentLocation.distance(targetLocation) < 1.5) {
                    // Efecto de empuje
                    target.setVelocity(direction.multiply(1.5));

                    // Efecto de lentitud
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));

                    waterBlast.remove();
                    cancel();
                }
                projectileLife--;
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 0, 1);
    }
}
