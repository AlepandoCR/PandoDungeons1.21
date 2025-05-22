package pandodungeons.bossfights.fights;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pandodungeons.PandoDungeons;
import pandodungeons.bossfights.bossEntities.magmaCube.entities.CubitoBoom;

public class CubitoBoomFight {
    private boolean stopFight;
    private final JavaPlugin plugin;
    private BossBar magmaCubeBossHealthBar;
    private final Location location;

    public CubitoBoomFight(Location location) {
        this.location = location;
        plugin = JavaPlugin.getPlugin(PandoDungeons.class);
        if (magmaCubeBossHealthBar == null) {
            magmaCubeBossHealthBar = Bukkit.createBossBar(
                    ChatColor.DARK_RED + "Cubo de Magma Gigante",
                    BarColor.RED,
                    BarStyle.SOLID
            );
        }
        for (Player player : location.getWorld().getPlayers()) {
            magmaCubeBossHealthBar.addPlayer(player);
        }
    }

    public void startMagmaCubeBossFight() {
        BukkitRunnable fight = new BukkitRunnable() {
            final CubitoBoom magmaCubeBoss = new CubitoBoom(plugin, location);
            int fireballThrowTicks = 0;
            int groundSlamTicks = 0;
            int lavaGenerationTicks = 0;
            int sizeIncreaseTicks = 0;

            @Override
            public void run() {
                if (location.getWorld() == null) {
                    magmaCubeBossHealthBar.removeAll();
                    StopFight();
                    cancel();
                    return;
                }
                if (magmaCubeBoss.getMagmaCube() == null) {
                    magmaCubeBossHealthBar.removeAll();
                    cancel();
                    return;
                }
                Location magmaCubeLocation = magmaCubeBoss.getMagmaCube().getLocation();
                if (magmaCubeBoss.getMagmaCube().isDead()) {
                    sendCongrats(magmaCubeBoss);
                    StopFight();
                    cancel();
                    magmaCubeBoss.getMagmaCube().remove();
                    return;
                }

                fireballThrowTicks++;
                groundSlamTicks++;
                lavaGenerationTicks++;
                sizeIncreaseTicks++;

                if (fireballThrowTicks >= 400) { // Lanzar bola de fuego cada 20 segundos
                    throwFireball(magmaCubeBoss);
                    fireballThrowTicks = 0;
                }

                if (groundSlamTicks >= 600) { // Salto aplastante cada 30 segundos
                    groundSlam(magmaCubeBoss);
                    groundSlamTicks = 0;
                }

                if (lavaGenerationTicks >= 800) { // Generar lava cada 40 segundos
                    generateLava(magmaCubeBoss);
                    lavaGenerationTicks = 0;
                }

                if (sizeIncreaseTicks >= 1000) { // Incrementar tamaño y explotar cada 50 segundos
                    sizeIncreaseAndExplode(magmaCubeBoss);
                    sizeIncreaseTicks = 0;
                }

                magmaCubeBoss.setMagmaCubeTarget(magmaCubeLocation);
                // Actualizar BossBar con la vida del Magma Cube
                bossBarManagement(magmaCubeBoss);
            }
        };

        if (!stopFight) {
            fight.runTaskTimer(plugin, 0, 1);
        }
    }


    private void throwFireball(CubitoBoom magmaCubeBoss) {
        // Obtener el objetivo del Magma Cube (en este caso, un jugador)
        if (magmaCubeBoss.getMagmaCube().getTarget() instanceof Player target) {

            // Obtener la ubicación del Magma Cube y del objetivo
            Location magmaCubeLocation = magmaCubeBoss.getMagmaCube().getLocation();
            Location targetLocation = target.getLocation();

            // Calcular la dirección hacia el objetivo
            Vector direction = targetLocation.toVector().subtract(magmaCubeLocation.toVector()).normalize();

            // Lanzar la bola de fuego en la dirección calculada
            SmallFireball fireball = magmaCubeBoss.getMagmaCube().launchProjectile(SmallFireball.class);
            fireball.setVelocity(direction.multiply(0.8)); // Ajustar la velocidad de la bola de fuego
            fireball.setIsIncendiary(true); // Hace que la bola de fuego incendie al impacto
            fireball.setYield(2.0f); // Ajustar el radio de explosión
        }
    }

    private void groundSlam(CubitoBoom magmaCubeBoss) {
        MagmaCube magmaCube = magmaCubeBoss.getMagmaCube();

        // Simulamos un salto aplicando velocidad hacia arriba
        magmaCube.setVelocity(new Vector(0, 1, 0));

        // Esperamos un corto tiempo antes de causar el daño por la caída
        new BukkitRunnable() {
            @Override
            public void run() {
                // Obtener la ubicación del Magma Cube al aterrizar
                Location slamLocation = magmaCube.getLocation();

                // Definimos el radio de daño
                double radius = 5.0;

                // Dañamos a los jugadores cercanos
                for (Player player : slamLocation.getWorld().getPlayers()) {
                    if (player.getLocation().distance(slamLocation) <= radius) {
                        player.damage(5.0, magmaCube); // Daño causado
                        player.sendMessage(ChatColor.RED + "¡El Cubo de Magma te ha aplastado!");
                    }
                }

                // Opcionalmente, crear un efecto visual (por ejemplo, partículas)
                slamLocation.getWorld().createExplosion(slamLocation, 0F, false, false);

            }
        }.runTaskLater(plugin, 20); // 1 segundo después del salto
    }


