package pandoClass.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.archer.Archer;
import pandoClass.assasin.Assasin;
import pandoClass.tank.Tank;
import pandodungeons.pandodungeons.PandoDungeons;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static pandodungeons.pandodungeons.Utils.FileUtils.getRpgPlayersFile;



public class RPGPlayerDataManager {
    private static final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File DATA_FOLDER = getRpgPlayersFile();

    static {
        if (!DATA_FOLDER.exists()) {
            DATA_FOLDER.mkdirs();
        }
    }

    public static void save(RPGPlayer player) {
        File file = new File(DATA_FOLDER, player.getPlayerUUID() + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(player, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RPGPlayer load(Player player) {
        UUID uuid = player.getUniqueId();
        File file = new File(DATA_FOLDER, uuid + ".json");
        if (!file.exists()) {
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            RPGPlayer returned = GSON.fromJson(reader, RPGPlayer.class);
            plugin.rpgPlayersList.put(returned, getClass(returned));
            return returned;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<RPGPlayer> loadAllPlayers() {
        List<RPGPlayer> players = new ArrayList<>();
        File[] files = DATA_FOLDER.listFiles((dir, name) -> name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    RPGPlayer player = GSON.fromJson(reader, RPGPlayer.class);
                    players.add(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return players;
    }

    public static Map<RPGPlayer, ClassRPG> getRPGPlayerMap(){
        Map<RPGPlayer, ClassRPG> aux = new HashMap<>();
        for(RPGPlayer player : loadAllPlayers()){
            ClassRPG classRPG = null;
            String classKey = player.getClassKey();
            if(classKey.equalsIgnoreCase("TankClass")){
                classRPG = new Tank(player);
            }else if(classKey.equalsIgnoreCase("ArcherClass")){
                classRPG = new Archer(player);
            }else if(classKey.equalsIgnoreCase("AssassinClass")){
                classRPG = new Assasin(player);
            }

            aux.put(player,classRPG);

        }
        return aux;
    }

    public static ClassRPG getClass(RPGPlayer player){
        ClassRPG classRPG = null;
        String classKey = player.getClassKey();
        if(classKey.equalsIgnoreCase("TankClass")){
            classRPG = new Tank(player);
        }else if(classKey.equalsIgnoreCase("ArcherClass")){
            classRPG = new Archer(player);
        }else if(classKey.equalsIgnoreCase("AssassinClass")){
            classRPG = new Assasin(player);
        }
        return classRPG;
    }
}
