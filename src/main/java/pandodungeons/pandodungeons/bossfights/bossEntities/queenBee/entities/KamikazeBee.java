package pandodungeons.pandodungeons.bossfights.bossEntities.queenBee.entities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Bee;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class KamikazeBee {
    private final Location location;
    private final JavaPlugin plugin;
    private Bee kamikazeBee;
    private final World world;
    private QueenBee queen;

    public KamikazeBee(JavaPlugin plugin, Location location,QueenBee queen) {
        this.plugin = plugin;
        this.location = location;
        this.world = location.getWorld();
        this.queen = queen;
        summonKamikazeBee();
    }

    public void summonKamikazeBee() {
        kamikazeBee = world.spawn(location, Bee.class, bee -> {
            // Ajustar los atributos de velocidad de vuelo
            AttributeInstance flyingSpeedAttr = bee.getAttribute(Attribute.GENERIC_FLYING_SPEED);
            if (flyingSpeedAttr != null) {
                flyingSpeedAttr.setBaseValue(0.9); // Aumentar la velocidad de vuelo
            }

            AttributeInstance followRangeAttr = bee.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
            if (followRangeAttr != null) {
                followRangeAttr.setBaseValue(100.0); // Aumentar el rango de seguimiento
            }

            // Ajustar la velocidad de movimiento (puede afectar el comportamiento de vuelo)
            AttributeInstance movementSpeedAttr = bee.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (movementSpeedAttr != null) {
                movementSpeedAttr.setBaseValue(0); // Aumentar la velocidad de movimiento
            }
            double queenHealth = queen.getBee().getMaxHealth();
            bee.setTarget(LocationUtils.findNearestPlayer(world, location));
            bee.addScoreboardTag("kamikaze");
            bee.addScoreboardTag("beefight");
            bee.addScoreboardTag("bossMob");
            bee.setInvulnerable(false);
            bee.setGlowing(true);
            bee.setCustomNameVisible(true);
            bee.setMaxHealth(queenHealth / 6);
            bee.setHealth(queenHealth / 6);

            // Intentar forzar un aumento en la velocidad de vuelo
            bee.setAI(true);
        });
    }

    public Bee getKamikazeBee() {
        return kamikazeBee;
    }

    public Location getBeeLocation() {
        return kamikazeBee.getLocation();
    }
}
