package pandodungeons.pandodungeons.bossfights.bossEntities.queenBee.entities;

import org.bukkit.*;
import org.bukkit.entity.Bee;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import pandodungeons.pandodungeons.Game.Stats;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class QueenBee {
    private BlockDisplay body;
    private Bee bee;

    public QueenBee(JavaPlugin plugin, Location location) {
        //Obtenemos las stats del objetivo para asi determinar las stats de la abeja reina en relación al nivel del jugador
        Player target = LocationUtils.findNearestPlayer(location.getWorld(), location);
        Stats targetStats = Stats.fromPlayer(target);
        int targetPrestige = targetStats.prestige();
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("La ubicación proporcionada no es válida");
        }
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            // Crear y configurar la entidad BlockDisplay
            body = (BlockDisplay) world.spawn(location, BlockDisplay.class, (display) -> {
                display.setBlock(Material.HONEY_BLOCK.createBlockData());
                if(targetPrestige < 1){
                    display.setCustomName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Reina Abeja");
                }else{
                    display.setCustomName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Reina Abeja" + ChatColor.RED + " Prestigio <" + targetPrestige + ">");
                }

                display.setCustomNameVisible(true);
                display.setPersistent(true);
                display.addScoreboardTag("queenBeeBlock");

                // Ajustar la escala y la transformación para centrar el bloque
                Transformation scale = new Transformation(
                        new Vector3f(-2.5f, -2f, -2.5f), // Traslación para centrar el bloque en la abeja
                        new AxisAngle4f(0f, 0f, 0f, 0f), // Sin rotación inicial
                        new Vector3f(5f, 5f, 5f), // Escalar el bloque
                        new AxisAngle4f(0f, 0f, 0f, 0f) // Sin rotación final
                );
                display.setTransformation(scale);
                display.setDisplayHeight(5.0f);
                display.setDisplayWidth(5.0f);
            });
            double dmg = (1 + (double) targetPrestige / 2);
            double baseHealth = 500d * (1 + ((double) targetPrestige / 2));
            int intDmg = (int) dmg;
            PotionEffect strengthEffect = new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, intDmg, false, false, false);
            bee = (Bee) world.spawn(location, Bee.class, (bee1) -> {
                double health = baseHealth;
                if(health > 2048D){
                    health = 2048;
                }
                bee1.setAnger(0);
                bee1.setTarget(target);
                bee1.setHasStung(false);
                bee1.addScoreboardTag("queenBee");
                bee1.addScoreboardTag("beefight");
                bee1.addScoreboardTag("bossMob");
                bee1.addPotionEffect(strengthEffect);
                bee1.setMaxHealth(health);
                bee1.setHealth(health);
            });
        });
    }

    public BlockDisplay getBody() {
        return body;
    }

    public Bee getBee() {
        return bee;
    }

    public void setBeeTarget(Location location) {
        if (bee != null) {
            bee.setHasStung(false);
            bee.setTarget(LocationUtils.findNearestPlayer(location.getWorld(), location));
        }
    }

    public void updateBodyRotation() {
        if (bee != null && body != null) {
            Location beeLocation = bee.getLocation();
            Location bodyLocation = beeLocation.clone().add(0, 0, 0); // Ajustar la posición del cuerpo para centrarlo
            body.teleport(bodyLocation);
            body.setRotation(beeLocation.getYaw(), 0);
        }
    }
}
