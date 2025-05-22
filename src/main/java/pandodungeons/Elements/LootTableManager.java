package pandodungeons.Elements;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pandodungeons.Game.Stats;

import java.util.*;

import static pandodungeons.Utils.ItemUtils.comperGum;
import static pandodungeons.Utils.ItemUtils.soulWritter;

public class LootTableManager {

    private static final List<LootItem> lootTable = new ArrayList<>();
    private static final Random random = new Random();

    static {
        // Añadir ítems a la tabla de botín con sus probabilidades y cantidades
        addItemToLootTable(createItem(Material.GOLDEN_CARROT, "Zanahoria Dorada", 5), 9);
        addItemToLootTable(createItem(Material.ENDER_CHEST, "Ender Chest", 1), 1);
        addItemToLootTable(createItem(Material.ENDER_EYE, "Ojo de Ender", 5), 12);
        addItemToLootTable(createItem(Material.HOPPER, "Tolva", 1), 15);
        addItemToLootTable(createItem(Material.COAL_BLOCK, "Bloque de Carbón", 16), 15);
        addItemToLootTable(createItem(Material.SEA_LANTERN, "Linterna Marina", 5), 12);
        addItemToLootTable(createItem(Material.RAW_IRON_BLOCK, "Hierro", 16), 30);
        addItemToLootTable(createItem(Material.RAW_GOLD_BLOCK, "Oro", 16), 20);
        addItemToLootTable(createItem(Material.HEART_OF_THE_SEA, "Corazon del Mar", 1), 7);
        addItemToLootTable(createItem(Material.RAW_COPPER_BLOCK, "Cobre", 16), 30);
        addItemToLootTable(createItem(Material.ANCIENT_DEBRIS, "Resto ancestral", 8), 10);
        addItemToLootTable(createItem(Material.REDSTONE_ORE, "Mineral de redstone", 16), 20);
        addItemToLootTable(createItem(Material.EMERALD, "Esmeralda", 26), 15);
        addItemToLootTable(createItem(Material.PRISMARINE, "Prismarina", 36), 20);
        addItemToLootTable(createItem(Material.DARK_PRISMARINE, "Prismarina Oscura", 36), 20);
        addItemToLootTable(createItem(Material.NAUTILUS_SHELL, "Caparazon de Nautilo", 6), 15);
        addItemToLootTable(createItem(Material.WIND_CHARGE, "Carga de Viento", 16), 15);
        addItemToLootTable(createItem(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, "Mejora de netherita", 1), 3);
        addItemToLootTable(comperGum(1), 10);
        addItemToLootTable(createItem(Material.SHULKER_SHELL,  "Caparazon de Shulker", 6), 10);
        addItemToLootTable(createItem(Material.ENCHANTED_GOLDEN_APPLE, "Manzana Dorada Encantada", 7), 2);
        addItemToLootTable(createItem(Material.TOTEM_OF_UNDYING, "Tótem de la Inmortalidad", 2), 7);
        addItemToLootTable(createItem(Material.HEARTBREAK_POTTERY_SHERD, "Trozo de ceramica", 8), 8);
        addItemToLootTable(createItem(Material.DANGER_POTTERY_SHERD, "Trozo de ceramica", 8), 11);
        addItemToLootTable(createItem(Material.PRIZE_POTTERY_SHERD, "Trozo de ceramica", 8), 11);
        addItemToLootTable(createItem(Material.LAPIS_BLOCK, "Bloque del mineral este que no se como se escribe en español", 10), 11);
        addItemToLootTable(createItem(Material.ANGLER_POTTERY_SHERD, "Trozo de ceramica", 8), 11);
        addItemToLootTable(createEnchantedBook(Enchantment.SWIFT_SNEAK, 3, 1, "Libro encantado"), 6);
        addItemToLootTable(createEnchantedBook(Enchantment.SWEEPING_EDGE, 3, 1, "Libro encantado"), 3);
        addItemToLootTable(createEnchantedBook(Enchantment.SOUL_SPEED, 3, 1, "Libro encantado"), 6);
        addItemToLootTable(createEnchantedBook(Enchantment.MENDING, 1, 1, "Libro encantado"), 6);
        addItemToLootTable(createEnchantedBook(Enchantment.WIND_BURST, 2, 1, "Libro encantado"), 6);
        addItemToLootTable(createEnchantedBook(Enchantment.UNBREAKING, 2, 1, "Libro encantado"), 6);
        addItemToLootTable(createEnchantedBook(Enchantment.EFFICIENCY, 2, 1, "Libro encantado"), 8);
        addItemToLootTable(createEnchantedBook(Enchantment.SHARPNESS, 2, 1, "Libro encantado"), 8);
        addItemToLootTable(soulWritter(1),6);
        addItemToLootTable(createCustomRabbitFoot(2), 2);
        addItemToLootTable(createCustomChickenEgg(3), 3);
        addItemToLootTable(createCustomGacha(1), 1);
        addItemToLootTable(createItem(Material.TNT, "Tnt", 37), 10);
        addItemToLootTable(createItem(Material.WITHER_SKELETON_SKULL, "Cabeza de Wither", 2), 5);
        addItemToLootTable(createItemNoName(Material.OMINOUS_TRIAL_KEY, 1), 11);
        addItemToLootTable(createItemNoName(Material.OMINOUS_TRIAL_KEY, 1), 15);
    }

