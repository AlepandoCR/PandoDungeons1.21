package pandodungeons.pandodungeons.bossfights.bossEntities.forestGuardian.attacks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pandodungeons.pandodungeons.bossfights.bossEntities.forestGuardian.entities.ForestGuardian;

import java.util.List;

public class Earthquake {
    private final ForestGuardian forestGuardian;
    private final JavaPlugin plugin;

    public Earthquake(ForestGuardian forestGuardian, JavaPlugin plugin) {
        this.forestGuardian = forestGuardian;
        this.plugin = plugin;
    }

    public void execute() {
        Location location = forestGuardian.getEvoker().getLocation();
        List<Player> players = location.getWorld().getPlayers();
        for (Player player : players) {
            player.sendMessage(ChatColor.GOLD + "¡El Guardián del Bosque desata un terremoto!");
        }
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 10) {
                    cancel();
                    return;
                }
                for (Player player : players) {
                    if(player.getWorld().equals(location.getWorld())){
                        if (location.distance(player.getLocation()) < 30){

                            player.damage(2.0);

                            // Calcular dirección hacia arriba
                            Vector upDirection = new Vector(0, 0.1, 0);

                            // Aplicar el empuje vertical hacia arriba
                            player.setVelocity(upDirection.multiply(2));
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
