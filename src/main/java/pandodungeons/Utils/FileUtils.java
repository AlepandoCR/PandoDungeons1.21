package pandodungeons.Utils;

import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.PandoDungeons;

import java.io.File;

public class FileUtils {
    private static final String PLAYER_LOCATIONS_FILE_NAME = "player_locations.json";
    private static final String DUNGEONS_DATA_FOLDER_NAME = "dungeons_data";
    private static final String DUNGEON_LAYOUT_FOLDER_NAME = "dungeons_struct";
    private static final String DUNGEONS_FILE_NAME = "dungeons.json";
    private static final String COMPANIONS_FILE = "companions";
    private static final String RPG_PLAYERS_FILE = "rpgPlayers";

    public static File getPlayerLocationsFile() {
        JavaPlugin plugin = PandoDungeons.getPlugin(PandoDungeons.class);
        File dataFolder = plugin.getDataFolder();
        return new File(dataFolder, PLAYER_LOCATIONS_FILE_NAME);
    }

    public static File getDungeonsDataFile() {
        JavaPlugin plugin = PandoDungeons.getPlugin(PandoDungeons.class);
        File dataFolder = plugin.getDataFolder();
        return new File(dataFolder, DUNGEONS_DATA_FOLDER_NAME + File.separator + DUNGEONS_FILE_NAME);
    }

    public static File getDungeonDataFolder() {
        JavaPlugin plugin = PandoDungeons.getPlugin(PandoDungeons.class);
        return new File(plugin.getDataFolder(), DUNGEONS_DATA_FOLDER_NAME);
    }

    public static File getDungeonLayoutFolder(){
        JavaPlugin plugin = PandoDungeons.getPlugin(PandoDungeons.class);
        return new File(plugin.getDataFolder(), DUNGEON_LAYOUT_FOLDER_NAME);
    }

    public static File getCompanionsFile(){
        JavaPlugin plugin = PandoDungeons.getPlugin(PandoDungeons.class);
        return new File(plugin.getDataFolder(), COMPANIONS_FILE);
    }

    public static File getRpgPlayersFile(){
        JavaPlugin plugin = PandoDungeons.getPlugin(PandoDungeons.class);
        return new File(plugin.getDataFolder(), RPG_PLAYERS_FILE);
    }

}
