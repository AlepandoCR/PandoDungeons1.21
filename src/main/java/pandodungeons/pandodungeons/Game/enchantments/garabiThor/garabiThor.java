package pandodungeons.pandodungeons.Game.enchantments.garabiThor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.List;
import org.bukkit.util.EulerAngle;

import static pandodungeons.pandodungeons.Utils.ParticleUtils.spawnElectricParticleCircle;

public class garabiThor {
    private static final JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    public static void handleGarabiThor(Player player, ItemStack item) {
        Location playerLocation = player.getEyeLocation();
        Vector direction = playerLocation.getDirection().normalize();

        ArmorStand armorStand = player.getWorld().spawn(playerLocation.clone().add(direction.clone().multiply(1.5)), ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.getEquipment().setItemInMainHand(item);
        armorStand.setCustomNameVisible(false);

        final double speed = 1.5; // Speed at which the ArmorStand moves
        final long startTime = System.currentTimeMillis(); // Record start time

        new BukkitRunnable() {
            private double angle = 0;
            private double timesTped = 0;

            @Override
            public void run() {

                Location currentLocation = armorStand.getLocation();

                if(!currentLocation.getWorld().equals(player.getWorld()) || !currentLocation.isWorldLoaded() || !currentLocation.isChunkLoaded()){
                    armorStand.remove();
                    this.cancel();
                    return;
                }

                if(currentLocation.distance(playerLocation) > 300){
                    armorStand.remove();
                    this.cancel();
                    return;
                }

                // Move the ArmorStand forward
                Vector movement = direction.clone().multiply(speed);
                currentLocation.add(movement);
                armorStand.teleport(currentLocation);
                spawnElectricParticleCircle(currentLocation.add(0,0.3,0), 1,5);
                timesTped++;


                // Rotate the item in the armor stand's right arm
                angle += 30; // Adjust rotation speed
                if (angle >= 360) angle -= 360;
                double radians = Math.toRadians(angle);
                armorStand.setRightArmPose(new EulerAngle(radians, 0, 0)); // Rotate the arm around two axes

                // Check if the ArmorStand has collided with a block
                Block block = currentLocation.getBlock();
                if (!block.getType().equals(Material.AIR)) {
                    strikeLightning(armorStand.getLocation());
                    armorStand.remove();
                    this.cancel();
                    return;
                }

                // Check if the ArmorStand has collided with any entities
                @NotNull List<Entity> entities = armorStand.getNearbyEntities(1, 1, 1);
                entities.remove(player);

                if (!entities.isEmpty()) {
                    strikeLightning(armorStand.getLocation());
                    currentLocation.createExplosion(1,false, false);
                    armorStand.remove();
                    double damage = timesTped * 2; // Damage multiplier based on timesTped
                    for (Entity entity : entities) {
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingEntity = (LivingEntity) entity;
                            livingEntity.damage(damage, player); // Apply damage with player as the attacker
                        }
                    }
                    this.cancel();
                    return;
                }

                // Check if 10 seconds have passed
                if (System.currentTimeMillis() - startTime >= 10000) {
                    strikeLightning(armorStand.getLocation());
                    armorStand.remove();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void strikeLightning(Location location) {
        Location[] circle = getCircle(location, 3, 3);
        for (Location loc : circle) {
            loc.getWorld().strikeLightning(loc);
        }
    }

    private static Location[] getCircle(Location center, double radius, int amount) {
        Location[] circle = new Location[amount];
        for (int i = 0; i < amount; i++) {
            double angle = 2 * Math.PI * i / amount;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            circle[i] = new Location(center.getWorld(), x, center.getY(), z);
        }
        return circle;
    }
}
