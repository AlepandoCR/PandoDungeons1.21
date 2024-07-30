package pandodungeons.pandodungeons.bossfights.bossEntities.forestGuardian.attacks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle;
import pandodungeons.pandodungeons.bossfights.bossEntities.forestGuardian.entities.ForestGuardian;

import java.util.List;

public class VinesStrike {
    private final ForestGuardian forestGuardian;
    private final JavaPlugin plugin;

    public VinesStrike(ForestGuardian forestGuardian, JavaPlugin plugin) {
        this.forestGuardian = forestGuardian;
        this.plugin = plugin;
    }

    public void execute() {
        Location location = forestGuardian.getEvoker().getLocation();
        List<Player> players = location.getWorld().getPlayers();
        for (Player player : players) {
            player.sendMessage(ChatColor.GREEN + "¡El Guardián del Bosque te atrapa con enredaderas!");
        }
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 100) { // Detener después de 5 segundos (100 ticks)
                    cancel();
                    return;
                }
                for (Player player : players) {
                    if(player.getWorld().equals(location.getWorld())){
                        if (location.distance(player.getLocation()) < 20) {
                            player.damage(1.0);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 4));
                            // Generar partículas de enredaderas alrededor del jugador
                            spawnVineParticles(player.getLocation());
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void spawnVineParticles(Location location) {
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 8) {
            double x = Math.cos(angle) * 1.5;
            double z = Math.sin(angle) * 1.5;
            Location particleLocation = location.clone().add(x, 0, z);
            location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, particleLocation, 5, 0.1, 0.5, 0.1, 0.05);
        }
    }
}
