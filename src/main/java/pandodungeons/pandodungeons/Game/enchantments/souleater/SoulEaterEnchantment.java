package pandodungeons.pandodungeons.Game.enchantments.souleater;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.checkerframework.checker.units.qual.N;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.bossfights.bossEntities.queenBee.attacks.NectarBombAttack;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static pandodungeons.pandodungeons.Utils.ParticleUtils.spawnHeartParticleCircle;

public class SoulEaterEnchantment {

    private static final JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    // URL de la textura de la mini cabeza
    private static final String TEXTURE_URL = "5d63fd3d50e2aabdf8da463b939c57b24d5fd20b519dfdb9ce84e96b0ee3239b";

    // Lore que identifica el encantamiento
    private static final String SOUL_EATER_LORE = "SoulEater";

    private static final NamespacedKey SOUL_COUNT_KEY = new NamespacedKey(plugin, "soul_count");

    /**
     * Aplica el lore de Soul Eater a un ítem.
     * @param item El ítem al que se aplicará el encantamiento.
     */
    public static void applySoulEater(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || item.getType() == Material.MACE) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!hasSoulEater(item)) {
            // Obtener el lore existente
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

            // Agregar el nuevo lore del encantamiento
            assert lore != null;
            if (!lore.contains(SOUL_EATER_LORE)) {
                lore.add(ChatColor.DARK_AQUA + SOUL_EATER_LORE);
                lore.add(ChatColor.AQUA.toString() + "Almas: " + ChatColor.YELLOW + "0/1k");
                meta.setLore(lore);
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, SOUL_EATER_LORE), PersistentDataType.STRING, SOUL_EATER_LORE);
                meta.getPersistentDataContainer().set(SOUL_COUNT_KEY, PersistentDataType.INTEGER, 0);
                item.setItemMeta(meta);
            }
        }else{
            //Quiza mejorar el enchant
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
            String loreValue = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, SOUL_EATER_LORE), PersistentDataType.STRING);
            return loreValue != null && loreValue.contains(SOUL_EATER_LORE);
        }

        return false;
    }

    /**
     * Crea una mini cabeza con una textura específica.
     * @param displayName El nombre que se mostrará en la mini cabeza.
     * @param textureUrl La URL de la textura para la mini cabeza.
     * @return Un ItemStack representando la mini cabeza.
     */
    public static ItemStack createHead(String displayName, String textureUrl) throws MalformedURLException {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // Crear perfil y texturas del jugador
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(new URL("https://textures.minecraft.net/texture/" + textureUrl)); // Establecer la textura de la piel del companion
        profile.setTextures(textures);

        meta.setPlayerProfile(profile);
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "soul"), PersistentDataType.STRING, "soul");
        meta.setCustomModelData(420);
        meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.WHITE + ChatColor.BOLD + displayName); // Nombre del item
        head.setItemMeta(meta);
        return head;
    }

    /**
     * Añade un alma al ítem y actualiza el lore.
     * @param item El ítem al que se le añadirá un alma.
     */
    public static void addSoul(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            int soulCount = getSoulCount(item) + 1;
            meta.getPersistentDataContainer().set(SOUL_COUNT_KEY, PersistentDataType.INTEGER, soulCount);

            // Actualizar el lore con la cantidad de almas
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            assert lore != null;
            boolean found = false;
            for (int i = 0; i < lore.size(); i++) {
                if (lore.get(i).startsWith(ChatColor.AQUA.toString() + "Almas: ")) {
                    lore.set(i, ChatColor.AQUA.toString() + "Almas: " + ChatColor.YELLOW + soulCount + "/1k");
                    found = true;
                    break;
                }
            }
            if (!found) {
                lore.add(ChatColor.AQUA.toString() + "Almas: " + ChatColor.YELLOW + soulCount + "/1k");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }


    /**
     * Reduce la cantidad de almas en el ítem y actualiza el lore.
     * @param item El ítem al que se le reducirán las almas.
     * @param toReduce La cantidad de almas a reducir.
     */
    public static void reduceSouls(ItemStack item, int toReduce) {
        if (item == null || item.getType() == Material.AIR || !hasSoulEater(item)) { // Verificar que el ítem tenga Soul Eater
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            int soulCount = getSoulCount(item) - toReduce;
            if (soulCount < 0) {
                soulCount = 0;
            }
            meta.getPersistentDataContainer().set(SOUL_COUNT_KEY, PersistentDataType.INTEGER, soulCount);

            // Actualizar el lore con la cantidad de almas
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            assert lore != null;
            boolean found = false;
            for (int i = 0; i < lore.size(); i++) {
                if (lore.get(i).startsWith(ChatColor.AQUA.toString() + "Almas: ")) {
                    lore.set(i, ChatColor.AQUA.toString() + "Almas: " + ChatColor.YELLOW + soulCount + "/1k");
                    found = true;
                    break;
                }
            }
            if (!found) {
                lore.add(ChatColor.AQUA.toString() + "Almas: " + ChatColor.YELLOW + soulCount + "/1k");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    public static void healAbility(Player player, ItemStack item){
        if(getSoulCount(item) >= 100){
            player.setHealth(player.getMaxHealth());
            reduceSouls(item,50);
            spawnHeartParticleCircle(player.getLocation(), 1, 10);
        }else{
            player.sendMessage(ChatColor.DARK_RED + "No tienes almas suficientes aun");
        }
    }

    public static void freezeAbility(Player player, ItemStack item){
        if(getSoulCount(item) >= 200){
            List<Entity> entities = player.getNearbyEntities(5,5,5);
            for(Entity entity : entities){
                if(!(entity instanceof Player)){
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100,10));
                }
            }
            reduceSouls(item,100);
        }else{
            player.sendMessage(ChatColor.DARK_RED + "No tienes almas suficientes aun");
        }
    }

    /**
     * Obtiene la cantidad de almas del ítem.
     * @param item El ítem del cual se obtendrá la cantidad de almas.
     * @return La cantidad de almas.
     */
    public static int getSoulCount(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !hasSoulEater(item)) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            return meta.getPersistentDataContainer().getOrDefault(SOUL_COUNT_KEY, PersistentDataType.INTEGER, 0);
        }

        return 0;
    }

    /**
     * Maneja el efecto del encantamiento Soul Eater cuando el jugador ataca a otro jugador.
     * @param attacker El jugador que está atacando.
     * @param target El objetivo del ataque.
     */
    public static void handleSoulEaterEffect(Player attacker, LivingEntity target) throws MalformedURLException {
        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (hasSoulEater(weapon)) {

            // Mostrar partículas de almas
            showSoulParticles(target.getLocation());

            // Crear y mover la mini cabeza hacia el jugador
            ItemStack soulHead = createHead("Soul", TEXTURE_URL);

            moveSoulHeadToPlayer(target.getLocation(), attacker, soulHead);
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
            final ItemStack item = attacker.getItemInHand();
            final double speed = 0.5;
            Location currentLocation = soulHead.getLocation();

            @Override
            public void run() {
                if (currentLocation.distance(attacker.getLocation()) < 0.5 || currentLocation.distance(attacker.getLocation()) > 100 || !currentLocation.getChunk().isLoaded()) {
                    soulHead.remove();
                    showSoulParticles(attacker.getLocation());

                    // Agregar un alma al arma del atacante
                    addSoul(item);

                    this.cancel();
                    return;
                }

                // Mover el Armor Stand hacia el jugador
                currentLocation.add(currentLocation.toVector().subtract(attacker.getLocation().toVector()).normalize().multiply(-speed));
                soulHead.teleport(currentLocation);

                // Hacer que el Armor Stand mire hacia el jugador
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
        // Actualizar el lore con la cantidad de almas
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Libro Encantado");
            lore.add(ChatColor.RESET.toString() + ChatColor.GRAY + "Soul Eater");
            meta.setLore(lore);
            book.setItemMeta(meta);
        }

        return book;
    }

}
