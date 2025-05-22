package pandoToros.game.modes;

import org.bukkit.*;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.PandoDungeons;

import java.util.*;

import static pandoToros.game.modes.ZoneMode.createDisplayStands;
import static pandoToros.game.modes.ZoneMode.updateDisplay;

public class PlatformMode {

    private static final double MIN_X = -7, MAX_X = 24;
    private static final double MIN_Z = -4, MAX_Z = 26;
    private static final double Y = -3;
    private static final int PLATFORM_SIZE = 6;
    private static final double SPEED = 0.1; // Velocidad de movimiento

    private static List<Location> platformBlocks = new ArrayList<>();
    private static Location platformCenter;
    public static List<Player> platformPlayers = new ArrayList<>();

    public static void startPlatformMode(World world, PandoDungeons plugin, Map<Player, Integer> points, List<Player> players) {
        platformCenter = new Location(world, 6, Y, 11); // Posición inicial de la plataforma
        createPlatform(world, platformCenter);

        Location displayCenter = new Location(world, 8, 3, -8);

        int armorstandCount = players.size();
        if(armorstandCount > 5) {
            armorstandCount = 5;
        }

        List<ArmorStand> displayStands = createDisplayStands(displayCenter, world, armorstandCount);
        new BukkitRunnable() {
            private double dx = SPEED; // Velocidad en X
            private double dz = 0;     // Velocidad en Z
            private int timer = 0;

            @Override
            public void run() {
                if(world == null){
                    for(Player player : players){
                        platformPlayers.remove(player);
                    }
                    this.cancel();
                    return;
                }
                // Mover la plataforma
                updateDisplay(displayStands, points);
                movePlatform(world);

                for(Player player : players){
                    if(!getPlayersOnPlatform(world).contains(player)){
                        platformPlayers.remove(player);
                    }
                }

                if(timer % 20 == 0){
                    for (Player player : getPlayersOnPlatform(world)) {
                        if(player.getGameMode() != GameMode.SPECTATOR){
                            points.put(player, points.getOrDefault(player, 0) + 1);
                        }
                    }
                }
                timer++;
            }

            private void movePlatform(World world) {
                // Actualizar posición del centro
                platformCenter.add(dx, 0, dz);

                // Redondear la posición del centro
                platformCenter.setX(Math.round(platformCenter.getX() * 10.0) / 10.0);
                platformCenter.setZ(Math.round(platformCenter.getZ() * 10.0) / 10.0);

                // Verificar límites y cambiar dirección
                if (platformCenter.getX() <= MIN_X || platformCenter.getX() >= MAX_X) {
                    dx = -Math.signum(dx) * SPEED; // Cambiar dirección sin alterar magnitud
                }
                if (platformCenter.getZ() <= MIN_Z || platformCenter.getZ() >= MAX_Z) {
                    dz = -Math.signum(dz) * SPEED;
                }


                // Actualizar la plataforma
                updatePlatform(world);
            }

        }.runTaskTimer(plugin, 0L, 2L);
    }

    private static void createPlatform(World world, Location center) {
        platformBlocks.clear();
        int halfSize = PLATFORM_SIZE / 2;

        for (int x = -halfSize; x < halfSize; x++) {
            for (int z = -halfSize; z < halfSize; z++) {
                Location blockLoc = center.clone().add(x, 0, z);
                Block block = world.getBlockAt(blockLoc);
                block.setType(Material.IRON_BLOCK); // Puedes cambiar el material si prefieres otro
                platformBlocks.add(blockLoc);
            }
        }
    }

    private static void updatePlatform(World world) {
        // Eliminar bloques anteriores
        for (Location loc : new ArrayList<>(platformBlocks)) {
            world.getBlockAt(loc).setType(Material.AIR);
        }

        // Actualizar lista y crear bloques nuevos
        createPlatform(world, platformCenter);
    }


    private static List<Player> getPlayersOnPlatform(World world) {
        List<Player> playersOnPlatform = new ArrayList<>();

            for (Player player : world.getPlayers()) {
                Location playerLoc = player.getLocation();
            for (Location blockLoc : platformBlocks) {
                if (isPlayerOnBlock(playerLoc, blockLoc)) {
                    playersOnPlatform.add(player);
                    break;
                }
            }
        }
        return playersOnPlatform;
    }

    private static boolean isPlayerOnBlock(Location playerLoc, Location blockLoc) {
        double px = playerLoc.getX();
        double pz = playerLoc.getZ();

        double bx = blockLoc.getX();
        double bz = blockLoc.getZ();

        // Verificar si el jugador está sobre el bloque
        return px >= bx && px < bx + 1 && pz >= bz && pz < bz + 1 && playerLoc.getY() >= Y && playerLoc.getY() < Y + 2;
    }
}