    // Método para crear un ítem con un nombre personalizado y una cantidad específica
    private static @NotNull ItemStack createItem(Material material, String name, int amount) {
        ItemStack item = new ItemStack(material, amount);
        name = ChatColor.BOLD + name;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setRarity(ItemRarity.EPIC);
            item.setItemMeta(meta);
        }
        return item;
    }

    // Método para crear un ítem con un nombre personalizado y una cantidad específica
    private static @NotNull ItemStack createItemNoName(Material material, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        return item;
    }

    // Método para crear un libro encantado con una cantidad específica
    private static @NotNull ItemStack createEnchantedBook(Enchantment enchantment, int level, int amount, String name) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, amount);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        if (meta != null) {
            meta.addStoredEnchant(enchantment, level, true);
            meta.setDisplayName(name);
            book.setItemMeta(meta);
        }
        return book;
    }

    // Método para crear el ítem personalizado "Rabinio" con una cantidad específica
    public static @NotNull ItemStack createCustomRabbitFoot(int amount) {
        ItemStack rabbitFoot = new ItemStack(Material.RABBIT_FOOT, amount);
        ItemMeta meta = rabbitFoot.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(200);
            meta.addEnchant(Enchantment.INFINITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            meta.setDisplayName(ChatColor.GRAY.toString() + ChatColor.BOLD + "    Dungeon Rabinio");
            meta.setLore(Arrays.asList(
                    "",
                    "§7Esta muy sucio por la tierra de la dungeon",
                    "§7Quizá alguien me lo pueda cambiar"
            ));
            rabbitFoot.setItemMeta(meta);
        }

        return rabbitFoot;
    }

    // Método para crear el ítem personalizado "Rabinio" con una cantidad específica
    public static @NotNull ItemStack createCustomGacha(int amount) {
        ItemStack rabbitFoot = new ItemStack(Material.SUNFLOWER, amount);
        ItemMeta meta = rabbitFoot.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(200);
            meta.addEnchant(Enchantment.INFINITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "    Dungeon Gacha");
            meta.setLore(Arrays.asList(
                    "",
                    "§7Esta muy sucio por la tierra de la dungeon",
                    "§7Quizá alguien me lo pueda cambiar"
            ));
            rabbitFoot.setItemMeta(meta);
        }

        return rabbitFoot;
    }

    // Método para crear el ítem personalizado "Safari Ether" con una cantidad específica
    public static @NotNull ItemStack createCustomChickenEgg(int amount) {
        ItemStack chickenEgg = new ItemStack(Material.EGG, amount);
        ItemMeta meta = chickenEgg.getItemMeta();

        if (meta != null) {
            meta.addEnchant(Enchantment.INFINITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            meta.setDisplayName("§7§lSafari §8§lEther");
            meta.setLore(Arrays.asList(
                    "",
                    "§7Esta muy sucio por la tierra de la dungeon",
                    "§7Quizá alguien me lo pueda cambiar"
            ));
            chickenEgg.setItemMeta(meta);
        }

        return chickenEgg;
    }

    // Método para añadir un ítem a la tabla de botín con una probabilidad
    private static void addItemToLootTable(ItemStack item, int probability) {
        lootTable.add(new LootItem(item, probability));
    }

    // Método para obtener un ítem aleatorio de la tabla de botín basado en las probabilidades
    public static ItemStack getRandomLoot(Player player) {
        boolean hasLuck = player.hasPotionEffect(PotionEffectType.LUCK);
        int lvl = 0;
        if(hasLuck){
            lvl = Objects.requireNonNull(player.getPotionEffect(PotionEffectType.LUCK)).getAmplifier();
        }

        int totalWeight = 0;

        for (LootItem lootItem : lootTable) {
            int probability = lootItem.getProbability();
            if (hasLuck && probability <= 10) {
                // Incrementar la probabilidad para ítems con probabilidad <= 10
                probability += 5 + (2 * lvl); // Ajustar este valor según sea necesario
            }
            totalWeight += probability;
        }

        int randomIndex = random.nextInt(totalWeight);
        int currentWeight = 0;

        for (LootItem lootItem : lootTable) {
            int probability = lootItem.getProbability();
            if (hasLuck && probability <= 10) {
                probability += 5; // Ajustar este valor según sea necesario
            }
            currentWeight += probability;
            if (randomIndex < currentWeight) {
                return lootItem.getItem();
            }
        }
        // Devolver un ítem por defecto en caso de error
        return new ItemStack(Material.COOKIE);
    }

    // Método para obtener toda la tabla de botín (opcional)
    @Contract(value = " -> new", pure = true)
    public static @NotNull List<LootItem> getLootTable() {
        return new ArrayList<>(lootTable);
    }


    public static void giveLootToPlayerList(List<Player> list){
        for(Player player : list){
            giveRandomLoot(player);
        }
    }

    public static void giveRandomLoot(Player player) {
        // Crear un nuevo cofre
        ItemStack chestItem = new ItemStack(Material.CHEST);
        BlockStateMeta chestMeta = (BlockStateMeta) chestItem.getItemMeta();
        if (chestMeta != null) {
            BlockState state = chestMeta.getBlockState();
            if (state instanceof Chest) {
                Chest chest = (Chest) state;

                // Nombrar el cofre
                chestMeta.setDisplayName(ChatColor.DARK_GREEN.toString() + "Premios");

                // Llenar el cofre con ítems aleatorios de la loot table
                // Un premio + cada prestigio
                Stats playerStats = Stats.fromPlayer(player);
                int playerPrestige = playerStats.prestige();
                int prestigeLeft = 0;
                if(playerPrestige >= chest.getInventory().getSize()){
                    prestigeLeft = playerPrestige - chest.getInventory().getSize();
                    playerPrestige = chest.getInventory().getSize() - 1;
                }
                for (int i = 0; i < (playerPrestige + 1); i++) {
                    ItemStack loot = LootTableManager.getRandomLoot(player);
                    chest.getInventory().addItem(loot);
                }
                chestMeta.setBlockState(chest);
                chestItem.setItemMeta(chestMeta);
                if(prestigeLeft > 0){
                    giveRandomLootByNumber(player, prestigeLeft);
                }
            }
        }

        // Dar el cofre al jugador
        player.getInventory().addItem(chestItem);
        player.sendMessage(ChatColor.GOLD + "¡Has recibido un cofre con premios!");
    }

    public static void giveRandomLootByNumber(Player player, int lootAmount) {
        // Crear un nuevo cofre
        ItemStack chestItem = new ItemStack(Material.CHEST);
        BlockStateMeta chestMeta = (BlockStateMeta) chestItem.getItemMeta();
        if (chestMeta != null) {
            BlockState state = chestMeta.getBlockState();
            if (state instanceof Chest) {
                Chest chest = (Chest) state;

                // Nombrar el cofre
                chestMeta.setDisplayName(ChatColor.DARK_GREEN.toString() + "Premios");
                int prestigeLeft = 0;
                if(lootAmount >= chest.getInventory().getSize()){
                    prestigeLeft = lootAmount - chest.getInventory().getSize();
                    lootAmount = chest.getInventory().getSize() - 1;
                }
                for (int i = 0; i < (lootAmount + 1); i++) {
                    ItemStack loot = LootTableManager.getRandomLoot(player);
                    chest.getInventory().addItem(loot);
                }
                chestMeta.setBlockState(chest);
                chestItem.setItemMeta(chestMeta);
                if(prestigeLeft > 0){
                    giveRandomLootByNumber(player, prestigeLeft);
                }
            }
        }

        // Dar el cofre al jugador
        player.getInventory().addItem(chestItem);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "¡Has recibido otro cofre con premios!");
    }

    // Método para obtener el nombre de un ítem
    public static String getItemName(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        return item.getType().name();
    }

    // Clase interna para representar un ítem con su probabilidad
    private static class LootItem {
        private final ItemStack item;
        private final int probability;

        public LootItem(ItemStack item, int probability) {
            this.item = item;
            this.probability = probability;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getProbability() {
            return probability;
        }
    }
}
