package pandoClass.gambling.combiner;

import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;

public class ItemFusionAnimator {

    private final World world;
    private final Location displayALocation;
    private final Location displayBLocation;
    private final Location finalLocationA;
    private final Location finalLocationB;
    private final Location fusionCenter;
    private final PandoDungeons plugin;

    private ItemDisplay displayA;
    private ItemDisplay displayB;

    public ItemFusionAnimator(PandoDungeons plugin, World world, ItemStack itemA, ItemStack itemB, Location centerLocation) {
        this.plugin = plugin;
        this.world = world;
        this.fusionCenter = centerLocation.clone().add(0, 0.5, 0);
        this.displayALocation = fusionCenter.clone();
        this.displayBLocation = fusionCenter.clone();
        this.finalLocationA = new Location(world, 43, 71, 397);
        this.finalLocationB = new Location(world, 30, 71, 397);

        spawnDisplays(itemA, itemB);
        startMovementPhase();
    }

    private void spawnDisplays(ItemStack itemA, ItemStack itemB) {
        displayA = world.spawn(displayALocation, ItemDisplay.class);
        displayA.setItemStack(itemA);

        displayB = world.spawn(displayBLocation, ItemDisplay.class);
        displayB.setItemStack(itemB);

        debug("Displays spawned.");
    }

    private void startMovementPhase() {
        debug("Starting movement phase.");
        moveToLocation(displayA, displayA.getLocation(), finalLocationA, 40, () -> {
            moveToLocation(displayB, displayB.getLocation(), finalLocationB, 40, this::startSpinPhase);
        });
    }

    private void startSpinPhase() {
        debug("Starting spin phase.");
        final int spinTicks = 70; // 3.5 seconds
        final float maxSpeed = 20f; // degrees per tick

        new BukkitRunnable() {
            int tick = 0;
            float currentSpeed = 0f;

            @Override
            public void run() {
                if (tick >= spinTicks) {
                    this.cancel();
                    startSpinDeceleration();
                    return;
                }

                float acceleration = maxSpeed / spinTicks;
                currentSpeed += acceleration;

                rotateDisplay(displayA, currentSpeed);
                rotateDisplay(displayB, currentSpeed);
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void startSpinDeceleration() {
        debug("Starting spin deceleration phase.");
        final int decelTicks = 40;
        final float initialSpeed = 20f;

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= decelTicks) {
                    this.cancel();
                    startCollisionPhase();
                    return;
                }

                float speed = initialSpeed * (1f - (float) tick / decelTicks);

                rotateDisplay(displayA, speed);
                rotateDisplay(displayB, speed);
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void startCollisionPhase() {
        debug("Starting collision phase.");
        final int duration = 20; // 1 second
        Location startA = displayA.getLocation().clone();
        Location startB = displayB.getLocation().clone();

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= duration) {
                    this.cancel();
                    explodeWithParticles();
                    displayA.remove();
                    displayB.remove();
                    return;
                }

                Location currentA = interpolate(startA, fusionCenter, (float) tick / duration);
                Location currentB = interpolate(startB, fusionCenter, (float) tick / duration);

                displayA.teleport(currentA);
                displayB.teleport(currentB);

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void explodeWithParticles() {
        debug("Explosion!");
        world.spawnParticle(Particle.LAVA, fusionCenter, 50, 0.2, 0.2, 0.2, 0.05);
        world.spawnParticle(Particle.CRIT, fusionCenter, 50, 0.2, 0.2, 0.2, 0.05);
        world.playSound(fusionCenter, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1.2f);
    }

    private void moveToLocation(Display display, Location from, Location to, int durationTicks, Runnable onComplete) {
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= durationTicks) {
                    this.cancel();
                    onComplete.run();
                    return;
                }

                float progress = (float) tick / durationTicks;
                Location interpolated = interpolate(from, to, progress);
                display.teleport(interpolated);

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private Location interpolate(Location from, Location to, float t) {
        return from.clone().add(to.toVector().subtract(from.toVector()).multiply(t));
    }

    private void rotateDisplay(Display display, float degrees) {
        display.setRotation(display.getLocation().getYaw() + degrees, 0);
    }

    private void debug(String message) {
        plugin.getLogger().info("[ItemFusionAnimator] " + message);
    }
}
