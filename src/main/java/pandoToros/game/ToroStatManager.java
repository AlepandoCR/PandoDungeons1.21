package pandoToros.game;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static pandoToros.game.ToroStats.fromToroStatsManager;

public class ToroStatManager {
    private static final Map<UUID, ToroStatManager> playerStats = new HashMap<>();
    private File playerFile;
    private FileConfiguration config;
    private int Kills;
    private int TimeSurvived;
    private int Upgrades;
    private String playerName;
    private int Effects;
    private int dodges;

    public ToroStatManager() {
        // Constructor vacío para permitir la inicialización manual de variables
    }

    public static List<String> getAllPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        for (ToroStatManager statsManager : playerStats.values()) {
            playerNames.add(statsManager.getPlayerName());
        }
        return playerNames;
    }

    public ToroStatManager(Player player) {
        this.playerFile = new File("plugins/PandoDungeons/PlayerData", player.getUniqueId() + "_Toro" + ".yml");
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

    public static List<ToroStats> getAllStats() {
        List<ToroStats> allStats = new ArrayList<>();
        for (ToroStatManager statsManager : playerStats.values()) {
            allStats.add(fromToroStatsManager(statsManager));
        }
        return allStats;
    }

    private void initializeDefaultStats(Player player) {
        // Initialize default stats values
        this.playerName = player.getName();
        this.Kills = 0;
        this.TimeSurvived = 0;
        this.Upgrades = 1;
        this.Effects = 0;
        this.dodges = 0;
        // Save default values to the config and file
        saveStats();
    }

    public static ToroStatManager getToroStatsManager(Player player) {
        return playerStats.computeIfAbsent(player.getUniqueId(), k -> new ToroStatManager(player));
    }

    public static ToroStatManager getPlayerStatsManagerByName(String name) {
        for (ToroStatManager statsManager : playerStats.values()) {
            if (statsManager.playerName.equalsIgnoreCase(name)) {
                return statsManager;
            }
        }
        return null;
    }

    public void loadStats() {
        this.Kills = config.getInt("Kills", 0);
        this.TimeSurvived = config.getInt("TimeSurvived", 0);
        this.Upgrades = config.getInt("Upgrades", 1);
        this.playerName = config.getString("playerName", "N/A");
        this.Effects = config.getInt("Effects", 0);
        this.dodges = config.getInt("dodges", 0);
    }

    public void saveStats() {
        config.set("playerName", playerName);
        config.set("Kills", Kills);
        config.set("TimeSurvived", TimeSurvived);
        config.set("Upgrades", Upgrades);
        config.set("Effects", Effects);
        config.set("dodges", dodges);
        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addKill() {
        loadStats();
        Kills++;
        saveStats();
    }

    public void addTime() {
        loadStats();
        TimeSurvived++;
        saveStats();
    }

    public void addUpgrade() {
        loadStats();
        Upgrades++;
        saveStats();
    }

    public void addEffect(){
        loadStats();
        Effects++;
        saveStats();
    }

    public void addDodges(){
        loadStats();
        dodges++;
        saveStats();
    }

    public int getKills() {
        return Kills;
    }

    public int getTimeSurvived() {
        return TimeSurvived;
    }

    public void resetLevelProgress(){
        Effects = 0;
        saveStats();
    }

    public static List<ToroStats> loadAllToroPlayerStatsList(){
        List<ToroStats> allStats = new ArrayList<>();
        File playerDataFolder = new File("plugins/PandoDungeons/PlayerData");
        if (!playerDataFolder.exists() || !playerDataFolder.isDirectory()) {
            return null;
        }

        File[] playerFiles = playerDataFolder.listFiles((dir, name) -> name.endsWith("Toro.yml"));
        if (playerFiles == null) {
            return null;
        }

        for (File playerFile : playerFiles) {
            String fileName = playerFile.getName();
            String uuidString = fileName.substring(0, fileName.length() - 4); // Remove ".yml"
            try {
                ToroStatManager manager = loadStatsManagerReturn(uuidString);
                allStats.add(fromToroStatsManager(manager));
            } catch (IllegalArgumentException e) {
                // Handle invalid UUID format
                e.printStackTrace();
            }
        }
        return allStats;
    }

    public static void loadAllToroPlayerStats() {
        File playerDataFolder = new File("plugins/PandoDungeons/PlayerData");
        if (!playerDataFolder.exists() || !playerDataFolder.isDirectory()) {
            return;
        }

        File[] playerFiles = playerDataFolder.listFiles((dir, name) -> name.endsWith("Toro.yml"));
        if (playerFiles == null) {
            return;
        }

        for (File playerFile : playerFiles) {
            String fileName = playerFile.getName();
            String uuidString = fileName.substring(0, fileName.length() - 9); // Remove ".yml"
            try {
                loadStatsManager(uuidString);
            } catch (IllegalArgumentException e) {
                // Handle invalid UUID format
                e.printStackTrace();
            }
        }
    }

    public static void loadStatsManager(String fileName) {
        File playerFile = new File("plugins/PandoDungeons/PlayerData", fileName + "_Toro" + ".yml");
        if (!playerFile.exists()) {
            throw new IllegalArgumentException("The file " + fileName + ".yml does not exist.");
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        ToroStatManager statsManager = new ToroStatManager();
        statsManager.playerFile = playerFile;
        statsManager.config = config;
        statsManager.loadStatsFromFile();

        UUID playerUUID = UUID.fromString(fileName);
        playerStats.put(playerUUID, statsManager);

    }

    public static ToroStatManager loadStatsManagerReturn(String fileName) {
        File playerFile = new File("plugins/PandoDungeons/PlayerData", fileName + "_Toro" + ".yml");
        if (!playerFile.exists()) {
            throw new IllegalArgumentException("The file " + fileName + ".yml does not exist.");
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        ToroStatManager statsManager = new ToroStatManager();
        statsManager.playerFile = playerFile;
        statsManager.config = config;
        statsManager.loadStatsFromFile();

        UUID playerUUID = UUID.fromString(fileName);
        playerStats.put(playerUUID, statsManager);
        return statsManager;
    }

    private void loadStatsFromFile() {
        this.Kills = config.getInt("Kills", 0);
        this.TimeSurvived = config.getInt("TimeSurvived", 0);
        this.Upgrades = config.getInt("Upgrades", 1);
        this.playerName = config.getString("playerName", "N/A");
        this.Effects = config.getInt("Effects", 0);
        this.dodges = config.getInt("dodges", 0);
    }

    public static List<ToroStatManager> getAllPlayerStats() {
        return new ArrayList<>(playerStats.values());
    }

    public int getUpgrades() {
        return Upgrades;
    }

    public String toString(Player player) {
        return ChatColor.BOLD + "" + ChatColor.GOLD + "Redondel Stats" + "\n" +
                ChatColor.RESET + ChatColor.AQUA + "Player: " + ChatColor.RESET + ChatColor.WHITE + playerName + "\n" +
                ChatColor.GREEN + "Kills: " + ChatColor.RESET + ChatColor.ITALIC + Kills + "\n" +
                ChatColor.GREEN + "Tiempo Sobrevivido: " + ChatColor.RESET + ChatColor.ITALIC + TimeSurvived/60 + "mins" + "\n" +
                ChatColor.GREEN + "Mejoras: " + ChatColor.RESET + ChatColor.ITALIC + Upgrades + "\n" +
                ChatColor.GREEN + "Efectos: " + ChatColor.RESET + ChatColor.ITALIC + Effects + "\n" +
                ChatColor.GREEN + "Esquivos: " + ChatColor.RESET + ChatColor.ITALIC + dodges + ChatColor.RESET + ChatColor.AQUA + ChatColor.BOLD + " 💨" +  "\n";
    }

    public void resetStats() {
        Kills = 0;
        Upgrades = 1;
        TimeSurvived = 0;
        Effects = 0;
        dodges = 0;
        saveStats();
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getEffects() {
        return Effects;
    }

    public int getDodges() {
        loadStats();
        return dodges;
    }

    public void setDodges(int dodges){
        this.dodges = dodges;
        saveStats();
    }
}
