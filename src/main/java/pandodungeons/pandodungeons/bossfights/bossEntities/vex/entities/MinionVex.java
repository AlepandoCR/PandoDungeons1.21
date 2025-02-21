package pandodungeons.pandodungeons.bossfights.bossEntities.vex.entities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Vex;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class MinionVex {
    private final Location location;
    private final JavaPlugin plugin;
    private Vex minionVex;
    private final World world;
    private final VexBoss vexBoss;

    public MinionVex(JavaPlugin plugin, Location location, VexBoss vexBoss) {
        this.plugin = plugin;
        this.location = location;
        this.world = location.getWorld();
        this.vexBoss = vexBoss;
        summonMinionVex();
    }

    public void summonMinionVex() {
        minionVex = world.spawn(location, Vex.class, vex -> {
            // Ajustar los atributos
            AttributeInstance flyingSpeedAttr = vex.getAttribute(Attribute.FLYING_SPEED);
            if (flyingSpeedAttr != null) {
                flyingSpeedAttr.setBaseValue(0.8); // Ajustar la velocidad de vuelo
            }

            AttributeInstance followRangeAttr = vex.getAttribute(Attribute.FLYING_SPEED);
            if (followRangeAttr != null) {
                followRangeAttr.setBaseValue(50.0); // Ajustar el rango de seguimiento
            }

            // Ajustar la velocidad de movimiento
            AttributeInstance movementSpeedAttr = vex.getAttribute  (Attribute.FLYING_SPEED);
            if (movementSpeedAttr != null) {
                movementSpeedAttr.setBaseValue(0.3); // Ajustar la velocidad de movimiento
            }

            double vexHealth = vexBoss.getVex().getMaxHealth();
            vex.setCustomName("§5Minion del Vex");
            vex.setTarget(LocationUtils.findNearestPlayer(world, location));
            vex.addScoreboardTag("vexMinion");
            vex.setInvulnerable(false);
            vex.setGlowing(true);
            vex.addScoreboardTag("bossMob");
            vex.setCustomNameVisible(true);
            vex.setMaxHealth(vexHealth / 4);
            vex.setHealth(vexHealth / 4);

            // Intentar forzar un aumento en la velocidad de vuelo
            vex.setAI(true);
        });
    }

    public Vex getMinionVex() {
        return minionVex;
    }

    public Location getMinionVexLocation() {
        return minionVex.getLocation();
    }
}
