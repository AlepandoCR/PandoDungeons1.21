package pandoToros.game.modes;

import net.minecraft.world.entity.EntityType;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;
import pandoToros.game.modes.cosmetic.CosmeticGoal;
import pandoToros.game.modes.cosmetic.base.effects.CosmeticDefaults;
import pandoToros.game.modes.teamScoreboard.Scoreboard;
import pandodungeons.pandodungeons.CustomEntities.Ball.BallArmadillo;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.List;

public class SoccerMatch {

    public static PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    public static void spawnArmadillo(World world){
        BallArmadillo ball = new BallArmadillo(EntityType.ARMADILLO, ((CraftWorld) world).getHandle());
        ball.spawnLocation(new Location(world,10,-3,10));
    }

    /**
     * Crea dos marcos de fútbol hechos de white concrete en las posiciones especificadas.
     *
     * @param world El mundo donde se construirán los marcos.
     */
    public static void createSoccerGoals(World world, List<Player> players, int team1points, int team2points, List<Player> team1, List<Player> team2) {
        spawnArmadillo(world);
        // Centro del primer marco
        Location goal1Center = new Location(world, 24, -4, 10);
        // Centro del segundo marco
        Location goal2Center = new Location(world, -8, -4, 10);

        // Dimensiones del marco
        int width = 7;  // Ancho del marco (horizontal)
        int height = 5; // Altura del marco (vertical)

        // Construir los marcos
        buildGoalFrame(goal1Center, width, height, Material.RED_CONCRETE);
        buildGoalFrame(goal2Center, width, height, Material.GREEN_CONCRETE);

        pandoToros.game.modes.teamScoreboard.Scoreboard scoreboard = new pandoToros.game.modes.teamScoreboard.Scoreboard(world, "Equipo Rojo", "Equipo Verde");

        startMonitoring(world,width,height,plugin,players,team1points,team2points,team1,team2, scoreboard);
    }

    /**
     * Construye el marco de un arco de fútbol en las coordenadas dadas.
     *
     * @param center Centro de la base del marco.
     * @param width Ancho del marco (horizontal).
     * @param height Altura del marco (vertical).
     */
    private static void buildGoalFrame(Location center, int width, int height, Material material) {
        World world = center.getWorld();
        int zStart = center.getBlockZ() - width / 2;
        int zEnd = center.getBlockZ() + width / 2;
        int x = center.getBlockX();
        int yStart = center.getBlockY();
        int yEnd = center.getBlockY() + height;

        // Crear los postes verticales
        for (int y = yStart; y <= yEnd; y++) {
            placeBlock(world, x, y, zStart, material); // Poste izquierdo
            placeBlock(world, x, y, zEnd, material);   // Poste derecho
        }

        // Crear la barra superior
        for (int z = zStart; z <= zEnd; z++) {
            placeBlock(world, x, yEnd, z, material);
        }
    }

    /**
     * Coloca un bloque en las coordenadas especificadas.
     *
     * @param world El mundo donde se colocará el bloque.
     * @param x Coordenada X del bloque.
     * @param y Coordenada Y del bloque.
     * @param z Coordenada Z del bloque.
     * @param material El material del bloque.
     */
    private static void placeBlock(World world, int x, int y, int z, Material material) {
        Block block = world.getBlockAt(x, y, z);
        block.setType(material);
    }

    /**
     * Monitorea el área de los marcos para detectar si alguna entidad ha pasado dentro.
     *
     * @param world El mundo donde se encuentran los marcos.
     */
    public static void monitorGoals(World world, int team1points, int team2points, Scoreboard scoreboard) {
        // Coordenadas para el primer marco (equipo verde)
        int goal1X = -8;
        int goal1YStart = -3;
        int goal1YEnd = 0;
        int goal1ZStart = 8;
        int goal1ZEnd = 12;

        // Coordenadas para el segundo marco (equipo rojo)
        int goal2X = 24;
        int goal2YStart = -3;
        int goal2YEnd = 0;
        int goal2ZStart = 8;
        int goal2ZEnd = 12;

        // Obtener todas las entidades cercanas
        List<Entity> nearbyEntities = world.getEntities();

        // Comprobar si alguna entidad está dentro de las áreas de los marcos
        for (Entity entity : nearbyEntities) {
            if (entity.getScoreboardTags().contains("bolaFut")) {
                Location entityLocation = entity.getLocation();
                CosmeticGoal cosmeticGoal = new CosmeticGoal(plugin);
                // Verificar si la entidad está dentro del área del primer marco
                if (isEntityInGoalArea(entityLocation, goal1X, goal1YStart, goal1YEnd, goal1ZStart, goal1ZEnd)) {
                    System.out.println("Entidad " + entity.getName() + " pasó por el primer marco.");
                    team2points++;
                    entity.remove();
                    cosmeticGoal.addAction(CosmeticDefaults.getDefault("EXPLOSION_GREEN"));
                    spawnArmadillo(world);
                }

                // Verificar si la entidad está dentro del área del segundo marco
                if (isEntityInGoalArea(entityLocation, goal2X, goal2YStart, goal2YEnd, goal2ZStart, goal2ZEnd)) {
                    System.out.println("Entidad " + entity.getName() + " pasó por el segundo marco.");
                    team1points++;
                    entity.remove();
                    cosmeticGoal.addAction(CosmeticDefaults.getDefault("EXPLOSION_RED"));
                    spawnArmadillo(world);
                }
                scoreboard.updateScore(team1points,team2points);
            }
        }
    }

    /**
     * Verifica si una entidad está dentro de los límites de un marco de fútbol.
     *
     * @param entityLocation La ubicación de la entidad.
     * @param x El valor fijo de la coordenada X del marco.
     * @param yStart El límite inferior en Y.
     * @param yEnd El límite superior en Y.
     * @param zStart El límite inferior en Z.
     * @param zEnd El límite superior en Z.
     * @return Verdadero si la entidad está dentro de los límites del marco.
     */
    private static boolean isEntityInGoalArea(Location entityLocation, int x, int yStart, int yEnd, int zStart, int zEnd) {
        return entityLocation.getBlockX() == x
                && entityLocation.getBlockY() >= yStart && entityLocation.getBlockY() <= yEnd
                && entityLocation.getBlockZ() >= zStart && entityLocation.getBlockZ() <= zEnd;
    }


    /**
     * Inicia un monitoreo periódico de los marcos para verificar si las entidades pasan por ellos.
     *
     * @param world      El mundo donde se encuentran los marcos.
     * @param width      El ancho de los marcos.
     * @param height     La altura de los marcos.
     * @param scoreboard
     */
    public static void startMonitoring(World world, int width, int height, PandoDungeons plugin, List<Player> players, int team1ponts, int team2points, List<Player> team1, List<Player> team2, Scoreboard scoreboard) {
        new BukkitRunnable() {
            final Location goal1Center = new Location(world, 24, -4, 10);
            // Centro del segundo marco
            final Location goal2Center = new Location(world, -8, -4, 10);
            int timer = 0;
            int seconds = 0 ;
            @Override
            public void run() {
                if(timer % 20 == 0){
                    seconds++;
                }
                timer++;
                if(seconds >= 300){
                    this.cancel();
                    if(world != null){
                        scoreboard.removeScoreboard();
                    }
                    return;
                }
                // Ejecutar el monitoreo de los marcos en cada tick
                monitorGoals(world, team1ponts, team2points, scoreboard);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
