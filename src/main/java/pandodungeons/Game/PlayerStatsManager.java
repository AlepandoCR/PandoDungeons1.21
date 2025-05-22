package pandodungeons.Game;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static pandodungeons.Game.Stats.fromStatsManager;

public class PlayerStatsManager {
    private static final Map<UUID, PlayerStatsManager> playerStats = new HashMap<>();
    private File playerFile;
    private FileConfiguration config;
    private int mobsKilled;
    private int dungeonsCompleted;
    private int dungeonLevel;
    private String playerName;
    private int levelProgress;
    private int prestige;

    public PlayerStatsManager() {
        // Constructor vacío para permitir la inicialización manual de variables
    }

    public static List<String> getAllPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        for (PlayerStatsManager statsManager : playerStats.values()) {
            playerNames.add(statsManager.getPlayerName());
        }
        return playerNames;
    }

    public File getPlayerFile() {
        return playerFile;
    }

    public PlayerStatsManager(Player player) {
        this.playerFile = new File("plugins/PandoDungeons/PlayerData", player.getUniqueId() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(playerFile);

        // Load existing stats or create new if file doesn't exist
        if (playerFile.exists()) {
            loadStats();
            if(playerName.equals("N/A")){
                this.playerName = player.getName();
            }
        } else {
            initializeDefaultStats(player);
        }
    }

    public static List<Stats> getAllStats() {
        List<Stats> allStats = new ArrayList<>();
        for (PlayerStatsManager statsManager : playerStats.values()) {
            allStats.add(fromStatsManager(statsManager));
        }
        return allStats;
    }

    private void initializeDefaultStats(Player player) {
        // Initialize default stats values
        this.playerName = player.getName();
        this.mobsKilled = 0;
        this.dungeonsCompleted = 0;
        this.dungeonLevel = 1;
        this.levelProgress = 0;
        this.prestige = 0;
        // Save default values to the config and file
        saveStats();
    }

    public static PlayerStatsManager getPlayerStatsManager(Player player) {
        return playerStats.computeIfAbsent(player.getUniqueId(), k -> new PlayerStatsManager(player));
    }

    public static PlayerStatsManager getPlayerStatsManagerByName(String name) {
        for (PlayerStatsManager statsManager : playerStats.values()) {
            if (statsManager.playerName.equalsIgnoreCase(name)) {
                return statsManager;
            }
        }
        return null;
    }

    public void loadStats() {
        this.mobsKilled = config.getInt("mobsKilled", 0);
        this.dungeonsCompleted = config.getInt("dungeonsCompleted", 0);
        this.dungeonLevel = config.getInt("dungeonLevel", 1);
        this.playerName = config.getString("playerName", "N/A");
        this.levelProgress = config.getInt("levelProgress", 0);
        this.prestige = config.getInt("prestiño", 0);
    }

    public void saveStats() {
        config.set("playerName", playerName);
        config.set("mobsKilled", mobsKilled);
        config.set("dungeonsCompleted", dungeonsCompleted);
        config.set("dungeonLevel", dungeonLevel);
        config.set("levelProgress", levelProgress);
        config.set("prestiño", prestige);
        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMobKill() {
        mobsKilled++;
        saveStats();
    }

    public void addDungeonCompletion() {
        loadStats();
        dungeonsCompleted++;
        levelProgress++;
        if(levelProgress >= 3){
            levelProgress = 0;
            dungeonLevel++;
        }
        if(dungeonLevel >= 5){
            prestige++;
            dungeonLevel = 1;
            levelProgress = 0;
        }
        saveStats();
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public int getDungeonsCompleted() {
        return dungeonsCompleted;
    }

    public void resetLevelProgress(){
        levelProgress = 0;
        saveStats();
    }

    public static List<Stats> loadAllPlayerStatsList(){
        List<Stats> allStats = new ArrayList<>();
        File playerDataFolder = new File("plugins/PandoDungeons/PlayerData");
        if (!playerDataFolder.exists() || !playerDataFolder.isDirectory()) {
            return null;
        }

        File[] playerFiles = playerDataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (playerFiles == null) {
            return null;
        }

        for (File playerFile : playerFiles) {
            if(!playerFile.getName().contains("Toro")){
                String fileName = playerFile.getName();
                String uuidString = fileName.substring(0, fileName.length() - 4); // Remove ".yml"
                try {
                    PlayerStatsManager manager = loadStatsManagerReturn(uuidString);
                    allStats.add(fromStatsManager(manager));
                } catch (IllegalArgumentException e) {
                    // Handle invalid UUID format
                    e.printStackTrace();
                }
            }

        }
        return allStats;
    }

    public static void loadAllPlayerStats() {
        File playerDataFolder = new File("plugins/PandoDungeons/PlayerData");
        if (!playerDataFolder.exists() || !playerDataFolder.isDirectory()) {
            return;
        }

        File[] playerFiles = playerDataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (playerFiles == null) {
            return;
        }

        for (File playerFile : playerFiles) {
            if(!playerFile.getName().contains("Toro")){
                String fileName = playerFile.getName();
                String uuidString = fileName.substring(0, fileName.length() - 4); // Remove ".yml"
                try {
                    loadStatsManager(uuidString);
                } catch (IllegalArgumentException e) {
                    // Handle invalid UUID format
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loadStatsManager(String fileName) {
        File playerFile = new File("plugins/PandoDungeons/PlayerData", fileName + ".yml");
        if (!playerFile.exists() && !playerFile.getName().contains("Toro")) {
            throw new IllegalArgumentException("The file " + fileName + ".yml does not exist.");
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        PlayerStatsManager statsManager = new PlayerStatsManager();
        statsManager.playerFile = playerFile;
        statsManager.config = config;
        statsManager.loadStatsFromFile();

        UUID playerUUID = UUID.fromString(fileName);
        playerStats.put(playerUUID, statsManager);

    }

    public static PlayerStatsManager loadStatsManagerReturn(String fileName) {
        File playerFile = new File("plugins/PandoDungeons/PlayerData", fileName + ".yml");
        if (!playerFile.exists() && !playerFile.getName().contains("Toro")) {
            throw new IllegalArgumentException("The file " + fileName + ".yml does not exist.");
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        PlayerStatsManager statsManager = new PlayerStatsManager();
        statsManager.playerFile = playerFile;
        statsManager.config = config;
        statsManager.loadStatsFromFile();

        UUID playerUUID = UUID.fromString(fileName);
        playerStats.put(playerUUID, statsManager);
        return statsManager;
    }

    private void loadStatsFromFile() {
        this.mobsKilled = config.getInt("mobsKilled", 0);
        this.dungeonsCompleted = config.getInt("dungeonsCompleted", 0);
        this.dungeonLevel = config.getInt("dungeonLevel", 1);
        this.playerName = config.getString("playerName", "N/A");
        this.levelProgress = config.getInt("levelProgress", 0);
        this.prestige = config.getInt("prestiño", 0);
    }

    public static List<PlayerStatsManager> getAllPlayerStats() {
        return new ArrayList<>(playerStats.values());
    }

    public int getDungeonLevel() {
        return dungeonLevel;
    }

    public String toString(Player player) {
        return ChatColor.BOLD + "" + ChatColor.GOLD + "Dungeons Stats" + "\n" +
                ChatColor.RESET + ChatColor.AQUA + "Player: " + ChatColor.RESET + ChatColor.WHITE + playerName + "\n" +
                ChatColor.AQUA + "Mobs Killed: " + ChatColor.RESET + ChatColor.GREEN + mobsKilled + "\n" +
                ChatColor.AQUA + "Dungeons Completed: " + ChatColor.RESET + ChatColor.GREEN + dungeonsCompleted + "\n" +
                ChatColor.AQUA + "Dungeon Level: " + ChatColor.RESET + ChatColor.GREEN + dungeonLevel + "\n" +
                ChatColor.AQUA + "Level Progress: " + ChatColor.RESET + ChatColor.GREEN + levelProgress + "\n" +
                ChatColor.AQUA + "Prestiño: " + ChatColor.RESET + ChatColor.GREEN + prestige + ChatColor.RESET + ChatColor.GOLD + " ✦" +  "\n";
    }

    public void resetStats() {
        mobsKilled = 0;
        dungeonLevel = 1;
        dungeonsCompleted = 0;
        levelProgress = 0;
        prestige = 0;
        saveStats();
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getLevelProgress() {
        return levelProgress;
    }

    public int getPrestige() {
        loadStats();
        return prestige;
    }

    public void setPrestige(int prestige){
        this.prestige = prestige;
        saveStats();
    }
}
