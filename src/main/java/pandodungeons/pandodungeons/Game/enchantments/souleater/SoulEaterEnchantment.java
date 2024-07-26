package pandodungeons.pandodungeons.Game.enchantments.souleater;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import pandodungeons.pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SoulEaterEnchantment {

    private static final JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    // URL de la textura de la mini cabeza
    private static final String TEXTURE_URL = "5d63fd3d50e2aabdf8da463b939c57b24d5fd20b519dfdb9ce84e96b0ee3239b";

    // Lore que identifica el encantamiento
    private static final String SOUL_EATER_LORE = ChatColor.AQUA + "Soul Eater";

    /**
     * Aplica el lore de Soul Eater a un ítem.
     * @param item El ítem al que se aplicará el encantamiento.
     */
    public static void applySoulEater(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Obtener el lore existente
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

            // Agregar el nuevo lore del encantamiento
            if (!lore.contains(SOUL_EATER_LORE)) {
                lore.add(SOUL_EATER_LORE);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
    }

    /**
     * Verifica si un ítem tiene el encantamiento Soul Eater.
     * @param item El ítem a verificar.
     * @return true si el ítem tiene el encantamiento, false en caso contrario.
     */
    public static boolean hasSoulEater(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            return Objects.requireNonNull(meta.getLore()).contains(SOUL_EATER_LORE);
        }

        return false;
    }
    /**
     * Crea una mini cabeza con una textura específica.
     * @param displayName El nombre que se mostrará en la mini cabeza.
     * @param textureUrl La URL de la textura para la mini cabeza.
     * @return Un ItemStack representando la mini cabeza.
     */
    private static ItemStack createHead(String displayName, String textureUrl) throws MalformedURLException {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // Crear perfil y texturas del jugador
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(new URL("https://textures.minecraft.net/texture/" + textureUrl)); // Establecer la textura de la piel del companion
        profile.setTextures(textures);

        meta.setPlayerProfile(profile);
        meta.setCustomModelData(420);
        meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.WHITE + ChatColor.BOLD + displayName); // Nombre del item
        head.setItemMeta(meta);
        return head;
    }


    /**
     * Maneja el efecto del encantamiento Soul Eater cuando el jugador ataca a otro jugador.
     * @param attacker El jugador que está atacando.
     * @param target El objetivo del ataque.
     */
    public static void handleSoulEaterEffect(Player attacker, LivingEntity target) throws MalformedURLException {
        plugin.getLogger().info("handling soul eater");
        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (hasSoulEater(weapon)) {

            // Mostrar partículas de almas
            showSoulParticles(target.getLocation());

            // Crear y mover la mini cabeza hacia el jugador
            ItemStack soulHead = createHead("Soul", TEXTURE_URL);

            moveSoulHeadToPlayer(target.getLocation(), attacker, soulHead);
        }else{
            plugin.getLogger().info("player doesnt have souleater");
        }
    }

    /**
     * Mueve la mini cabeza desde una ubicación de origen hacia la ubicación del jugador objetivo.
     * @param start La ubicación inicial de la mini cabeza.
     * @param attacker El jugador objetivo.
     * @param head El ItemStack representando la mini cabeza.
     */
    public static void moveSoulHeadToPlayer(Location start, Player attacker, ItemStack head) {
        start.setYaw(calculateYaw(start, attacker.getLocation()));
        start.setPitch(calculatePitch(start, attacker.getLocation()));
        ArmorStand soulHead = start.getWorld().spawn(start, ArmorStand.class, armorStand -> {
            armorStand.setVisible(false);
            armorStand.setSmall(true);
            armorStand.setMarker(true);
            armorStand.setHelmet(head);
        });

        new BukkitRunnable() {
            final double speed = 0.5;
            Location currentLocation = soulHead.getLocation();

            @Override
            public void run() {
                if (currentLocation.distance(attacker.getLocation()) < 0.5 || currentLocation.distance(attacker.getLocation()) > 100 || !currentLocation.getChunk().isLoaded()) {
                    soulHead.remove();
                    showSoulParticles(attacker.getLocation());
                    this.cancel();
                    return;
                }

                // Move the armor stand towards the player
                currentLocation.add(currentLocation.toVector().subtract(attacker.getLocation().toVector()).normalize().multiply(-speed));
                soulHead.teleport(currentLocation);

                // Make the armor stand look at the player
                makeArmorStandLookAtPlayer(soulHead, attacker);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public static void makeArmorStandLookAtPlayer(ArmorStand armorStand, Player player) {
        Location armorStandLocation = armorStand.getLocation();
        Location playerLocation = player.getLocation();
        // Calculate the yaw angle (rotation around the Y-axis)
        double yaw = calculateYaw(armorStandLocation, playerLocation);

        // Calculate the pitch angle (rotation around the X-axis)
        double pitch = calculatePitch(armorStandLocation, playerLocation);

        // Convert radians to degrees and set the head pose
        armorStand.setHeadPose(new EulerAngle(pitch, yaw, 0));
    }

    public static float calculateYaw(Location armorStandLocation, Location playerLocation){
        // Calculate the direction vector
        double deltaX = playerLocation.getX() - armorStandLocation.getX();

        double deltaZ = playerLocation.getZ() - armorStandLocation.getZ();

        // Calculate the yaw angle (rotation around the Y-axis)
        return (float) Math.atan2(-deltaX, deltaZ);
    }

    public static float calculatePitch(Location armorStandLocation, Location playerLocation){

        double deltaX = playerLocation.getX() - armorStandLocation.getX();
        double deltaY = playerLocation.getY() - armorStandLocation.getY();
        double deltaZ = playerLocation.getZ() - armorStandLocation.getZ();


        // Calculate the yaw angle (rotation around the Y-axis)
        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        return (float) Math.atan2(-deltaY, distanceXZ);

    }

    /**
     * Muestra partículas de almas en una ubicación dada.
     * @param location La ubicación donde se mostrarán las partículas.
     */
    private static void showSoulParticles(Location location) {
        location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, location, 30, 0.5, 0.5, 0.5, 0.1);
    }

    /**
     * Crea un libro encantado con el nombre del encantamiento Soul Eater en amarillo.
     * @return Un ItemStack representando el libro encantado.
     */
    public static ItemStack createSoulEaterEnchantedBook() {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Soul Eater");
            book.setItemMeta(meta);
        }

        return book;
    }

}
