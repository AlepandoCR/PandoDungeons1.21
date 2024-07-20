package pandodungeons.pandodungeons.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class BossUtils {

    public static boolean isAnyBossAlive(Location location, double radius) {
        for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (isBoss(entity)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBoss(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        return entity.getScoreboardTags().contains("bossMob");
    }
}
