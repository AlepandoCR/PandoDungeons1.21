package pandodungeons.pandodungeons.bossfights.bossEntities.spider.attacks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.CaveSpider;
import pandodungeons.pandodungeons.bossfights.bossEntities.spider.entities.SpiderBoss;

import java.util.ArrayList;
import java.util.List;

public class SpiderSummon {

    private final SpiderBoss spiderBoss;
    private final List<CaveSpider> minions = new ArrayList<>();

    public SpiderSummon(SpiderBoss spiderBoss) {
        this.spiderBoss = spiderBoss;
    }

    public void execute() {
        Location location = spiderBoss.getSpider().getLocation();
        World world = location.getWorld();

        for (int i = 0; i < 5; i++) { // Invocar 5 mini arañas
            CaveSpider miniSpider = world.spawn(location, CaveSpider.class, (spider) -> {
                spider.setMaxHealth(spiderBoss.getSpider().getMaxHealth() /4);
                spider.setHealth(spiderBoss.getSpider().getMaxHealth() / 4);
                spider.setCustomName(ChatColor.DARK_GREEN + "Mini Araña");
                spider.setCustomNameVisible(true);
                spider.addScoreboardTag("miniSpider");
                spider.addScoreboardTag("bossMob");
                spider.setTarget(spiderBoss.getSpider().getTarget());
            });
            minions.add(miniSpider);
        }
    }

    public boolean areMinionsAlive() {
        return minions.stream().anyMatch(spider -> !spider.isDead());
    }

    public void removeMinions() {
        for (CaveSpider spider : minions) {
            if (spider != null && !spider.isDead()) {
                spider.remove();
            }
        }
        minions.clear();
    }

    public List<CaveSpider> getMinions() {
        return minions;
    }
}
