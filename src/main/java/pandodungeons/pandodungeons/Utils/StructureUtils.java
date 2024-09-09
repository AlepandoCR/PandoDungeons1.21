package pandodungeons.pandodungeons.Utils;

import com.fastasyncworldedit.core.extent.processor.lighting.RelightMode;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pandodungeons.pandodungeons.PandoDungeons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;

public class StructureUtils {

    private static final Random random = new Random();
    private static final JavaPlugin plugin = PandoDungeons.getPlugin(PandoDungeons.class);
    private static final File DUNGEON_STRUCTURES_FOLDER = FileUtils.getDungeonLayoutFolder();

    public static Clipboard loadStructure(Location location, String theme, String playerName, org.bukkit.World world) {
        File themeFolder = new File(DUNGEON_STRUCTURES_FOLDER, theme);
        if (!themeFolder.exists() || !themeFolder.isDirectory()) {
            Bukkit.getLogger().warning("La temática " + theme + " no existe o no es una carpeta.");
            return null;
        }

        File[] structures = themeFolder.listFiles((dir, name) -> name.endsWith(".schem") && !name.contains("Spawn"));
        if (structures == null || structures.length == 0) {
            Bukkit.getLogger().warning("No se encontraron estructuras para la temática " + theme);
            return null;
        }

        Random random = new Random();
        File structureFile = structures[random.nextInt(structures.length)];
        ClipboardFormat format = ClipboardFormats.findByFile(structureFile);

        if (format == null) {
            Bukkit.getLogger().warning("No se pudo determinar el formato de la estructura: " + structureFile.getName());
            return null;
        }

        Clipboard clipboard;
        try (FileInputStream fis = new FileInputStream(structureFile); ClipboardReader reader = format.getReader(fis)) {
            clipboard = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (clipboard != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(new BukkitWorld(world)).relightMode(RelightMode.NONE).build()) {
                    editSession.setFastMode(true);
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .ignoreAirBlocks(true)
                            .to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                            .build();
                    Operations.complete(operation);
                } catch (WorldEditException e) {
                    e.printStackTrace();
                }
            });
        }

        return clipboard;
    }


    public static @Nullable Location findBlock(@NotNull Location roomLocation, int radius, Material targetMaterial) {
        World world = roomLocation.getWorld();
        if (world == null || !isWorldLoaded(world)) return null;

        int centerX = roomLocation.getBlockX();
        int centerY = roomLocation.getBlockY();
        int centerZ = roomLocation.getBlockZ();

        for (int r = 0; r <= radius; r++) {
            for (int x = -r; x <= r; x++) {
                for (int y = -r; y <= r; y++) {
                    // Solo itera sobre la superficie del cubo en z
                    int z = r;
                    if (checkBlock(world, centerX + x, centerY + y, centerZ + z, targetMaterial)) {
                        return new Location(world, centerX + x, centerY + y, centerZ + z);
                    }
                    z = -r;
                    if (checkBlock(world, centerX + x, centerY + y, centerZ + z, targetMaterial)) {
                        return new Location(world, centerX + x, centerY + y, centerZ + z);
                    }
                }
            }
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    // Solo itera sobre la superficie del cubo en y
                    int y = r;
                    if (checkBlock(world, centerX + x, centerY + y, centerZ + z, targetMaterial)) {
                        return new Location(world, centerX + x, centerY + y, centerZ + z);
                    }
                    y = -r;
                    if (checkBlock(world, centerX + x, centerY + y, centerZ + z, targetMaterial)) {
                        return new Location(world, centerX + x, centerY + y, centerZ + z);
                    }
                }
            }
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    // Solo itera sobre la superficie del cubo en x
                    int x = r;
                    if (checkBlock(world, centerX + x, centerY + y, centerZ + z, targetMaterial)) {
                        return new Location(world, centerX + x, centerY + y, centerZ + z);
                    }
                    x = -r;
                    if (checkBlock(world, centerX + x, centerY + y, centerZ + z, targetMaterial)) {
                        return new Location(world, centerX + x, centerY + y, centerZ + z);
                    }
                }
            }
        }

        return null;
    }

    private static boolean checkBlock(World world, int x, int y, int z, Material targetMaterial) {
        if(world == null || !isWorldLoaded(world)) return false;
        return world.getBlockAt(x, y, z).getType() == targetMaterial;
    }


    public static Location findNetheriteBlock(Location roomLocation, int radius) {
        return findBlock(roomLocation, radius, Material.NETHERITE_BLOCK);
    }

    public static Location findDriedKelpBlock(Location roomLocation, int radius) {
        return findBlock(roomLocation, radius, Material.DRIED_KELP_BLOCK);
    }



    public static void loadSpawnStructure(Location location, String theme, String playerName, org.bukkit.World world) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File themeFolder = new File(DUNGEON_STRUCTURES_FOLDER, theme);
            if (!themeFolder.exists() || !themeFolder.isDirectory()) {
                Bukkit.getLogger().warning("La temática " + theme + " no existe o no es una carpeta.");
                return;
            }

            File[] spawnStructures = themeFolder.listFiles((dir, name) -> name.endsWith(".schem") && name.contains("Spawn"));
            if (spawnStructures == null || spawnStructures.length == 0) {
                Bukkit.getLogger().warning("No se encontraron estructuras de spawn para la temática " + theme);
                return;
            }

            File structureFile = spawnStructures[random.nextInt(spawnStructures.length)];
            ClipboardFormat format = ClipboardFormats.findByFile(structureFile);

            if (format == null) {
                Bukkit.getLogger().warning("No se pudo determinar el formato de la estructura: " + structureFile.getName());
                return;
            }

            Clipboard clipboard;
            try (FileInputStream fis = new FileInputStream(structureFile); ClipboardReader reader = format.getReader(fis)) {
                clipboard = reader.read();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if (clipboard != null) {
                try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(new BukkitWorld(world)).relightMode(RelightMode.NONE).build()) {
                    editSession.setFastMode(true);
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .ignoreAirBlocks(false)
                            .to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                            .build();
                    Operations.complete(operation);
                } catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static List<String> getAvailableThemes() {
        List<String> themes = new ArrayList<>();
        File[] themeFolders = DUNGEON_STRUCTURES_FOLDER.listFiles(File::isDirectory);
        if (themeFolders != null) {
            for (File themeFolder : themeFolders) {
                themes.add(themeFolder.getName());
            }
        }
        if(themes.isEmpty()){
            Bukkit.getLogger().warning("No se encontró ningun Theme");
        }
        return themes;
    }

    public static void removeDungeon(String playerName, Plugin plugin) {
        World world = Bukkit.getWorld("dungeon_" + playerName);
        if (world != null) {
            List<Player> players = world.getPlayers();
            for(Player player: players){
                player.setGameMode(GameMode.SURVIVAL);
                if(!player.getName().toLowerCase(Locale.ROOT).equals(playerName)){
                    Location playerSpawnPoint = player.getBedSpawnLocation();
                    if(playerSpawnPoint != null){
                        player.teleport(playerSpawnPoint);
                    }else{
                        World spawn = Bukkit.getWorld("spawn");
                        if(spawn != null){
                            Location spawnSpawn = spawn.getSpawnLocation();
                            player.teleport(spawnSpawn);
                        }
                    }
                }
            }
            Bukkit.unloadWorld(world, false); // Descarga el mundo completamente
            Bukkit.getLogger().info(Bukkit.unloadWorld(world, false)+"");

            new BukkitRunnable()
            {
                @Override
                public void run() {

                    deleteWorld(world.getWorldFolder()); // Borra los archivos del mundo
                }
            }.runTaskLater(plugin,20);
        }
    }

    public static boolean isWorldLoaded(World world) {
        return Bukkit.getWorlds().contains(world);
    }

    private static void deleteWorld(File worldFolder) {
        // Busca el mundo correspondiente al folder
        World world = Bukkit.getWorld(worldFolder.getName());
        if (world != null) {
            // Elimina todas las entidades del mundo
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof Player)) {
                    entity.remove();
                }
            }

            // Desactiva y desprovee el mundo del servidor
            Bukkit.unloadWorld(world, false);
        } else {
            Bukkit.getLogger().log(Level.WARNING, "World not found: " + worldFolder.getName());
        }

        // Continúa con la eliminación de los archivos del mundo
        if (worldFolder.exists()) {
            File[] files = worldFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorld(file);
                    } else {
                        if (!file.delete()) {
                            Bukkit.getLogger().log(Level.WARNING, "Failed to delete file: " + file.getAbsolutePath());
                        }
                    }
                }
            }
            if (!worldFolder.delete()) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to delete world folder: " + worldFolder.getAbsolutePath());
            } else {
                Bukkit.getLogger().log(Level.INFO, "World folder deleted successfully: " + worldFolder.getAbsolutePath());
            }
        } else {
            Bukkit.getLogger().log(Level.WARNING, "World folder does not exist: " + worldFolder.getAbsolutePath());
        }
    }

    public static org.bukkit.World createDungeonWorld(String playerName) {
        // Crear un nuevo mundo con un nombre único para el jugador
        WorldCreator creator = new WorldCreator("dungeon_" + playerName);
        creator.type(WorldType.FLAT); // Tipo de mundo flat
        creator.generator(new EmptyChunkGenerator()); // Generador de chunks vacío

        org.bukkit.World world = Bukkit.createWorld(creator);

        if (world != null) {
            // Configurar propiedades adicionales del mundo
            setupWorldBorder(world);
            world.setKeepSpawnInMemory(false); // No mantener el spawn en memoria
            world.setGameRule(GameRule.KEEP_INVENTORY, true); // Mantener inventario al morir
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false); // Deshabilitar la generación de mobs
            world.setDifficulty(Difficulty.HARD);
            world.setSpawnLimit(SpawnCategory.MONSTER, 10000);
            world.setTime(18000);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
        }

        return world;
    }


    private static void setupWorldBorder(org.bukkit.World world) {
        org.bukkit.WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setCenter(0, 0);
        worldBorder.setSize(1400); // Diámetro de 1400 bloques (700 de radio)
    }

    public static World getDungeonWorld(String playerName) {
        return Bukkit.getWorld(playerName);
    }
}
