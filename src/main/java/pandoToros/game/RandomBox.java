package pandoToros.game;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.Utils.LocationUtils;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Random;

import static pandodungeons.pandodungeons.Game.enchantments.souleater.SoulEaterEnchantment.createHead;

public class RandomBox {

    private static final Random RANDOM = new Random();

    static PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    public static void createBox(Location location) throws MalformedURLException {
        // Crear la cabeza de la caja (normal o rara)
        ItemStack headNormal = createHead("RandomBox", "900d28ff7b543dd088d004b1b1f95b38d444ea0461ff5ae3c68d76c0c16e2527");
        ItemStack headRare = createHead("RandomBox", "81623d5238dab7decd320265cae1dc6ca91b7fa95f34673aaf4b3ad5c6ba16e1");

        // Determinar si es normal o rara
        boolean isRare = RANDOM.nextBoolean(); // 50% probabilidad
        ItemStack head = isRare ? headRare : headNormal;

        // Crear el ArmorStand
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class, stand -> {
            stand.setInvisible(true);
            stand.setGravity(false);
            stand.setMarker(true);
            stand.addScoreboardTag("randomBox");
            stand.getEquipment().setHelmet(head);
        });

        // Animar el ArmorStand (levitar y girar)
        animateArmorStand(armorStand, plugin);
    }

    private static void animateArmorStand(ArmorStand armorStand, PandoDungeons plugin) {
        new BukkitRunnable() {
            double angle = 0; // Ángulo inicial
            double heightOffset = 0; // Desplazamiento vertical
            boolean goingUp = true; // Dirección del movimiento vertical
            final Location initialLocation = armorStand.getLocation().clone(); // Clonar para mantener la posición inicial

            @Override
            public void run() {

                List<Entity> closeEntities = armorStand.getNearbyEntities(0.5,0.5,0.5);

                if(!closeEntities.isEmpty()){
                    if(closeEntities.getFirst() instanceof Player player){
                        armorStand.remove();
                        triggerEffect(player);
                        this.cancel();
                        return;
                    }
                }

                if (!armorStand.isValid()) {
                    this.cancel();
                    return; // Detener si el ArmorStand ya no existe
                }

                // Rotar el ArmorStand
                angle += 3; // Incrementar el ángulo
                if (angle >= 360) angle = 0;

                // Ajustar la rotación en el Location
                Location updatedLocation = initialLocation.clone();
                updatedLocation.setYaw((float) angle); // Cambiar el yaw para rotación horizontal

                // Levitar (subir y bajar)
                if (goingUp) {
                    heightOffset += 0.05;
                    if (heightOffset >= 0.5) goingUp = false;
                } else {
                    heightOffset -= 0.05;
                    if (heightOffset <= 0) goingUp = true;
                }
                updatedLocation.add(0, heightOffset, 0); // Ajustar altura

                // Teletransportar para aplicar rotación y levitación
                armorStand.teleport(updatedLocation);
            }
        }.runTaskTimer(plugin, 0, 2); // Ejecutar cada 2 ticks
    }



    public static void triggerEffect(Player player) {
        // Elegir un efecto aleatorio
        boolean positiveEffect = RANDOM.nextBoolean(); // 50% probabilidad de positivo o negativo

        if (positiveEffect) {
            applyPositiveEffect(player);
        } else {
            applyNegativeEffect(player);
        }
    }

    private static void applyPositiveEffect(Player player) {
        // Ejemplo: Añadir salud y efectos positivos
        player.setHealth(Math.min(player.getHealth() + 4.0, player.getMaxHealth())); // Restaurar salud
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0)); // Lentitud por 10 segundos
        player.sendMessage(ChatColor.GREEN + "¡Has recibido un efecto positivo!");
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 10);
    }

    private static void applyNegativeEffect(Player player) {
        // Ejemplo: Aplicar efectos negativos a otros jugadores en el mundo
        List<Player> players = player.getWorld().getPlayers();
        for (Player otherPlayer : players) {
            boolean positiveEffect = RANDOM.nextBoolean();
            if (otherPlayer.equals(player)) continue; // Saltar al jugador que activó el efecto
            if (positiveEffect) {
                otherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 2)); // Lentitud por 10 segundos
            } else {
                otherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 2)); // Lentitud por 10 segundos
            }

            otherPlayer.sendMessage(ChatColor.RED + "¡Has recibido un efecto negativo de la caja!");
        }
        player.sendMessage(ChatColor.RED + "¡Los demás jugadores han sido afectados negativamente!");
    }
}
