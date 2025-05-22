package pandodungeons.bossfights.bossEntities.magmaCube.entities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.Game.Stats;
import pandodungeons.Utils.LocationUtils;

public class CubitoBoom {
    private MagmaCube magmaCube;
    private final JavaPlugin plugin;

    public CubitoBoom(JavaPlugin plugin, Location location) {
        this.plugin = plugin;
        Player target = LocationUtils.findNearestPlayer(location.getWorld(), location);
        Stats targetStats = Stats.fromPlayer(target);
        int targetPrestige = targetStats.prestige();
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("La ubicación proporcionada no es válida");
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            magmaCube = world.spawn(location, MagmaCube.class, (magmaCube) -> {
                magmaCube.setTarget(target);
                magmaCube.addScoreboardTag("magmaCubeBoss");
                magmaCube.addScoreboardTag("bossMob");
                if (targetPrestige < 1) {
                    magmaCube.setCustomName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Cubo de Magma Gigante");
                } else {
                    magmaCube.setCustomName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Cubo de Magma Gigante" + ChatColor.RED + " Prestigio <" + targetPrestige + ">");
                }
                double baseHealth = 600d * (1 + ((double) targetPrestige / 2));
                magmaCube.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, targetPrestige));
                if (baseHealth > 2048D) {
                    baseHealth = 2048;
                }
                magmaCube.setMaxHealth(baseHealth);
                magmaCube.setHealth(baseHealth);
                magmaCube.setSize(10); // Establecer un tamaño grande para el jefe
            });
        });
    }

    public MagmaCube getMagmaCube() {
        return magmaCube;
    }

    public void setMagmaCubeTarget(Location location) {
        if (magmaCube != null) {
            magmaCube.setTarget(LocationUtils.findNearestPlayer(location.getWorld(), location));
        }
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
