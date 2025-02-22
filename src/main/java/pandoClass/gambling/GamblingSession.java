package pandoClass.gambling;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.net.MalformedURLException;
import java.util.*;

import static pandodungeons.pandodungeons.Game.enchantments.souleater.SoulEaterEnchantment.createHead;

public class GamblingSession {

    private static final int HORSE_COUNT = 3;
    // Línea de partida y meta que se definen en el constructor
    private final Location startLine;
    private final Location finishLine;

    // Mapeo de cada caballo (id 1 a 5) con su ArmorStand
    private final Map<Integer, ArmorStand> horses = new HashMap<>();
    // Registro de apuestas para cada caballo
    private final Map<Integer, List<Bet>> bets = new HashMap<>();
    // Velocidades únicas asignadas a cada caballo
    private final Map<Integer, Double> horseVelocities = new HashMap<>();

    private boolean raceStarted = false;
    private final JavaPlugin plugin;

    // Dirección de la carrera (calculada de startLine a finishLine)
    private final Vector raceDirection;
    // Vector "derecho" para calcular el offset lateral (producto cruzado con el vector up)
    private final Vector rightVector;
    // Distancia lateral entre caballos
    private final double offsetSpacing = 2.0;

    public GamblingSession(JavaPlugin plugin, Location startLine, Location finishLine) throws MalformedURLException {
        this.plugin = plugin;
        this.startLine = startLine;
        this.finishLine = finishLine;

        // Calcula la dirección principal de la carrera
        this.raceDirection = finishLine.toVector().subtract(startLine.toVector()).normalize();
        // Calcula el vector perpendicular (a la derecha) usando el vector up (0,1,0)
        this.rightVector = raceDirection.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        // Se instancian los 5 caballos con su offset correspondiente
        for (int i = 1; i <= HORSE_COUNT; i++) {
            ArmorStand horse = spawnHorse(i);
            horses.put(i, horse);
            bets.put(i, new ArrayList<>());
        }
    }

    // Método para spawnear un caballo (armorstand) con el offset lateral
    private ArmorStand spawnHorse(int horseId) throws MalformedURLException {

        int offsetIndex = horseId - 1;
        Vector offset = rightVector.clone().multiply(offsetSpacing * offsetIndex);
        Location spawnLoc = startLine.clone().add(offset);
        ItemStack head = createHead("horse" + horseId, "265e72fa2d42e7871c4deac117bb9b9e4161c5b3d71f662e037c071bb066e42a");
        return spawnArmorStand(spawnLoc, head, "Horse" + horseId);
    }

    // Método auxiliar para spawnear un ArmorStand customizado
    private ArmorStand spawnArmorStand(Location loc, ItemStack head, String tag) {
        ArmorStand as = loc.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            armorStand.setInvulnerable(true);
            armorStand.setRemoveWhenFarAway(false);
            // Evita colisiones para que se mueva a través de bloques
            armorStand.setCollidable(false);
            armorStand.getEquipment().setHelmet(head);
            armorStand.addScoreboardTag(tag);
        });
        return as;
    }

    // Registro de una apuesta
    public void addBet(Player player, int horseId, double amount) {
        if (!bets.containsKey(horseId)) return;
        bets.get(horseId).add(new Bet(player, amount));
        Bukkit.broadcastMessage(player.getName() + " apostó " + amount + " en el caballo " + horseId);

        // Inicia la carrera cuando cada caballo tenga al menos una apuesta
        if (!raceStarted && allHorsesHaveBet()) {
            startRace();
        }
    }

    // Verifica si todos los caballos tienen al menos una apuesta
    private boolean allHorsesHaveBet() {
        for (int id = 1; id <= HORSE_COUNT; id++) {
            if (bets.get(id).isEmpty()) return false;
        }
        return true;
    }

    // Inicia la carrera: asigna velocidades únicas y mueve los caballos
    public void startRace() {
        raceStarted = true;
        assignUniqueVelocities();
        Bukkit.broadcastMessage("¡La carrera ha comenzado!");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Integer, ArmorStand> entry : horses.entrySet()) {
                    int id = entry.getKey();
                    ArmorStand horse = entry.getValue();
                    double speed = horseVelocities.get(id);

                    // Mover el caballo mediante teleportación para ignorar colisiones
                    Location current = horse.getLocation();
                    Location next = current.clone().add(raceDirection.clone().multiply(speed));
                    // Actualizar la rotación para que mire en la dirección de carrera
                    next.setYaw((float) getYawFromVector(raceDirection));
                    horse.teleport(next);

                    // Efecto visual: partículas de humo en cada movimiento
                    horse.getWorld().spawnParticle(Particle.SMOKE, horse.getLocation(), 5, 0.3, 0.3, 0.3, 0.01);

                    // Para cada caballo, se calcula su meta ajustada con el mismo offset
                    int offsetIndex = id - 3;
                    Vector offset = rightVector.clone().multiply(offsetSpacing * offsetIndex);
                    Location horseFinish = finishLine.clone().add(offset);

                    // Comprueba si el caballo ha cruzado la meta (usando la proyección sobre la dirección de carrera)
                    if (hasCrossedFinishLine(horse.getLocation(), horseFinish)) {
                        Bukkit.broadcastMessage("¡El caballo " + id + " ha ganado la carrera!");
                        cancel(); // Detiene el runnable
                        payoutWinner(id);
                        break;
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    // Asigna velocidades aleatorias (únicas) para cada caballo
    private void assignUniqueVelocities() {
        Set<Double> assignedSpeeds = new HashSet<>();
        Random random = new Random();

        for (int id = 1; id <= HORSE_COUNT; id++) {
            double speed;
            do {
                // Velocidad entre 0.1 y 0.01 (ajustable)
                speed = 0.01 + (0.1 - 0.01) * random.nextDouble();
            } while (assignedSpeeds.contains(speed));
            assignedSpeeds.add(speed);
            horseVelocities.put(id, speed);
        }
    }

    // Comprueba si el caballo ha cruzado la línea de meta ajustada para su offset
    private boolean hasCrossedFinishLine(Location horseLoc, Location horseFinish) {
        // Calcula la proyección sobre la dirección de carrera
        Vector startToHorse = horseLoc.toVector().subtract(startLine.toVector());
        Vector startToFinish = horseFinish.toVector().subtract(startLine.toVector());
        return startToHorse.dot(raceDirection) >= startToFinish.dot(raceDirection);
    }

    // Calcula el yaw (rotación horizontal) a partir de un vector de dirección
    private double getYawFromVector(Vector direction) {
        double yaw = Math.toDegrees(Math.atan2(direction.getZ(), direction.getX())) - 90;
        return (yaw < 0) ? yaw + 360 : yaw;
    }

    // Realiza el pago a los jugadores que apostaron por el caballo ganador
    private void payoutWinner(int winningHorseId) {
        List<Bet> winningBets = bets.get(winningHorseId);
        for (Bet bet : winningBets) {
            double payout = bet.amount() * 2; // Ejemplo: paga el doble de la apuesta
            bet.player().sendMessage("¡Ganaste! Recibes " + payout + " monedas.");
            // Aquí se actualizaría el balance del jugador, etc.
        }
    }

    // Clase interna que representa una apuesta
        public record Bet(Player player, double amount) {
    }
}
