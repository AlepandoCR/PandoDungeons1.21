package pandodungeons.pandodungeons.Utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class ParticleUtils {

    /**
     * Spawns a circle of green particles around the given location.
     *
     * @param location The center location of the circle.
     * @param radius   The radius of the circle.
     * @param points   The number of points (particles) in the circle.
     */
    public static void spawnParticleCircle(Location location, double radius, int points) {
        World world = location.getWorld();
        if (world == null) return;

        double increment = (2 * Math.PI) / points;

        for (int i = 0; i < points; i++) {
            double angle = i * increment;
            double x = location.getX() + radius * Math.cos(angle);
            double z = location.getZ() + radius * Math.sin(angle);
            Location particleLocation = new Location(world, x, location.getY(), z);
            world.spawnParticle(Particle.HAPPY_VILLAGER, particleLocation, 1, 0, 0, 0, 0);
        }
    }

    public static void spawnSoulCircle(Location location, double radius, int points) {
        World world = location.getWorld();
        if (world == null) return;

        double increment = (2 * Math.PI) / points;

        for (int i = 0; i < points; i++) {
            double angle = i * increment;
            double x = location.getX() + radius * Math.cos(angle);
            double z = location.getZ() + radius * Math.sin(angle);
            Location particleLocation = new Location(world, x, location.getY(), z);
            world.spawnParticle(Particle.SOUL, particleLocation, 1, 0, 0, 0, 0);
        }
    }

    public static void spawnHeartParticleCircle(Location location, double radius, int points) {
        World world = location.getWorld();
        if (world == null) return;

        double increment = (2 * Math.PI) / points;

        for (int i = 0; i < points; i++) {
            double angle = i * increment;
            double x = location.getX() + radius * Math.cos(angle);
            double z = location.getZ() + radius * Math.sin(angle);
            Location particleLocation = new Location(world, x, location.getY(), z);
            world.spawnParticle(Particle.HEART, particleLocation, 1, 0, 0, 0, 0);
        }
    }

    public static void spawnElectricParticleCircle(Location location, double radius, int points) {
        World world = location.getWorld();
        if (world == null) return;

        double increment = (2 * Math.PI) / points;

        for (int i = 0; i < points; i++) {
            double angle = i * increment;
            double x = location.getX() + radius * Math.cos(angle);
            double z = location.getZ() + radius * Math.sin(angle);
            double y = location.getY() + radius * Math.tan(angle);

            Location particleLocation = new Location(world, x, y, z);
            world.spawnParticle(Particle.ELECTRIC_SPARK, particleLocation, 1, 0, 0, 0, 0);
        }
    }
}
