package pandodungeons.bossfights.bossEntities.queenBee.attacks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bee;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import pandodungeons.bossfights.bossEntities.queenBee.entities.QueenBee;


public class VenomAttack {

    private final QueenBee queen;
    private final int numberOfArrows;
    private final double radius;

    public VenomAttack(QueenBee queen, int numberOfArrows, double radius) {
        this.queen = queen;
        this.numberOfArrows = numberOfArrows;
        this.radius = radius;
    }

    public void execute() {
        Bee bee = queen.getBee();
        if (bee == null) {
            return;
        }

        World world = bee.getWorld();
        Location beeLocation = bee.getLocation();

        for (int i = 0; i < numberOfArrows; i++) {
            double angle = 2 * Math.PI * i / numberOfArrows;
            double x = beeLocation.getX() + radius * Math.cos(angle);
            double z = beeLocation.getZ() + radius * Math.sin(angle);
            Location arrowLocation = new Location(world, x, beeLocation.getY(), z);

            Arrow arrow = world.spawn(arrowLocation, Arrow.class);
            arrow.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 2), true); // 5 segundos de veneno
            arrow.setShooter(bee);

            Vector direction = arrowLocation.toVector().subtract(beeLocation.toVector()).normalize();
            arrow.setVelocity(direction.multiply(1.5)); // Ajustar la velocidad de la flecha
        }
    }
}
