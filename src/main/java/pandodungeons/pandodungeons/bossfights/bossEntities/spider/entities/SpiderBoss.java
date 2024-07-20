package pandodungeons.pandodungeons.bossfights.bossEntities.spider.entities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.pandodungeons.Game.Stats;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class SpiderBoss {
    private Spider spider;
    private final JavaPlugin plugin;

    public SpiderBoss(JavaPlugin plugin, Location location) {
        this.plugin = plugin;
        Player target = LocationUtils.findNearestPlayer(location.getWorld(), location);
        Stats targetStats = Stats.fromPlayer(target);
        int targetPrestige = targetStats.getPrestige();
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("La ubicación proporcionada no es válida");
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            spider = world.spawn(location, Spider.class, (spider) -> {
                spider.setTarget(target);
                spider.addScoreboardTag("spiderBoss");
                spider.addScoreboardTag("bossMob");
                if (targetPrestige < 1) {
                    spider.setCustomName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Araña Gigante");
                } else {
                    spider.setCustomName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Araña Gigante" + ChatColor.RED + " Prestigio <" + targetPrestige + ">");
                }
                double baseHealth = 500d * (1 + ((double) targetPrestige / 2));
                spider.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, targetPrestige));
                spider.setMaxHealth(baseHealth);
                spider.setHealth(baseHealth);
            });
        });
    }

    public Spider getSpider() {
        return spider;
    }

    public void setSpiderTarget(Location location) {
        if (spider != null) {
            spider.setTarget(LocationUtils.findNearestPlayer(location.getWorld(), location));
        }
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
