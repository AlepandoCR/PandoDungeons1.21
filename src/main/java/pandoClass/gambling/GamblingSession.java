package pandoClass.gambling;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pandoClass.RPGPlayer;
import pandodungeons.pandodungeons.PandoDungeons;

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
    private final PandoDungeons plugin;

    // Dirección de la carrera (calculada de startLine a finishLine)
    private final Vector raceDirection;
    // Vector "derecho" para calcular el offset lateral (producto cruzado con el vector up)
    private final Vector rightVector;
    // Distancia lateral entre caballos
    private final double offsetSpacing = 1.33;

    public GamblingSession(PandoDungeons plugin, Location startLine, Location finishLine) throws MalformedURLException {
        this.plugin = plugin;
        this.startLine = startLine;
        this.finishLine = finishLine;

        // Calcula la dirección principal de la carrera
        this.raceDirection = finishLine.toVector().subtract(startLine.toVector()).normalize();
        // Calcula el vector perpendicular (a la derecha) usando el vector up (0,1,0)
        this.rightVector = raceDirection.clone().crossProduct(new Vector(0, 1, 0)).normalize();

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
        ItemStack head = createHead("Gallina" + horseId, "265e72fa2d42e7871c4deac117bb9b9e4161c5b3d71f662e037c071bb066e42a");
        return spawnArmorStand(spawnLoc, head, "Gallina" + horseId);
    }

    private ArmorStand spawnArmorStand(Location loc, ItemStack head, String tag) {
        if (loc.getWorld() == null) return null;


        ArmorStand as = loc.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            armorStand.setInvulnerable(true);
            armorStand.setRemoveWhenFarAway(false);
            armorStand.setCollidable(false);
            armorStand.setMarker(true);

            armorStand.setCustomName(ChatColor.GOLD + " " + tag);
            armorStand.setCustomNameVisible(true); // Asegurar que se muestra

            armorStand.getEquipment().setHelmet(head);
            // armorStand.addScoreboardTag(tag); // Prueba sin esto
        });
        return as;
    }


    // Registro de una apuesta
    // Registro de una apuesta
    public void addBet(Player player, int horseId, int amount) {
        // Verificar si el jugador ya ha apostado en cualquier caballo
        for (List<Bet> betList : bets.values()) {
            for (Bet bet : betList) {
                if (bet.player().equals(player)) {
                    player.sendMessage(ChatColor.RED + "¡Ya has apostado! No puedes hacer más de una apuesta en esta sesión.");
                    return;
                }
            }
        }

        // Verificar si el caballo existe en la lista de apuestas
        if (!bets.containsKey(horseId)) {
            player.sendMessage(ChatColor.RED + "Ese numero de Gallina no existe");
            return;
        }

        if(plugin.rpgManager.getPlayer(player).getCoins() < amount){
            player.sendMessage(ChatColor.RED + "No tienes ese dinero para apostar");
            return;
        }

        // Registrar la apuesta
        bets.get(horseId).add(new Bet(player, amount));
        Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " apostó " + amount + " en la Gallina " + horseId);

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
        removeCoins();
        Bukkit.broadcastMessage("¡La carrera ha comenzado!");

        new BukkitRunnable() {
            @Override
            public void run() {
                assignUniqueVelocities();
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
                        Bukkit.broadcastMessage("¡La Gallina " + id + " ha ganado la carrera!");
                        removeHorses();
                        cancel(); // Detiene el runnable
                        payoutWinner(id);
                        try {
                            plugin.gamblingSession = new GamblingSession(plugin,startLine,finishLine);
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    public void  removeHorses(){
        if(plugin.gamblingSession != null){
            for (Map.Entry<Integer, ArmorStand> entry : horses.entrySet()) {
                ArmorStand horse = entry.getValue();
                horse.remove();
            }
        }
    }

    private void removeCoinsFromPlayer(Player player, int toRemove){
        plugin.rpgManager.getPlayer(player).removeCoins(toRemove);
    }

    private void    removeCoins(){
        for (Map.Entry<Integer, List<Bet>> entry : bets.entrySet()) {
            for (Bet bet : entry.getValue()) {
                removeCoinsFromPlayer(bet.player(), bet.amount());
                bet.player().sendMessage(ChatColor.YELLOW + "Se han retirado " + bet.amount() + " monedas de tu cuenta.");
            }
        }
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


    private void payoutWinner(int winningHorseId) {
        // Obtenemos las apuestas ganadoras
        List<Bet> winningBets = bets.get(winningHorseId);
        double totalBetAmount = 0;  // Total apostado por los ganadores
        double totalPoolAmount = 0; // Total del "pozo" (dinero de los jugadores que no ganaron)
        double totalAmountBet = 0;  // Total apostado por todos los jugadores (ganadores + perdedores)

        // Primero, calculamos la cantidad total apostada por los ganadores
        for (Bet bet : winningBets) {
            totalBetAmount += bet.amount();  // Acumulamos la cantidad apostada por los ganadores
        }

        // Luego, calculamos el total apostado en todas las apuestas y sumamos el dinero de los jugadores que no ganaron
        for (Map.Entry<Integer, List<Bet>> entry : bets.entrySet()) {
            if (entry.getKey() != winningHorseId) {
                // Si la apuesta es de un caballo que no ganó, acumulamos el dinero de los jugadores perdedores
                for (Bet bet : entry.getValue()) {
                    totalPoolAmount += bet.amount();
                    // Le quitamos el dinero a los jugadores perdedores
                    bet.player().sendMessage(ChatColor.RED + "Perdiste tu apuesta de " + bet.amount() + " monedas.");
                }
            }
            // También sumamos todo el dinero apostado en la pool
            for (Bet bet : entry.getValue()) {
                totalAmountBet += bet.amount();
            }
        }

        // Ahora, repartimos el "pozo" entre los ganadores y les devolvemos lo apostado
        for (Bet bet : winningBets) {
            // Calcular la proporción de la pool que recibe este jugador
            double proportionalShare = (bet.amount() / totalAmountBet) * totalPoolAmount;

            // El pago es la cantidad apostada más la parte proporcional de la pool
            double payout = bet.amount() + proportionalShare;

            // Enviamos el mensaje al jugador indicando el pago
            bet.player().sendMessage(ChatColor.GREEN + "¡Ganaste! Recibes " + payout + " monedas.");

            // Se añade el dinero al jugador
            plugin.rpgManager.getPlayer(bet.player()).addCoins((int) payout);

            // Aquí se actualizaría el balance del jugador, según tu implementación
        }
    }
    // Clase interna que representa una apuesta
        public record Bet(Player player, int amount) {
    }
}