    private void generateLava(CubitoBoom magmaCubeBoss) {
        MagmaCube magmaCube = magmaCubeBoss.getMagmaCube();
        Location origin = magmaCube.getLocation();
        World world = origin.getWorld();

        // Radio de los charcos de lava
        int radius = 5;

        // Duración en ticks (20 ticks = 1 segundo) de los charcos de lava
        int lavaDurationTicks = 100; // 5 segundos

        // Crear charcos de lava alrededor del Magma Cube
        for (int i = 0; i < 8; i++) {
            double angle = i * (Math.PI / 4); // 8 charcos distribuidos uniformemente en el círculo
            double xOffset = radius * Math.cos(angle);
            double zOffset = radius * Math.sin(angle);
            Location lavaLocation = origin.clone().add(xOffset, 0, zOffset);

            // Crear un Armor Stand invisible para representar el charco de lava
            ArmorStand lavaPool = world.spawn(lavaLocation, ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setGravity(false);
                stand.setMarker(true); // Usamos marker para evitar colisiones
                stand.setInvulnerable(true);
            });

            // Crear efecto visual de partículas de lava alrededor del Armor Stand
            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks >= lavaDurationTicks) {
                        lavaPool.remove();
                        cancel();
                        return;
                    }

                    // Mostrar partículas de lava
                    world.spawnParticle(Particle.LAVA, lavaLocation.clone().add(0, 0.5, 0), 10, 0.5, 0.5, 0.5, 0.01);

                    // Aplicar daño a jugadores que se encuentren dentro del charco de lava
                    for (Player player : world.getPlayers()) {
                        if (player.getLocation().distance(lavaLocation) <= 1.5) {
                            player.damage(4); // Daño moderado
                        }
                    }

                    ticks++;
                }
            }.runTaskTimer(magmaCubeBoss.getPlugin(), 0, 5); // Ejecutar cada 5 ticks (0.25 segundos)
        }
    }


    private void sizeIncreaseAndExplode(CubitoBoom magmaCubeBoss) {
        BukkitRunnable sizeIncreaseTask = new BukkitRunnable() {
            private int ticks = 0;

            @Override
            public void run() {
                if (ticks < 200) { // Incrementar tamaño durante 10 segundos (200 ticks)
                    int newSize = Math.min(10, magmaCubeBoss.getMagmaCube().getSize() + 1);
                    magmaCubeBoss.getMagmaCube().setSize(newSize);
                } else if (ticks >= 200 && ticks < 260) { // Oscilar entre tamaño 10 y 8 durante 3 segundos (60 ticks)
                    if (ticks % 20 == 0) {
                        magmaCubeBoss.getMagmaCube().setSize(10);
                    } else if (ticks % 10 == 0) {
                        magmaCubeBoss.getMagmaCube().setSize(8);
                    }
                } else if (ticks >= 260) { // Explosión y división en mini Magma Cubes
                    explodeAndSplit(magmaCubeBoss);
                    cancel();
                }
                ticks++;
            }
        };
        sizeIncreaseTask.runTaskTimer(plugin, 0, 1);
    }

    private void explodeAndSplit(CubitoBoom magmaCubeBoss) {
        MagmaCube magmaCube = magmaCubeBoss.getMagmaCube();
        Location location = magmaCube.getLocation();
        World world = location.getWorld();

        // Número de mini Magma Cubes a generar
        int miniCubesCount = 4;

        // Generar mini Magma Cubes en la ubicación de la explosión
        for (int i = 0; i < miniCubesCount; i++) {
            world.spawn(location, MagmaCube.class, miniCube -> {
                miniCube.setSize(1); // Establecer el tamaño pequeño del mini Magma Cube
                miniCube.addScoreboardTag("miniMagmaCube");

                // Establecer una pequeña cantidad de vida para los mini Magma Cubes
                miniCube.setMaxHealth(10);
                miniCube.setHealth(10);

                // Opcional: Añadir efectos visuales o de sonido
                miniCube.getWorld().playSound(miniCube.getLocation(), Sound.ENTITY_MAGMA_CUBE_JUMP, 1.0F, 1.0F);
            });
        }

        // Mensaje de alerta a los jugadores cercanos
        for (Player player : world.getPlayers()) {
            if (player.getLocation().distance(location) <= 15) {
                player.sendMessage(ChatColor.RED + "¡El Cubo de Magma explotó en mini Cubos!");
            }
        }
    }


    private void bossBarManagement(CubitoBoom magmaCubeBoss) {
        double health = magmaCubeBoss.getMagmaCube().getHealth();
        double maxHealth = magmaCubeBoss.getMagmaCube().getMaxHealth();

        double progress = health / maxHealth;
        progress = Math.max(0.0, Math.min(1.0, progress));

        magmaCubeBossHealthBar.setProgress(progress);
    }

    public void StopFight() {
        stopFight = true;
        magmaCubeBossHealthBar.removeAll();
    }

    public void sendCongrats(CubitoBoom magmaCubeBoss) {
        for (Player player : magmaCubeBoss.getMagmaCube().getWorld().getPlayers()) {
            player.sendMessage(ChatColor.DARK_RED + "¡Has derrotado al Cubo de Magma Gigante!");
        }
    }
}
