package pandodungeons.pandodungeons.bossfights.bossEntities.forestGuardian.entities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.pandodungeons.Game.Stats;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class ForestGuardian {
    private Evoker evoker;
    int targetPrestige;

    public ForestGuardian(JavaPlugin plugin, Location location) {
        Player target = LocationUtils.findNearestPlayer(location.getWorld(), location);
        Stats targetStats = Stats.fromPlayer(target);
        targetPrestige = targetStats.getPrestige();
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("La ubicación proporcionada no es válida");
        }
        double dmg = (1 + (double) targetPrestige / 2);
        int intDmg = (int) dmg;
        PotionEffect strengthEffect = new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, intDmg, false, false, false);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            evoker = world.spawn(location, Evoker.class, (evoker) -> {
                evoker.setCustomName(ChatColor.DARK_GREEN + "Guardián del Bosque");
                evoker.setCustomNameVisible(true);
                if (targetPrestige < 1) {
                    evoker.setCustomName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Guardián del Bosque");
                } else {
                    evoker.setCustomName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Guardián del Bosque" + ChatColor.RED + " Prestigio <" + targetPrestige + ">");
                }
                double baseHealth = 500d * (1 + ((double) targetPrestige / 2));
                evoker.addPotionEffect(strengthEffect);
                if(baseHealth > 2048D){
                    baseHealth = 2048;
                }
                evoker.setMaxHealth(baseHealth);
                evoker.setHealth(baseHealth);
                evoker.addScoreboardTag("bossMob");
                evoker.setTarget(target);
            });
        });
    }

    public Evoker getEvoker() {
        return evoker;
    }

    public void setEvokerTarget(Location location) {
        if (evoker != null) {
            evoker.setTarget(LocationUtils.findNearestPlayer(location.getWorld(), location));
        }
    }
}
