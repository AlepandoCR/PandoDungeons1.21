package pandoClass.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.classes.archer.Archer;
import pandoClass.classes.assasin.Assasin;
import pandoClass.classes.tank.Tank;
import pandodungeons.pandodungeons.PandoDungeons;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static pandodungeons.pandodungeons.Utils.FileUtils.getRpgPlayersFile;

public class RPGPlayerDataManager {
    private final Gson GSON;
    private final File DATA_FOLDER;
    private final PandoDungeons plugin;

    public RPGPlayerDataManager(PandoDungeons plugin) {
        DATA_FOLDER = getRpgPlayersFile();
        if (!DATA_FOLDER.exists()) {
            DATA_FOLDER.mkdirs();
        }
        this.plugin = plugin;
        this.GSON = new GsonBuilder().setPrettyPrinting().create();
    }

    public void save(RPGPlayer player) {
        File file = new File(DATA_FOLDER, player.getPlayerUUID() + ".json");
        File backup = new File(DATA_FOLDER, player.getPlayerUUID() + ".backup.json");

        try {
            // Create a backup of the current file before writing a new one
            if (file.exists()) {
                Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(player, writer);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Error saving data for: " + player.getPlayerUUID());
            e.printStackTrace();
        }
    }

    public RPGPlayer load(Player player) {
        if(player == null){
            return null;
        }
        UUID uuid = player.getUniqueId();
        File file = new File(DATA_FOLDER, uuid + ".json");

        if (!file.exists()) {
            return null;
        }

        try (FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, RPGPlayer.class);
        } catch (IOException e) {
            plugin.getLogger().severe("Error loading data for: " + uuid);
            e.printStackTrace();
            return null;
        }
    }

    public List<RPGPlayer> loadAllPlayers() {
        List<RPGPlayer> players = new ArrayList<>();
        File[] files = DATA_FOLDER.listFiles((dir, name) -> name.endsWith(".json") && !name.contains(".backup"));

        if (files != null) {
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    RPGPlayer player = GSON.fromJson(reader, RPGPlayer.class);
                    if (player != null) {
                        players.add(player);
                    }
                } catch (IOException e) {
                    plugin.getLogger().severe("Error loading player data from file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }

        return players;
    }

    public Map<Player, ClassRPG> getRPGPlayerMap() {
        Map<Player, ClassRPG> rpgPlayerMap = new HashMap<>();
        for (RPGPlayer player : loadAllPlayers()) {
            ClassRPG classRPG = getClass(player);
            if (classRPG != null && player.getPlayer() != null) {
                rpgPlayerMap.put(player.getPlayer(), classRPG);
            }
        }
        return rpgPlayerMap;
    }

    public ClassRPG getClass(RPGPlayer player) {
        if (player == null || player.getClassKey() == null || player.getClassKey().isEmpty()) {
            return null;
        }

        String classKey = player.getClassKey();
        switch (classKey.toLowerCase()) {
            case "tankclass":
                return new Tank(player, plugin);
            case "archerclass":
                return new Archer(player, plugin);
            case "assassinclass":
                return new Assasin(player, plugin);
            default:
                return null;
        }
    }
}