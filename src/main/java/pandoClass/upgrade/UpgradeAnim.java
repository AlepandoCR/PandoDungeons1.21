package pandoClass.upgrade;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pandodungeons.PandoDungeons;

public class UpgradeAnim {

    private final Location TARGET_LOCATION = new Location(Bukkit.getWorld("spawn"), -17.5, 62.5, 401.5);
    private static final double MAX_DISTANCE = 100.0; // Distancia máxima permitida entre el jugador y las coordenadas objetivo
    private static final double SPEED = 1.7; // Velocidad de movimiento en bloques por segundo

    private final PandoDungeons plugin;
    private final Player player;

    private final Villager villager;

    private final Villager otherVillager;

    public UpgradeAnim(PandoDungeons plugin, Player player, Location eventLocation, Villager otherVillager) {
        this.plugin = plugin;
        this.player = player;
        this.villager = createVillager(TARGET_LOCATION.getWorld(),eventLocation);
        this.otherVillager = otherVillager;
        player.hideEntity(plugin,otherVillager);
    }

    private Villager createVillager(World world, Location spawnLocation){
        // Generar el aldeano en la ubicación inicial
        Villager villager = (Villager) world.spawnEntity(spawnLocation, EntityType.VILLAGER);

        villager.setVillagerType(Villager.Type.SNOW);
        villager.setProfession(Villager.Profession.ARMORER);

        return villager;
    }


    public void spawnAndMoveVillager(Player player) {
        Location targetLocation = TARGET_LOCATION;

        targetLocation.subtract(0,2.5,0);

        // Ocultar al aldeano para otros jugadores
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                onlinePlayer.hideEntity(plugin, villager);
            }
        }

        // Crear una tarea repetitiva para mover al aldeano hacia la ubicación objetivo
        new BukkitRunnable() {
            @Override
            public void run() {
                // Verificar si el aldeano ha llegado a la ubicación objetivo
                if (!villager.isValid()) {
                    player.showEntity(plugin,otherVillager);
                    villager.remove();
                    this.cancel(); // Detener la tarea
                    return;
                }

                // Calcular la dirección hacia la ubicación objetivo
                Location currentLocation = villager.getLocation();
                double deltaX = targetLocation.getX() - currentLocation.getX();
                double deltaY = targetLocation.getY() - currentLocation.getY();
                double deltaZ = targetLocation.getZ() - currentLocation.getZ();
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                // Normalizar la dirección y establecer la velocidad de movimiento
                double speed = 0.07; // Velocidad de movimiento del aldeano
                double velocityX = (deltaX / distance) * speed;
                double velocityY = (deltaY / distance) * speed;
                double velocityZ = (deltaZ / distance) * speed;

                // Aplicar la velocidad al aldeano
                villager.setVelocity(new Vector(velocityX, velocityY, velocityZ));

                // Establecer la orientación del aldeano hacia la ubicación objetivo
                float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90;
                float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)));
                villager.setRotation(yaw, pitch);
            }
        }.runTaskTimer(plugin, 0L, 1L); // Ejecutar la tarea cada tick (20 ticks por segundo)
    }




    public void animateItem(ItemStack itemStack) {
        if (player == null || itemStack == null) return;
        spawnAndMoveVillager(player);


        // Verificar si el jugador está en el mismo mundo que las coordenadas objetivo
        if (!player.getWorld().equals(TARGET_LOCATION.getWorld())) {
            player.sendMessage(ChatColor.RED + "No estás en el mismo mundo que las coordenadas objetivo.");
            return;
        }

        // Verificar la distancia entre el jugador y las coordenadas objetivo
        if (player.getLocation().distance(TARGET_LOCATION) > MAX_DISTANCE) {
            player.sendMessage(ChatColor.RED + "Estás demasiado lejos de las coordenadas objetivo.");
            return;
        }

        // Crear una copia del ItemStack y asignarla a un ArmorStand invisible e indestructible
        ItemStack itemCopy = itemStack.clone();
        ArmorStand armorStand = spawnArmorStand(player.getLocation(), itemCopy);

        // Mantener el ArmorStand frente al jugador durante 1.5 segundos
        new BukkitRunnable() {
            long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                if (!player.isOnline() || armorStand.isDead() || !armorStand.isValid()) {
                    armorStand.remove();
                    player.showEntity(plugin,otherVillager);
                    cancel();
                    return;
                }

                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime >= 3000) {
                    // Iniciar el movimiento hacia las coordenadas objetivo
                    moveArmorStand(armorStand, () -> {
                        // Esperar 2 segundos en la ubicación objetivo
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                // Generar la explosión de fuegos artificiales
                                spawnFireworkExplosion(TARGET_LOCATION);
                                villager.remove();
                                player.showEntity(plugin, otherVillager);
                                // Iniciar el regreso al jugador
                                returnToPlayer(armorStand, player);
                            }
                        }.runTaskLater(plugin, 40L); // 2 segundos de espera
                    });
                    cancel();
                    return;
                }

                // Actualizar la posición del ArmorStand frente al jugador
                Location frontLocation = getFrontLocation(player, 1.2);
                armorStand.teleport(frontLocation);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private ArmorStand spawnArmorStand(Location location, ItemStack item) {
        World world = location.getWorld();
        if (world == null) return null;

        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setGlowing(true);
        armorStand.setGravity(false);
        armorStand.setMarker(true);
        armorStand.getEquipment().setItemInMainHand(item);
        return armorStand;
    }

    private Location getFrontLocation(Player player, double distance) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();
        return eyeLocation.add(direction.multiply(distance)).add(0.5,-1,0.5);
    }

    private void moveArmorStand(ArmorStand armorStand, Runnable onComplete) {

        Location targetLocation = TARGET_LOCATION;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (armorStand.isDead()) {
                    cancel();
                    return;
                }



                Location currentLocation = armorStand.getLocation();
                Vector direction = targetLocation.toVector().subtract(currentLocation.toVector()).normalize();
                armorStand.teleport(currentLocation.add(direction.multiply(UpgradeAnim.SPEED / 20.0)));

                if (currentLocation.distance(TARGET_LOCATION) <= UpgradeAnim.SPEED / 20.0) {
                    armorStand.teleport(TARGET_LOCATION);
                    onComplete.run();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Crea y detona un Firework con efecto personalizado en la ubicación dada.
     * Esta explosión de fireworks sirve como efecto visual sin causar daños.
     *
     * @param center         La ubicación donde se crea la explosión.
     */
    private void spawnFireworkExplosion(Location center) {
        World world = center.getWorld();
        // Spawnea un Firework en la ubicación dada
        Firework firework = world.spawn(center, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        // Crea un efecto de Firework con el color deseado, con fade igual, trail y flicker activados.
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.ORANGE)
                .withFade(Color.MAROON)
                .trail(true)
                .flicker(true)
                .build();
        meta.addEffect(effect);
        meta.setPower(0); // Poder 0 para que se detone rápidamente
        firework.setFireworkMeta(meta);
        // Detonar inmediatamente el Firework para simular la explosión.
        firework.detonate();
    }


    private void returnToPlayer(ArmorStand armorStand, Player player) {
        new BukkitRunnable() {
            long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                if (!player.isOnline() || armorStand.isDead()) {
                    armorStand.remove();
                    player.showEntity(plugin,otherVillager);
                    cancel();
                    return;
                }

                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime >= 2000) {
                    armorStand.remove();
                    cancel();
                    return;
                }

                // Actualizar la posición del ArmorStand frente al jugador
                Location frontLocation = getFrontLocation(player, 2.0);
                armorStand.teleport(frontLocation);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
