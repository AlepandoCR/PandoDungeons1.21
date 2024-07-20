package pandodungeons.pandodungeons.bossfights.bossEntities.vex.entities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.pandodungeons.Game.Stats;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class VexBoss {
    private Vex vex;

    public VexBoss(JavaPlugin plugin, Location location) {
        Player target = LocationUtils.findNearestPlayer(location.getWorld(), location);
        Stats targetStats = Stats.fromPlayer(target);
        int targetPrestige = targetStats.getPrestige();
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("La ubicación proporcionada no es válida");
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            vex = world.spawn(location, Vex.class, (vex) -> {
                vex.setTarget(target);
                vex.addScoreboardTag("vexBoss");
                vex.addScoreboardTag("bossMob");
                if(targetPrestige < 1){
                    vex.setCustomName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Vex");
                }else {
                    vex.setCustomName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Vex" + ChatColor.RED + " Prestigio <" + targetPrestige + ">");
                }
                double dmg = (1 + (double) targetPrestige / 2);
                int intDmg = (int) dmg;
                PotionEffect strengthEffect = new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, intDmg, false, false, false);
                double baseHealth = 200d * (1 + ((double) targetPrestige / 2));
                vex.addPotionEffect(strengthEffect);
                vex.setMaxHealth(baseHealth);
                vex.setHealth(baseHealth);
            });
        });
    }

    public Vex getVex() {
        return vex;
    }

    public void setVexTarget(Location location) {
        if (vex != null) {
            vex.setTarget(LocationUtils.findNearestPlayer(location.getWorld(), location));
        }
    }
}
