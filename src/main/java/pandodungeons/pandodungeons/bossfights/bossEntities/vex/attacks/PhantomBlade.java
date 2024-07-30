package pandodungeons.pandodungeons.bossfights.bossEntities.vex.attacks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pandodungeons.pandodungeons.bossfights.bossEntities.vex.entities.VexBoss;

public class PhantomBlade {
    private final VexBoss vexBoss;
    private final JavaPlugin plugin;

    public PhantomBlade(VexBoss vexBoss, JavaPlugin plugin) {
        this.vexBoss = vexBoss;
        this.plugin = plugin;
    }

    public void execute() {
        Player target = (Player) vexBoss.getVex().getTarget();
        if (target != null) {
            Location startLocation = vexBoss.getVex().getLocation();

            // Crear una armadura con una espada
            ArmorStand blade = (ArmorStand) vexBoss.getVex().getWorld().spawnEntity(startLocation, EntityType.ARMOR_STAND);
            blade.setInvisible(true);
            blade.setGravity(false);
            blade.setItemInHand(new ItemStack(Material.NETHERITE_SWORD));

            // Crear una tarea para mover la espada hacia el jugador
            new BukkitRunnable() {
                int projectileLife = 80; // Vida del proyectil en ticks

                @Override
                public void run() {
                    if (blade.isDead() || target.isDead() || projectileLife < 1) {
                        blade.remove();
                        cancel();
                        return;
                    }

                    Location currentLocation = blade.getLocation();
                    Location targetLocation = target.getLocation();
                    Vector direction = targetLocation.toVector().subtract(currentLocation.toVector()).normalize();

                    currentLocation.add(direction.multiply(0.5));
                    blade.teleport(currentLocation);

                    // Añadir partículas moradas
                    vexBoss.getVex().getWorld().spawnParticle(Particle.WITCH, blade.getLocation(), 10, 0.2, 0.2, 0.2, 0.05);

                    if(targetLocation.getWorld().equals(currentLocation.getWorld())){
                        if (currentLocation.distance(targetLocation) < 1.5) {
                            // Golpear al jugador
                            target.damage(5.0);
                            target.sendMessage(ChatColor.DARK_PURPLE + "¡El Vex te corta con su Espada Fantasma!");
                            blade.remove();
                            cancel();
                            return;
                        }
                    }
                    projectileLife--;
                }
            }.runTaskTimer(plugin, 0, 1);
        }
    }
}
