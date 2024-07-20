package pandodungeons.pandodungeons.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LocationUtils {
    private static final File playerLocationsFile = FileUtils.getPlayerLocationsFile();
    private static final File dungeonsDataFile = FileUtils.getDungeonsDataFile();

    public static JSONObject getPlayerLocationData(String playerUUID) {
        JSONObject playerLocations = loadPlayerLocations();
        return (JSONObject) playerLocations.get(playerUUID);
    }

    public static void savePlayerLocationData(String playerUUID, Location location, String dungeonID) {
        JSONObject playerLocations = loadPlayerLocations();
        JSONObject data = new JSONObject();
        data.put("world", location.getWorld().getName());
        data.put("x", location.getX());
        data.put("y", location.getY());
        data.put("z", location.getZ());
        data.put("dungeonID", dungeonID);
        playerLocations.put(playerUUID, data);
        saveLocationDataToFile(playerLocations, playerLocationsFile);
    }

    public static void removePlayerLocationData(String playerUUID) {
        JSONObject playerLocations = loadPlayerLocations();
        playerLocations.remove(playerUUID);
        saveLocationDataToFile(playerLocations, playerLocationsFile);
    }

    public static Location getLocationFromJSON(JSONObject jsonLocation) {
        String worldName = (String) jsonLocation.get("world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        double x = (double) jsonLocation.get("x");
        double y = (double) jsonLocation.get("y");
        double z = (double) jsonLocation.get("z");
        return new Location(world, x, y, z);
    }

    private static JSONObject loadPlayerLocations() {
        return loadDataFromFile(playerLocationsFile);
    }

    private static JSONObject loadDataFromFile(File file) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(file)) {
            return (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            return new JSONObject();
        }
    }

    private static void saveLocationDataToFile(JSONObject data, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Location> getAllDungeonLocations() {
        JSONObject data = loadDataFromFile(dungeonsDataFile);
        JSONArray dungeonArray = (JSONArray) data.get("dungeons");
        Map<String, Location> locations = new HashMap<>();
        if (dungeonArray != null) {
            for (Object obj : dungeonArray) {
                JSONObject dungeonData = (JSONObject) obj;
                String dungeonID = (String) dungeonData.get("dungeonID");
                JSONObject locationData = (JSONObject) dungeonData.get("location");
                Location location = getLocationFromJSON(locationData);
                if (location != null) {
                    locations.put(dungeonID, location);
                }
            }
        }
        return locations;
    }
    public static List<String> getAllDungeonWorlds() {
        JSONObject data = loadDataFromFile(dungeonsDataFile);
        JSONArray dungeonArray = (JSONArray) data.get("dungeons");
        List<String> dungeonWorlds = new ArrayList<>();

        if (dungeonArray != null) {
            for (Object obj : dungeonArray) {
                JSONObject dungeonData = (JSONObject) obj;
                JSONObject locationData = (JSONObject) dungeonData.get("location");

                if (locationData != null) { // Verificación de null
                    String worldName = (String) locationData.get("world");
                    if (worldName != null) { // Verificación de null
                        dungeonWorlds.add(worldName);
                    }
                }
            }
        }
        return dungeonWorlds;
    }

    public static boolean isDungeonWorld(String worldName) {
        List<String> dungeonWorlds = getAllDungeonWorlds();
        return dungeonWorlds.contains(worldName);
    }

    public static Location getDungeonLocation(String playerUUID) {
        JSONObject playerLocationData = getPlayerLocationData(playerUUID);
        if (playerLocationData == null) {
            return null;
        }
        String dungeonID = (String) playerLocationData.get("dungeonID");
        return getDungeonLocationById(dungeonID);
    }

    public static Location getDungeonLocationById(String dungeonID) {
        JSONObject data = loadDataFromFile(dungeonsDataFile);
        JSONArray dungeonArray = (JSONArray) data.get("dungeons");
        if (dungeonArray != null) {
            for (Object obj : dungeonArray) {
                JSONObject dungeonData = (JSONObject) obj;
                String storedDungeonID = (String) dungeonData.get("dungeonID");
                if (storedDungeonID.equals(dungeonID)) {
                    JSONObject locationData = (JSONObject) dungeonData.get("location");
                    return getLocationFromJSON(locationData);
                }
            }
        }
        return null;
    }

    public static void saveDungeonLocationData(String dungeonID, Location location) {
        JSONObject data = loadDataFromFile(dungeonsDataFile);
        JSONArray dungeonArray = (JSONArray) data.get("dungeons");
        if (dungeonArray == null) {
            dungeonArray = new JSONArray();
            data.put("dungeons", dungeonArray);
        }
        JSONObject dungeonData = new JSONObject();
        dungeonData.put("dungeonID", dungeonID);
        dungeonData.put("location", getLocationJSONObject(location));
        dungeonArray.add(dungeonData);
        saveLocationDataToFile(data, dungeonsDataFile);
    }

    private static JSONObject getLocationJSONObject(Location location) {
        JSONObject jsonLocation = new JSONObject();
        jsonLocation.put("world", location.getWorld().getName());
        jsonLocation.put("x", location.getX());
        jsonLocation.put("y", location.getY());
        jsonLocation.put("z", location.getZ());
        return jsonLocation;
    }

    public static void removeDungeonLocationData(String dungeonID) {
        JSONObject data = loadDataFromFile(dungeonsDataFile);
        JSONArray dungeonArray = (JSONArray) data.get("dungeons");
        if (dungeonArray != null) {
            dungeonArray.removeIf(obj -> {
                JSONObject dungeonData = (JSONObject) obj;
                return dungeonID.equals(dungeonData.get("dungeonID"));
            });
            saveLocationDataToFile(data, dungeonsDataFile);
        }
    }

    public static boolean hasActiveDungeon(String playerUUID) {
        JSONObject playerLocations = loadPlayerLocations();
        JSONObject data = (JSONObject) playerLocations.get(playerUUID);
        return data != null && data.containsKey("dungeonID");
    }


    public static List<Location> getAllDungeonRoomLocations(String dungeonID) {
        dungeonID = dungeonID; // Ensure dungeonID consistency
        Bukkit.getLogger().info("Loading rooms for dungeon ID: " + dungeonID); // Log

        List<Location> roomLocations = new ArrayList<>();
        JSONObject data = loadDataFromFile(dungeonsDataFile);
        JSONArray dungeonArray = (JSONArray) data.get("dungeons");
        if (dungeonArray != null) {
            for (Object obj : dungeonArray) {
                JSONObject dungeonData = (JSONObject) obj;
                String storedDungeonID = (String) dungeonData.get("dungeonID");
                if (storedDungeonID.equals(dungeonID)) {
                    JSONArray roomsArray = (JSONArray) dungeonData.get("rooms");
                    if (roomsArray != null) {
                        for (Object roomObj : roomsArray) {
                            JSONObject roomData = (JSONObject) roomObj;
                            Location roomLocation = getLocationFromJSON(roomData);
                            if (roomLocation != null) {
                                roomLocations.add(roomLocation);
                            }
                        }
                    }
                    break;
                }
            }
        }

        Bukkit.getLogger().info("Loaded rooms for dungeon ID: " + dungeonID + " - Total rooms: " + roomLocations.size()); // Log
        return roomLocations;
    }


    public static void saveDungeonRoomLocations(String dungeonID, List<Location> roomLocations) {
        dungeonID = "dungeon_" + dungeonID;
        Bukkit.getLogger().info("Saving rooms for dungeon ID: " + dungeonID); // Log

        JSONObject data = loadDataFromFile(dungeonsDataFile);
        JSONArray dungeonArray = (JSONArray) data.get("dungeons");
        if (dungeonArray == null) {
            dungeonArray = new JSONArray();
            data.put("dungeons", dungeonArray);
        }

        JSONObject dungeonDataToUpdate = null;
        for (Object obj : dungeonArray) {
            JSONObject dungeonData = (JSONObject) obj;
            String storedDungeonID = (String) dungeonData.get("dungeonID");
            if (storedDungeonID.equals(dungeonID)) {
                dungeonDataToUpdate = dungeonData;
                break;
            }
        }

        if (dungeonDataToUpdate == null) {
            dungeonDataToUpdate = new JSONObject();
            dungeonDataToUpdate.put("dungeonID", dungeonID);
            dungeonArray.add(dungeonDataToUpdate);
        }

        JSONArray roomsArray = new JSONArray();
        for (Location roomLocation : roomLocations) {
            roomsArray.add(getLocationJSONObject(roomLocation));
        }

        dungeonDataToUpdate.put("rooms", roomsArray);
        saveLocationDataToFile(data, dungeonsDataFile);

        Bukkit.getLogger().info("Rooms saved for dungeon ID: " + dungeonID + " - Total rooms: " + roomsArray.size()); // Log
    }

    public static Player findNearestPlayer(World world, Location location) {
        double nearestDistanceSquared = Double.MAX_VALUE;
        Player nearestPlayer = null;

        for (Player player : world.getPlayers()) {
            double distanceSquared = player.getLocation().distanceSquared(location);
            if (distanceSquared < nearestDistanceSquared) {
                nearestDistanceSquared = distanceSquared;
                nearestPlayer = player;
            }
        }

        return nearestPlayer;
    }
}
