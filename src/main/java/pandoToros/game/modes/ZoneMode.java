package pandoToros.game.modes;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class ZoneMode {

    public static void startZoneMode(World world, PandoDungeons plugin, Map<Player,Integer> points, List<Player> players) {
        // Coordenadas que delimitan el área donde el círculo se puede mover
        double minX = -7, maxX = 24;
        double minZ = -4, maxZ = 26;
        double y = -3; // Altura constante
        double initialDiameter = 20; // Diámetro inicial
        double finalDiameter = 2;    // Diámetro final
        int durationSeconds = 300;  // 5 minutos en segundos
        int ticksPerSecond = 20;    // Ticks por segundo en Bukkit
        int totalTicks = durationSeconds * ticksPerSecond;

        // Variables para el movimiento y reducción de diámetro
        Random random = new Random();
        Location currentCenter = new Location(world, random.nextDouble() * (maxX - minX) + minX, y, random.nextDouble() * (maxZ - minZ) + minZ);

        Location displayCenter = new Location(world, 8, 3, -8);

        int armorstandCount = players.size();
        if(armorstandCount > 5) {
            armorstandCount = 5;
        }

        List<ArmorStand> displayStands = createDisplayStands(displayCenter, world, armorstandCount);

        new BukkitRunnable() {
            int elapsedTicks = 0;
            double currentDiameter = initialDiameter;
            int seconds = 0;
            @Override
            public void run() {
                if (elapsedTicks >= totalTicks) {
                    // Detener el bucle después de 5 minutos
                    cancel();
                    return;
                }

                if(elapsedTicks % 20 == 0){
                    seconds++;
                    List<Player> playersInCircle = getPlayersInCircle(currentCenter, currentDiameter, world);
                    for (Player player : playersInCircle) {
                        if(player.getGameMode() != GameMode.SPECTATOR){
                            int currentPoints = points.getOrDefault(player, 0);
                            points.put(player, currentPoints + 1);
                        }
                    }
                }

                updateDisplay(displayStands, points);

                // Calcular el progreso de tiempo
                double progress = elapsedTicks / (double) totalTicks;

                // Reducir el diámetro del círculo gradualmente
                currentDiameter = initialDiameter - (initialDiameter - finalDiameter) * progress;

                // Dibujar el círculo de partículas
                drawParticleCircle(currentCenter, currentDiameter, world);

                // Definir una velocidad base (es la distancia por paso, puede ser ajustada)
                double baseSpeed = 0.5; // Este es el valor que controla lo "lento" que se mueve

                // Definir un multiplicador de rango para aumentar la distancia que se mueve por paso
                double rangeMultiplier = 2.0; // Este valor controla cuán lejos puede moverse el círculo en cada paso

                // Calcular el desplazamiento más grande, pero respetando la velocidad base
                double deltaX = (random.nextDouble() - 0.5) * baseSpeed * rangeMultiplier; // Desplazamiento en X
                double deltaZ = (random.nextDouble() - 0.5) * baseSpeed * rangeMultiplier; // Desplazamiento en Z

                // Actualizar la posición del círculo, respetando los límites
                double newX = Math.min(maxX, Math.max(minX, currentCenter.getX() + deltaX));
                double newZ = Math.min(maxZ, Math.max(minZ, currentCenter.getZ() + deltaZ));
                currentCenter.setX(newX);
                currentCenter.setZ(newZ);

                // Incrementar el contador de ticks
                elapsedTicks++;
            }
        }.runTaskTimer(plugin, 0L, 1L); // Ejecutar cada tick
    }

    public static List<Player> getPlayersInCircle(Location center, double diameter, World world) {
        double radius = diameter / 2; // Calcular el radio del círculo
        List<Player> playersInCircle = new ArrayList<>();

        for (Player player : world.getPlayers()) {
            // Calcular la distancia en 2D entre el jugador y el centro del círculo
            double distanceSquared = center.distanceSquared(player.getLocation());
            if (distanceSquared <= radius * radius) {
                playersInCircle.add(player); // Añadir a la lista si está dentro del círculo
            }
        }

        return playersInCircle;
    }

    private static void drawParticleCircle(Location center, double diameter, org.bukkit.World world) {
        double radius = diameter / 2;
        int particleCount = (int) (radius * 13); // Más partículas para círculos más grandes
        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            world.spawnParticle(Particle.GLOW_SQUID_INK, x, center.getY(), z, 1, 0, 0, 0, 0); // Cambia el tipo de partícula si es necesario
        }
    }

    public static List<ArmorStand> createDisplayStands(Location center, World world, int count) {
        List<ArmorStand> stands = new ArrayList<>();
        double spacing = 0.3; // Separación vertical entre los nombres
        for (int i = 0; i < count; i++) {
            Location loc = center.clone().add(0, -i * spacing, 0);
            ArmorStand stand = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setCustomNameVisible(true);
            stand.setGravity(false);
            stands.add(stand);
        }
        return stands;
    }

    public static void updateDisplay(List<ArmorStand> stands, Map<Player, Integer> points) {
        // Ordenar jugadores por puntos
        List<Map.Entry<Player, Integer>> sortedPoints = points.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .toList();

        // Actualizar los nombres y colores
        for (int i = 0; i < stands.size(); i++) {
            ArmorStand stand = stands.get(i);
            if (i < sortedPoints.size()) {
                Map.Entry<Player, Integer> entry = sortedPoints.get(i);
                String name = entry.getKey().getName();
                int score = entry.getValue();
                String color = getColorForPosition(i);
                stand.setCustomName(color + name + ChatColor.RESET + " - " + score);
            } else {
                stand.setCustomName("");
            }
        }
    }

    private static String getColorForPosition(int position) {
        return switch (position) {
            case 0 -> ChatColor.GOLD.toString();
            case 1 -> ChatColor.GRAY.toString();
            case 2 -> ChatColor.DARK_RED.toString();
            default -> ChatColor.BOLD.toString() + ChatColor.WHITE;
        };
    }
}
