package pandodungeons.pandodungeons.CustomEntities.Ball;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static pandoToros.game.ArenaMaker.isRedondelWorld;
import static pandodungeons.pandodungeons.Utils.ItemUtils.soccerBall;

public class BallEventHandler implements Listener {

    private static final double MAX_DISTANCE = 2.0;
    private final HashMap<UUID, Double> chargeMap = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> actionBarRunnables = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> trajectoryRunnables = new HashMap<>();
    private final HashMap<UUID, Long> startTimes = new HashMap<>();
    private final JavaPlugin plugin;
    private static final long MAX_CHARGE = 3000L; // 3 segundos para carga completa

    public BallEventHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private LivingEntity findNearestBall(Player player) {
        List<Entity> nearbyEntities = player.getNearbyEntities(MAX_DISTANCE, MAX_DISTANCE, MAX_DISTANCE);
        LivingEntity nearestBall = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Entity entity : nearbyEntities) {
            if (entity.getScoreboardTags().contains("bolaFut") && entity instanceof LivingEntity) {
                double distance = player.getLocation().distance(entity.getLocation());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestBall = (LivingEntity) entity;
                }
            }
        }
        return nearestBall;
    }

    private void showTrajectory(Player player, double charge) {
        LivingEntity nearestBall = findNearestBall(player);
        if (nearestBall == null) return;

        double normalizedCharge = charge / MAX_CHARGE;
        double horizontalForce = normalizedCharge * 6.0;
        double verticalForce = normalizedCharge * 1.5;

        Vector direction = player.getLocation().getDirection().multiply(horizontalForce);
        direction.setY(direction.getY() + verticalForce);

        Vector startPosition = nearestBall.getLocation().toVector();
        Vector step = direction.clone().normalize().multiply(0.5); // Ajusta el tamaño del paso

        // Eliminar partículas anteriores para el jugador actual
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.getWorld().getEntities().stream()
                    .filter(entity -> entity.getScoreboardTags().contains("trajectoryParticle_" + player.getUniqueId()))
                    .forEach(Entity::remove);
        });

        // Define la opción de polvo con el color naranja
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.ORANGE, 1.0f);

        for (int i = 0; i < 6; i++) { // Ajusta el número de pasos si es necesario
            startPosition.add(step);
            player.getWorld().spawnParticle(Particle.DUST, startPosition.toLocation(player.getWorld()), 1, dustOptions); // Muestra las partículas en la trayectoria
        }
    }

    private void startTrajectoryTask(Player player) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!chargeMap.containsKey(player.getUniqueId())) {
                    this.cancel();
                    return;
                }

                double charge = chargeMap.get(player.getUniqueId());
                showTrajectory(player, charge);
            }
        };

        runnable.runTaskTimer(plugin, 0L, 1L); // Ejecutar cada 5 ticks
        trajectoryRunnables.put(player.getUniqueId(), runnable);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        LivingEntity nearestBall = findNearestBall(player);

        if (nearestBall != null) {
            if (event.isSneaking()) { // Verifica si el jugador está agachado (manteniendo el clic)
                if (!chargeMap.containsKey(player.getUniqueId())) {
                    chargeMap.put(player.getUniqueId(), 0.0); // Empieza la carga
                    startTimes.put(player.getUniqueId(), System.currentTimeMillis()); // Guarda el tiempo de inicio
                    handleChargeBar(player);
                    startTrajectoryTask(player); // Inicia la tarea de actualización de la trayectoria
                }
            } else { // Si el jugador deja de agacharse (suelta el clic)
                if (chargeMap.containsKey(player.getUniqueId())) {
                    double charge = chargeMap.get(player.getUniqueId());

                    if (charge > 0) {
                        if (nearestBall != null) {
                            // Ajuste de fuerza según la carga
                            double normalizedCharge = charge / MAX_CHARGE;
                            double horizontalForce = normalizedCharge * 6.0; // Aumenta la fuerza horizontal máxima
                            double verticalForce = normalizedCharge * 1.5; // Aumenta la fuerza vertical máxima

                            Vector kickDirection = player.getLocation().getDirection().multiply(horizontalForce);
                            kickDirection.setY(kickDirection.getY() + verticalForce); // Ajuste para levantar la bola

                            nearestBall.setVelocity(kickDirection);
                        }

                        // Mostrar la trayectoria
                        showTrajectory(player, charge);
                    }

                    // Resetea la carga
                    chargeMap.remove(player.getUniqueId());
                    startTimes.remove(player.getUniqueId());
                    if (actionBarRunnables.containsKey(player.getUniqueId())) {
                        actionBarRunnables.get(player.getUniqueId()).cancel();
                        actionBarRunnables.remove(player.getUniqueId());
                    }
                    if (trajectoryRunnables.containsKey(player.getUniqueId())) {
                        trajectoryRunnables.get(player.getUniqueId()).cancel();
                        trajectoryRunnables.remove(player.getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (entity.getScoreboardTags().contains("bolaFut")) {
                if (event.getDamageSource().getDamageType().equals(DamageType.FALL)) {
                    event.setCancelled(true); // Cancela el daño si la entidad es una "bolaFut"
                }
            }
        }
    }

    @EventHandler
    public void clickArmadillo(PlayerInteractEntityEvent event){
        if(event.getRightClicked().getScoreboardTags().contains("bolaFut") && !isRedondelWorld(event.getRightClicked().getWorld().getName())){
            event.getRightClicked().remove();
            event.getRightClicked().getWorld().dropItemNaturally(event.getRightClicked().getLocation(), soccerBall(1));
        }
    }

    private void handleChargeBar(Player player) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!chargeMap.containsKey(player.getUniqueId())) {
                    this.cancel();
                    return; // Cancela la tarea si la carga se detiene
                }

                Long startTime = startTimes.get(player.getUniqueId());
                if (startTime == null) {
                    // Si no hay tiempo de inicio, cancela la tarea
                    this.cancel();
                    return;
                }

                long currentTime = System.currentTimeMillis();
                long chargeTime = currentTime - startTime;
                double charge = Math.min(chargeTime, MAX_CHARGE);

                // Actualiza la carga en el mapa
                chargeMap.put(player.getUniqueId(), charge);

                // Muestra la carga en la barra de acción
                int progressBars = (int) ((charge / (double) MAX_CHARGE) * 10);
                String progressBar = (ChatColor.GREEN + "▊").repeat(progressBars) + (ChatColor.RED.toString() + "▬").repeat(10 - progressBars);
                player.sendActionBar(ChatColor.GOLD + "[" + ChatColor.RESET + progressBar + ChatColor.RESET + ChatColor.GOLD + "]");

                if (charge >= MAX_CHARGE) {
                    this.cancel();
                    actionBarRunnables.remove(player.getUniqueId());
                }
            }
        };

        runnable.runTaskTimer(plugin, 0L, 1L); // Ajusta el intervalo si es necesario
        actionBarRunnables.put(player.getUniqueId(), runnable);
        startTimes.put(player.getUniqueId(), System.currentTimeMillis()); // Asegúrate de guardar el tiempo de inicio
    }
}
