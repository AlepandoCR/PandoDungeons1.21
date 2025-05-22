package pandoToros.game;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.util.*;

import static pandodungeons.Game.enchantments.souleater.SoulEaterEnchantment.createHead;

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

                List<Entity> closeEntities = armorStand.getNearbyEntities(1, 1, 1);

                if (!closeEntities.isEmpty()) {
                    if (closeEntities.getFirst() instanceof Player player) {
                        armorStand.remove();
                        triggerEffect(player);
                        this.cancel();
                        return;
                    }else if(closeEntities.getFirst() instanceof ArmorStand){
                        armorStand.remove();
                        this.cancel();
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
            ToroStatManager.getToroStatsManager(player).addUpgrade();
            applyPositiveEffect(player);
        } else {
            ToroStatManager.getToroStatsManager(player).addEffect();
            applyNegativeEffect(player);
        }
    }

    public static List<Player> RedPlayers = new ArrayList<>();

    public static void RedPlayer(Player player) {
        RedPlayers.add(player);

        // Crear la BossBar para mostrar el tiempo restante
        BossBar bossBar = Bukkit.createBossBar(
                "Tiempo restante: " + RIDE_TIME + " segundos",
                BarColor.RED,
                BarStyle.SEGMENTED_10
        );
        bossBar.addPlayer(player);

        // Crear un ArmorStand para la bandera
        ArmorStand bannerStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
        bannerStand.setVisible(false); // No mostrar el ArmorStand
        bannerStand.setMarker(true);  // No interactuable
        bannerStand.setSmall(true);   // Tamaño pequeño
        bannerStand.setGravity(false); // Sin gravedad
        bannerStand.setCustomNameVisible(false);

        // Asignar un banner rojo al ArmorStand
        ItemStack redBanner = new ItemStack(Material.RED_BANNER);
        bannerStand.getEquipment().setHelmet(redBanner);

        // Tarea para manejar el tiempo restante y actualizar la BossBar
        new BukkitRunnable() {
            int timer = 0;
            int sc = 10;

            @Override
            public void run() {
                if (!player.isOnline() || sc <= 0) {
                    unRedPlayer(player);
                    bossBar.removeAll(); // Remover la BossBar
                    bannerStand.remove(); // Eliminar el ArmorStand
                    cancel(); // Detener la tarea
                    return;
                }

                timer++;

                double progress = sc / (double) RIDE_TIME;

                // Actualizar la BossBar
                bossBar.setTitle("Tiempo restante: " + sc + " segundos");
                bossBar.setProgress(progress);

                // Sincronizar la posición y dirección del ArmorStand con el jugador
                Location playerLocation = player.getLocation();

                // Calcular la posición frente al jugador
                double distanceInFront = 0.7; // Ajusta esta distancia para mover el banner más adelante
                double radYaw = Math.toRadians(playerLocation.getYaw());
                double offsetX = -Math.sin(radYaw) * distanceInFront;
                double offsetZ = Math.cos(radYaw) * distanceInFront;

                Location bannerLocation = playerLocation.clone().add(offsetX, -0.9, offsetZ); // Ajustar altura con -0.9
                bannerStand.teleport(bannerLocation);

                // Orientar el ArmorStand hacia la dirección del jugador
                bannerStand.setRotation(playerLocation.getYaw(), 0); // Solo rotación horizontal

                // Generar una pequeña esfera de partículas sobre la cabeza del jugador
                double radius = 0.2; // Radio de la esfera
                int particleCount = 10; // Número de partículas por iteración
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1.0f); // Color rojo y tamaño de partícula

                Location particleCenter = playerLocation.clone().add(0, 2.3, 0); // Ajustar altura sobre la cabeza
                for (int i = 0; i < particleCount; i++) {
                    double angle = Math.random() * 2 * Math.PI;
                    double yOffset = (Math.random() * 2 - 1) * radius; // Altura aleatoria dentro del radio
                    double xOffset = Math.cos(angle) * Math.sqrt(radius * radius - yOffset * yOffset);
                    double zOffset = Math.sin(angle) * Math.sqrt(radius * radius - yOffset * yOffset);

                    Location particleLocation = particleCenter.clone().add(xOffset, yOffset, zOffset);
                    particleLocation.add(0,0.3,0);
                    player.getWorld().spawnParticle(Particle.DUST, particleLocation, 1, dustOptions);
                }

                // Decrementar el tiempo restante cada segundo
                if (timer % 20 == 0) {
                    sc--;
                }
            }
        }.runTaskTimer(plugin, 0, 1L); // Ejecutar cada tick (1/20 de segundo)

    }

    public static List<Player> protectedPlayers = new ArrayList<>();

    public static void protectPlayer(Player player){
        player.sendMessage(ChatColor.AQUA + "Haz recibido un escudo protector");
     protectedPlayers.add(player);
        new BukkitRunnable() {

            @Override
            public void run() {
                if(!isProtectedPlayer(player)){
                    this.cancel();
                    return;
                }
                createDustSphere(player, Color.AQUA,1,100);
            }

        }.runTaskTimer(plugin,0,1);
    }

    public static boolean isProtectedPlayer(Player player){
        return protectedPlayers.contains(player);
    }

    public static void unProtectPlayer(Player player){
        if(isProtectedPlayer(player)) protectedPlayers.remove(player);
    }

    public static void createDustSphere(Player player, Color color, double radius, int density) {
        // Centro de la esfera (posición del jugador)
        Location center = player.getLocation().add(0, 1, 0); // Añade altura para centrar en el torso/cabeza

        // Opciones de la partícula DUST
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1.0f); // Color celeste y tamaño normal

        // Generar partículas en la superficie de la esfera
        for (int i = 0; i < density; i++) {
            // Coordenadas aleatorias dentro de la esfera
            double theta = Math.random() * 2 * Math.PI; // Ángulo azimutal (horizontal)
            double phi = Math.acos(2 * Math.random() - 1); // Ángulo polar (vertical)

            // Calcular las coordenadas en 3D
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);

            // Posición de la partícula
            Location particleLocation = center.clone().add(x, y, z);

            // Spawn de la partícula
            player.getWorld().spawnParticle(Particle.DUST, particleLocation, 1, dustOptions);
        }
    }



    public static void RedPlayer(net.minecraft.world.entity.player.Player player){
        RedPlayers.add((Player) player.getBukkitEntity());
    }

    public static boolean isRedPlayer(Player player){
        return RedPlayers.contains(player);
    }

    public static boolean isRedPlayer(net.minecraft.world.entity.player.Player player){
        return RedPlayers.contains((Player) player.getBukkitEntity());
    }

    public static void unRedPlayer(Player player){
        if(isRedPlayer(player)) RedPlayers.remove(player);
    }

    public static void unRedPlayer(net.minecraft.world.entity.player.Player player){
        if(isRedPlayer(player)) RedPlayers.remove((Player) player.getBukkitEntity());
    }

    private static void applyPositiveEffect(Player player) {
        // Ejemplo: Añadir salud y efectos positivos
        int effect = RANDOM.nextInt(5);
        switch (effect){
            case 0:
                player.setHealth(Math.min(player.getHealth() + 4.0, player.getMaxHealth())); // Restaurar salud
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0)); // Lentitud por 10 segundos
                break;
            case 1:
                giveFishingRodWithCustomModelData(player);
                giveHorseRide(player);
                break;
            case 2:
                player.setFoodLevel(20);
                break;
            case 3:
                giveFishingRodWithCustomModelData(player);
                break;
            case 4:
                protectPlayer(player);
                break;
            default:
                break;
        }
        player.sendMessage(ChatColor.GREEN + "¡Has recibido un efecto positivo!");
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 10);
    }

    private static void applyNegativeEffect(Player player) {
        // Obtener todos los jugadores en el mundo del jugador que activó el efecto
        List<Player> players = player.getWorld().getPlayers();
        boolean RededPlayer = false;
        for (Player otherPlayer : players) {
            // Saltar al jugador que activó el efecto
            if (otherPlayer.equals(player)) continue;

            // Decidir aleatoriamente cuál efecto aplicar
            int effectType = RANDOM.nextInt(4); // 0: lentitud, 1: ceguera, 2: minar 3x3
            switch (effectType) {
                case 0:
                    otherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 2)); // Lentitud por 10 segundos
                    otherPlayer.sendMessage(ChatColor.RED + "¡Has sido ralentizado por el efecto negativo de la caja!");
                    break;

                case 1:
                    otherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 2)); // Ceguera por 15 segundos
                    otherPlayer.sendMessage(ChatColor.RED + "¡Has sido cegado por el efecto negativo de la caja!");
                    break;

                case 2:
                    mineAroundPlayer(otherPlayer); // Minar área 3x3
                    otherPlayer.sendMessage(ChatColor.RED + "¡Un efecto negativo ha destruido el área a tu alrededor!");
                    break;
                case 3:
                    if(!RededPlayer){
                        RedPlayer(otherPlayer);
                        otherPlayer.sendMessage(ChatColor.RED + "¡Haz sido marcado!");
                        RededPlayer = true;
                    }
                    break;
                default:
                    break;
            }
        }

        // Mensaje para el jugador que activó el efecto
        player.sendMessage(ChatColor.RED + "¡Has causado estragos con un efecto negativo!");
    }

    public static void giveFishingRodWithCustomModelData(Player player) {
        // Crear una caña de pescar
        ItemStack fishingRod = new ItemStack(Material.FISHING_ROD);

        // Obtener el meta del item (permite agregar propiedades como el CustomModelData)
        ItemMeta meta = fishingRod.getItemMeta();

        if (meta != null) {
            // Establecer el CustomModelData a 10
            meta.setCustomModelData(69);

            // Aplicar el ItemMeta con el CustomModelData
            fishingRod.setItemMeta(meta);
        }

        // Darle la caña de pescar al jugador
        player.getInventory().addItem(fishingRod);
        player.sendMessage("Te he dado una caña de pescar con el poder de atar al toro");
    }

    private static final int RIDE_TIME = 10; // Tiempo de duración en segundos

    /**
     * Coloca al jugador en un caballo y muestra una BossBar con el tiempo restante.
     * El caballo desaparece después de 10 segundos.
     *
     * @param player El jugador al que se le colocará el caballo.
     */
    public static void giveHorseRide(Player player) {
        // Crear un caballo y colocar al jugador sobre él
        Location playerLocation = player.getLocation();
        Horse horse = (Horse) player.getWorld().spawnEntity(playerLocation, EntityType.HORSE);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.setTamed(true);
        horse.setOwner(player);
        horse.setPassenger(player);  // Coloca al jugador sobre el caballo

        // Crear una BossBar para mostrar el tiempo restante
        BossBar bossBar = Bukkit.createBossBar(
                "Tiempo restante: " + RIDE_TIME + " segundos",
                BarColor.YELLOW,
                BarStyle.SEGMENTED_10);
        bossBar.addPlayer(player);  // Añadir al jugador a la BossBar

        // Hacer que la barra de jefe cuente hacia atrás cada segundo
        new BukkitRunnable() {
            int timeRemaining = RIDE_TIME;

            @Override
            public void run() {
                // Normalizar el progreso para que esté entre 0.0 y 1.0
                double progress = timeRemaining / (double) RIDE_TIME;

                // Actualizar la BossBar con el tiempo restante
                bossBar.setTitle("Tiempo restante: " + timeRemaining + " segundos");
                bossBar.setProgress(progress);  // Usar el valor normalizado

                // Decrementar el tiempo restante
                timeRemaining--;

                // Si el tiempo se ha agotado, quitar al caballo y la BossBar
                if (timeRemaining <= 0) {
                    horse.remove();  // Eliminar el caballo
                    bossBar.removeAll();  // Eliminar la BossBar del jugador
                    cancel();  // Detener la tarea
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Ejecuta cada segundo (20 ticks)
    }


    // Método para minar un área 3x3 alrededor de un jugador objetivo
    private static void mineAroundPlayer(Player target) {
        Location loc = target.getLocation();
        World world = target.getWorld();

        // Iterar en un área de 3x3 alrededor de la posición del jugador
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location blockLoc = loc.clone().add(x, y, z);
                    if (!blockLoc.getBlock().isEmpty()) { // Evitar bloques de aire
                        world.getBlockAt(blockLoc).setType(Material.AIR); // Convertir el bloque a aire
                    }
                }
            }
        }

        world.spawnParticle(Particle.EXPLOSION, loc, 5); // Efecto visual
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f); // Efecto sonoro
    }
}
