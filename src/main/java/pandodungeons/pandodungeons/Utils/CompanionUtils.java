package pandodungeons.pandodungeons.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pandodungeons.pandodungeons.CustomEntities.CompanionLoops.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static pandodungeons.pandodungeons.Utils.FileUtils.getCompanionsFile;
import static pandodungeons.pandodungeons.Utils.ItemUtils.*;

public class CompanionUtils {
    private static final File DATA_FOLDER = getCompanionsFile();
    private static final JSONParser parser = new JSONParser();

    private static final Map<UUID, Map<String, CompanionData>> playerCompanions = new HashMap<>();
    private static final Set<String> COMPANION_TYPES = Set.of(
            "allay", "breeze", "armadillo", "oso", "sniffer"
    );

    private static class CompanionData {
        int level;
        String type;

        CompanionData(int level, String type) {
            this.level = level;
            this.type = type;
        }

        JSONObject toJSON() {
            JSONObject obj = new JSONObject();
            obj.put("level", level);
            obj.put("type", type);
            return obj;
        }

        static CompanionData fromJSON(JSONObject obj) {
            return new CompanionData(((Long) obj.get("level")).intValue(), (String) obj.get("type"));
        }

        public void setLevel(int level){
            this.level = level;
        }
    }

    public static void loadAllCompanions() {
        if (!DATA_FOLDER.exists()) {
            DATA_FOLDER.mkdirs(); // Create folder if it doesn't exist
        }

        for (File file : DATA_FOLDER.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                try (FileReader reader = new FileReader(file)) {
                    // Check if the file is empty
                    if (file.length() == 0) {
                        Bukkit.getLogger().warning("File is empty: " + file.getName());
                        continue;
                    }
                    JSONObject json = (JSONObject) parser.parse(reader);
                    UUID uuid = UUID.fromString(file.getName().replace(".json", ""));
                    Map<String, CompanionData> companions = new HashMap<>();

                    for (Object key : json.keySet()) {
                        String companionType = (String) key;
                        JSONObject data = (JSONObject) json.get(companionType);
                        companions.put(companionType, CompanionData.fromJSON(data));
                    }

                    playerCompanions.put(uuid, companions);
                } catch (IOException e) {
                    Bukkit.getLogger().severe("IOException while loading companions from file: " + file.getName());
                    e.printStackTrace();
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("ParseException while loading companions from file: " + file.getName());
                    e.printStackTrace();
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Unexpected error while loading companions from file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }


    public static void loadCompanions(Player player) {
        UUID uuid = player.getUniqueId();
        File playerFile = getPlayerFile(uuid);

        if (playerFile.exists()) {
            if (playerFile.length() > 0) {  // Verificar si el archivo no está vacío
                try (FileReader reader = new FileReader(playerFile)) {
                    JSONObject json = (JSONObject) parser.parse(reader);
                    Map<String, CompanionData> companions = new HashMap<>();

                    for (Object key : json.keySet()) {
                        String companionType = (String) key;
                        JSONObject data = (JSONObject) json.get(companionType);
                        companions.put(companionType, CompanionData.fromJSON(data));
                    }

                    playerCompanions.put(uuid, companions);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            } else {
                playerCompanions.put(uuid, new HashMap<>());
            }
        } else {
            playerCompanions.put(uuid, new HashMap<>());
        }
    }

    public static void saveCompanions(Player player) {
        UUID uuid = player.getUniqueId();
        Map<String, CompanionData> companions = playerCompanions.getOrDefault(uuid, new HashMap<>());
        JSONObject json = new JSONObject();

        companions.forEach((type, data) -> json.put(type, data.toJSON()));

        if (!DATA_FOLDER.exists()) {
            DATA_FOLDER.mkdirs(); // Crea la carpeta y sus subdirectorios si no existen
        }

        File playerFile = getPlayerFile(uuid);

        try (FileWriter writer = new FileWriter(playerFile)) {
            writer.write(json.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getPlayerFile(UUID uuid) {
        if (!DATA_FOLDER.exists()) {
            DATA_FOLDER.mkdirs(); // Create the folder if it doesn't exist
        }

        File playerFile = new File(DATA_FOLDER, uuid.toString() + ".json");
        try {
            if (!playerFile.exists()) {
                playerFile.createNewFile(); // Create the file if it doesn't exist
                // Initialize the file with an empty JSON object
                try (FileWriter writer = new FileWriter(playerFile)) {
                    writer.write("{}");
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("IOException while creating player file: " + playerFile.getName());
            e.printStackTrace();
        }
        return playerFile;
    }



    public static String searchCompanionType(String input) {
        String lowerCaseInput = input.toLowerCase(Locale.ROOT);

        for (String companionType : COMPANION_TYPES) {
            if (lowerCaseInput.contains(companionType.toLowerCase(Locale.ROOT))) {
                return companionType;
            }
        }
        return null; // Retorna null si no se encuentra ningún tipo de companion
    }

    public static boolean isCompanionType(String companionType) {
        for (String string : COMPANION_TYPES) {
            if (companionType.toLowerCase(Locale.ROOT).contains(string)) {
                return true;
            }
        }

        return COMPANION_TYPES.contains(companionType.toLowerCase(Locale.ROOT));
    }

    public static void unlockCompanion(Player player, String companionType, int level) {
        loadCompanions(player);
        if(!isCompanionType(companionType)){
            player.sendMessage(ChatColor.RED + "Ese tipo de acompañante no existe");
            return;
        }
        if(hasUnlockedCompanion(player, companionType)){
            return;
        }
        UUID uuid = player.getUniqueId();
        Map<String, CompanionData> companions = playerCompanions.computeIfAbsent(uuid, k -> new HashMap<>());
        companions.put(companionType.toLowerCase(Locale.ROOT), new CompanionData(level, companionType.toLowerCase(Locale.ROOT)));

        saveCompanions(player);
        player.sendMessage(ChatColor.GREEN + "Haz desbloqueado el acompañante: " + companionType);
    }

    public static boolean hasSelectedCompanion(Player player) {
        loadCompanions(player);
        UUID uuid = player.getUniqueId();
        Map<String, CompanionData> companions = playerCompanions.getOrDefault(uuid, new HashMap<>());
        return companions.containsKey("selected");
    }


    public static void unlockRandomCompanion(Player player) {
        loadCompanions(player);
        UUID uuid = player.getUniqueId();
        Map<String, CompanionData> companions = playerCompanions.getOrDefault(uuid, new HashMap<>());

        // Obtener todos los tipos de companions no desbloqueados
        List<String> lockedCompanions = new ArrayList<>();
        for (String companionType : COMPANION_TYPES) {
            if (!companions.containsKey(companionType)) {
                lockedCompanions.add(companionType);
            }
        }

        // Verificar si ya tiene todos los companions desbloqueados
        if (lockedCompanions.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Ya tienes todos los acompañantes desbloqueados.");
            return;
        }

        // Seleccionar un companion aleatorio no desbloqueado
        String randomCompanionType = lockedCompanions.get(new Random().nextInt(lockedCompanions.size()));
        int initialLevel = 1; // Puedes cambiar el nivel inicial si es necesario

        // Desbloquear el companion aleatorio
        companions.put(randomCompanionType.toLowerCase(Locale.ROOT), new CompanionData(initialLevel, randomCompanionType.toLowerCase(Locale.ROOT)));
        playerCompanions.put(uuid, companions);

        saveCompanions(player);
        player.sendMessage(ChatColor.GREEN + "Has desbloqueado un acompañante aleatorio: " + randomCompanionType);
    }


    public static boolean hasUnlockedCompanion(Player player, String companionType) {
        loadCompanions(player);
        UUID uuid = player.getUniqueId();
        Map<String, CompanionData> companions = playerCompanions.getOrDefault(uuid, new HashMap<>());
        return companions.containsKey(companionType.toLowerCase(Locale.ROOT));
    }

    public static int getCompanionLevel(Player player, String companionType) {
        loadCompanions(player);
        UUID uuid = player.getUniqueId();
        Map<String, CompanionData> companions = playerCompanions.getOrDefault(uuid, new HashMap<>());
        CompanionData companionData = companions.get(companionType.toLowerCase(Locale.ROOT));
        return companionData != null ? companionData.level : 0;
    }

    public static void addCompanionLevel(Player player, String companionType) {
        loadCompanions(player);
        UUID uuid = player.getUniqueId();
        Map<String, CompanionData> companions = playerCompanions.getOrDefault(uuid, new HashMap<>());
        CompanionData companionData = companions.get(companionType.toLowerCase(Locale.ROOT));
        companionData.level++;
        saveCompanions(player);
    }

    public static String getSelectedCompanion(Player player) {
        loadCompanions(player);
        UUID uuid = player.getUniqueId();
        Map<String, CompanionData> companions = playerCompanions.getOrDefault(uuid, new HashMap<>());
        return companions.entrySet().stream()
                .filter(entry -> entry.getKey().equals("selected"))
                .findFirst()
                .map(Map.Entry::getValue)
                .map(companionData -> companionData.type)
                .orElse(null);
    }

    /**
     *
     * @param player Crea un UI de trade para desbloquear companions
     */
    public static void openUnlockCompanionMenu(Player player) {
        Merchant merchant = Bukkit.createMerchant(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "¡Desbloquea compañeros!");

        List<MerchantRecipe> recipes = new ArrayList<>();

        ItemStack result1 = armadilloFragmentItem(1);
        MerchantRecipe trade1 = new MerchantRecipe(result1, 0, 10000, false);
        trade1.addIngredient(new ItemStack(Material.BEACON, 1));
        trade1.addIngredient(new ItemStack(Material.ARMADILLO_SCUTE, 10));
        recipes.add(trade1);


        ItemStack result2 = breezeFragmentItem(3);
        MerchantRecipe trade2 = new MerchantRecipe(result2, 0, 10000, false);
        trade2.addIngredient(new ItemStack(Material.DRAGON_EGG, 1));
        trade2.addIngredient(new ItemStack(Material.BREEZE_ROD, 10));
        recipes.add(trade2);

        ItemStack result3 = allayFragmentItem(1);
        MerchantRecipe trade3 = new MerchantRecipe(result3, 0, 10000, false);
        trade3.addIngredient(new ItemStack(Material.HEART_OF_THE_SEA, 4));
        trade3.addIngredient(new ItemStack(Material.PITCHER_PLANT, 10));
        recipes.add(trade3);

        ItemStack result4 = osoFragmentItem(1);
        MerchantRecipe trade4 = new MerchantRecipe(result4, 0, 10000, false);
        trade4.addIngredient(new ItemStack(Material.HONEY_BLOCK, 10));
        trade4.addIngredient(polarBearFur(16));
        recipes.add(trade4);

        ItemStack result5 = snifferFragmentItem(1);
        MerchantRecipe trade5 = new MerchantRecipe(result5, 0, 10000, false);
        trade5.addIngredient(new ItemStack(Material.TURTLE_SCUTE, 10));
        trade5.addIngredient(new ItemStack(Material.SNIFFER_EGG, 5));
        recipes.add(trade5);

        merchant.setRecipes(recipes);

        player.openMerchant(merchant, true);
    }


    public static void selectCompanion(Player player, String companionType) {
        loadCompanions(player);
        if (hasUnlockedCompanion(player, companionType.toLowerCase(Locale.ROOT))) {
            UUID uuid = player.getUniqueId();
            Map<String, CompanionData> companions = playerCompanions.computeIfAbsent(uuid, k -> new HashMap<>());
            int level = getCompanionLevel(player, companionType);
            companions.put("selected", new CompanionData(level, companionType.toLowerCase(Locale.ROOT)));
            playerCompanions.put(uuid, companions); // Asegurar que el mapa se actualiza en playerCompanions
            saveCompanions(player);
        }
    }

    public static void summonSelectedCompanion(Player player) {
        loadCompanions(player);
        String selectedType = getSelectedCompanion(player);
        if (selectedType != null) {
            Companion compa;
            switch (selectedType.toLowerCase(Locale.ROOT)) {
                case "allay":
                    compa = new CompanionAllay(player);
                    break;
                case "breeze":
                    compa = new CompanionBreeze(player);
                    break;
                case "armadillo":
                    compa = new CompanionArmadillo(player);
                    break;
                case "oso":
                    compa = new CompanionPolarBear(player);
                    break;
                case "sniffer":
                    compa = new CompanionSniffer(player);
                    break;
                default:
                    // Handle if companion type is not recognized
                    break;
            }
        }
    }

    public static Map<String, Integer> getUnlockedCompanions(Player player) {
        loadCompanions(player);
        Map<String, Integer> unlockedCompanions = new HashMap<>();
        UUID uuid = player.getUniqueId();
        Map<String, CompanionData> companions = playerCompanions.getOrDefault(uuid, new HashMap<>());

        companions.forEach((type, data) -> {
            if (!type.equals("selected")) {
                unlockedCompanions.put(type, data.level);
            }
        });

        return unlockedCompanions;
    }
}
