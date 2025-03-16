package pandodungeons.pandodungeons.Utils;

import net.minecraft.world.damagesource.DamageTypes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.pandodungeons.PandoDungeons;

import javax.swing.plaf.SplitPaneUI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ItemUtils {

  private static final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

  private static final NamespacedKey soulUses = new NamespacedKey(plugin, "soulUses");
  private static final NamespacedKey soulWritter = new NamespacedKey(plugin, "soulWritter");
  private static final NamespacedKey garabiThor = new NamespacedKey(plugin, "garabiThor");
  private static final NamespacedKey bateria = new NamespacedKey(plugin, "bateria");

  public static ItemStack garabiThor(int amount) {
    ItemStack item = new ItemStack(Material.MACE, amount);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.AQUA.toString() + ChatColor.BOLD + "GarabiThor");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.GOLD.toString() + "Desata los poderes miticos de Garabito " + ChatColor.BOLD + "⚡");
    lore.add("");
    meta.setLore(lore);
    meta.getPersistentDataContainer().set(garabiThor, PersistentDataType.STRING, "garabiThor");
    meta.getPersistentDataContainer().set(bateria, PersistentDataType.DOUBLE, 1000D);
    meta.setCustomModelData(69);
    meta.setRarity(ItemRarity.EPIC);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack garabiThor(int amount, PandoDungeons plugin) {
    ItemStack item = new ItemStack(Material.MACE, amount);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.AQUA.toString() + ChatColor.BOLD + "GarabiThor");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.GOLD.toString() + "Desata los poderes miticos de Garabito " + ChatColor.BOLD + "⚡");
    lore.add("");
    meta.setLore(lore);
    NamespacedKey garabiThor = new NamespacedKey(plugin, "garabiThor");
    NamespacedKey bateria = new NamespacedKey(plugin, "bateria");
    meta.getPersistentDataContainer().set(garabiThor, PersistentDataType.STRING, "garabiThor");
    meta.getPersistentDataContainer().set(bateria, PersistentDataType.DOUBLE, 1000D);
    meta.setCustomModelData(69);
    meta.setRarity(ItemRarity.EPIC);
    item.setItemMeta(meta);
    return item;
  }

  public static double getBateria(ItemStack item) {
    if (isGarabiThor(item)) {
      ItemMeta meta = item.getItemMeta();
      return meta.getPersistentDataContainer().getOrDefault(bateria, PersistentDataType.DOUBLE, 0D);
    }
    return 0D;
  }

  public static void addBateria(ItemStack item) {
    Random random = new Random();
    double min = 75;
    double max = 150;

    double amount = min + (max - min) * random.nextDouble() + getBateria(item);

    if (amount > 1000) {
      amount = 1000;
    }

    if (isGarabiThor(item)) {
      ItemMeta meta = item.getItemMeta();
      meta.getPersistentDataContainer().set(bateria, PersistentDataType.DOUBLE, amount);
      item.setItemMeta(meta);
    }
  }

  public static void removeBatery(ItemStack item, double toReduce) {

    double amount = getBateria(item) - toReduce;
    if (amount < 0) {
      amount = 0;
    }
    if (isGarabiThor(item)) {
      ItemMeta meta = item.getItemMeta();
      meta.getPersistentDataContainer().set(bateria, PersistentDataType.DOUBLE, amount);
      item.setItemMeta(meta);
    }
  }

  public static void setBatery(ItemStack item, double toPut) {

    double amount = toPut;
    if (amount < 0) {
      amount = 0;
    }
    if (isGarabiThor(item)) {
      ItemMeta meta = item.getItemMeta();
      meta.getPersistentDataContainer().set(bateria, PersistentDataType.DOUBLE, amount);
      item.setItemMeta(meta);
    }
  }

  public static boolean isGarabiThor(ItemStack item) {
    if (item == null || item.getType().equals(Material.AIR)) {
      return false;
    }
    if (item.hasItemMeta()) {
      ItemMeta meta = item.getItemMeta();
      if (!item.getPersistentDataContainer().isEmpty()) {
        if (item.getPersistentDataContainer().getKeys().contains(garabiThor)) {
          String data = Objects.requireNonNull(meta.getPersistentDataContainer().get(garabiThor, PersistentDataType.STRING));
          return data.contains("garabiThor");
        }
      }
    }
    return false;
  }

  public static ItemStack soulWritter(int amount) {
    ItemStack item = new ItemStack(Material.FLOWER_BANNER_PATTERN, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Escritos de Minor");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.DARK_AQUA.toString() + "Ancla el alma de los seres a este mundo");
    lore.add(ChatColor.YELLOW + "Usos: " + ChatColor.GOLD + "150");
    lore.add("");
    meta.getPersistentDataContainer().set(soulWritter, PersistentDataType.STRING, "soulWritter");
    meta.getPersistentDataContainer().set(soulUses, PersistentDataType.INTEGER, 150);
    meta.setLore(lore);
    meta.setCustomModelData(69);
    meta.setRarity(ItemRarity.RARE);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack soulWritter(int amount, PandoDungeons plugin) {
    ItemStack item = new ItemStack(Material.FLOWER_BANNER_PATTERN, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Escritos de Minor");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.DARK_AQUA.toString() + "Ancla el alma de los seres a este mundo");
    lore.add(ChatColor.YELLOW + "Usos: " + ChatColor.GOLD + "150");
    lore.add("");
    NamespacedKey soulUses = new NamespacedKey(plugin, "soulUses");
    NamespacedKey soulWritter = new NamespacedKey(plugin, "soulWritter");
    meta.getPersistentDataContainer().set(soulWritter, PersistentDataType.STRING, "soulWritter");
    meta.getPersistentDataContainer().set(soulUses, PersistentDataType.INTEGER, 150);
    meta.setLore(lore);
    meta.setCustomModelData(69);
    meta.setRarity(ItemRarity.RARE);
    item.setItemMeta(meta);
    return item;
  }

  public static boolean isSoulWritter(ItemStack item) {
    if (item == null || item.getType().equals(Material.AIR)) {
      return false;
    }
    if (item.hasItemMeta()) {
      ItemMeta itemMeta = item.getItemMeta();
      if (!item.getPersistentDataContainer().isEmpty()) {
        if (item.getPersistentDataContainer().getKeys().contains(soulWritter)) {
          String data = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(soulWritter, PersistentDataType.STRING));
          return data.contains("soulWritter");
        }
      }
    }
    return false;
  }

  public static boolean playerHasSoulWritter(Player player){
    for(int i = 0; i <= player.getInventory().getSize(); i++){
      if(isSoulWritter(player.getInventory().getItem(i))){
        return true;
      }
    }
    return false;
  }

  public static ItemStack getSlayerSoulWritter(Player player){
    for(int i = 0; i <= player.getInventory().getSize(); i++){
      if(isSoulWritter(player.getInventory().getItem(i))){
        return player.getInventory().getItem(i);
      }
    }
    return null;
  }

  public static int getWritterUses(ItemStack item){
    if(isSoulWritter(item)){
      return item.getItemMeta().getPersistentDataContainer().getOrDefault(soulUses, PersistentDataType.INTEGER, 0);
    }
    return 0;
  }

  public static void reduceWritterUses(ItemStack item){
    int amount = getWritterUses(item) - 1;
    if(amount < 0){
      amount = 0;
    }
    if(isSoulWritter(item)){
      ItemMeta meta = item.getItemMeta();
      meta.getPersistentDataContainer().set(soulUses, PersistentDataType.INTEGER, amount);
      // Actualizar el lore con la cantidad de almas
      List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
      assert lore != null;
      boolean found = false;
      for (int i = 0; i < lore.size(); i++) {
        if (lore.get(i).startsWith(ChatColor.YELLOW.toString() + "Usos: ")) {
          lore.set(i, ChatColor.YELLOW.toString() + "Usos: " + ChatColor.GOLD + amount);
          found = true;
          break;
        }
      }
      if (!found) {
        lore.add(ChatColor.YELLOW.toString() + "Usos: " + ChatColor.GOLD + amount);
      }
      meta.setLore(lore);
      item.setItemMeta(meta);
    }
  }

  public static ItemStack polarBearFur(int amount){
    ItemStack item = new ItemStack(Material.SNOWBALL, amount);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.WHITE.toString() + ChatColor.BOLD + "Pelaje de Oso");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.DARK_GRAY.toString() + "Funciona en la mesa de crafteo ⚃");
    lore.add("");
    meta.setLore(lore);
    meta.setCustomModelData(69);
    meta.setRarity(ItemRarity.RARE);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack armadilloFragmentItem(int amount){
    ItemStack item = new ItemStack(Material.ARMADILLO_SCUTE, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.translateAlternateColorCodes('&', "&x&6&b&4&2&2&6&lFragmento de Armadillo"));
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GRAY.toString() + "Se utiliza para desbloquear al compañero Armadillo");
    lore.add(ChatColor.DARK_GRAY.toString() + "Funciona en la mesa de crafteo ⚃");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack snifferFragmentItem(int amount){
    ItemStack item = new ItemStack(Material.YELLOW_DYE, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Fragmento de Sniffer");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GRAY.toString() + "Se utiliza para desbloquear al compañero Sniffer");
    lore.add(ChatColor.DARK_GRAY.toString() + "Funciona en la mesa de crafteo ⚃");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack pufferFishFragment(int amount){
    ItemStack item = new ItemStack(Material.PUFFERFISH, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Fragmento de PufferFish");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GRAY.toString() + "Se utiliza para desbloquear al compañero PufferFish");
    lore.add(ChatColor.DARK_GRAY.toString() + "Funciona en la mesa de crafteo ⚃");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack breezeFragmentItem(int amount){
    ItemStack item = new ItemStack(Material.BREEZE_ROD, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Fragmento de Breeze");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GRAY.toString() + "Se utiliza para desbloquear al compañero Breeze");
    lore.add(ChatColor.DARK_GRAY.toString() + "Funciona en la mesa de crafteo ⚃");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack allayFragmentItem(int amount){
    ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Fragmento de Allay");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GRAY.toString() + "Se utiliza para desbloquear al compañero Allay");
    lore.add(ChatColor.DARK_GRAY.toString() + "Funciona en la mesa de crafteo ⚃");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack osoFragmentItem(int amount){
    ItemStack item = new ItemStack(Material.SNOWBALL, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.WHITE.toString() + ChatColor.BOLD + "Fragmento de Oso");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GRAY.toString() + "Se utiliza para desbloquear al compañero Oso");
    lore.add(ChatColor.DARK_GRAY.toString() + "Funciona en la mesa de crafteo ⚃");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack allayUnlockItem(int amount){
    ItemStack item = new ItemStack(Material.HEAVY_CORE, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.LIGHT_PURPLE + "Compañero Allay");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    meta.setCustomModelData(420);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GRAY.toString() + "Se utiliza para desbloquear al compañero Allay");
    lore.add("");
    lore.add(ChatColor.LIGHT_PURPLE.toString() + "Consumible \uD83D\uDDB1");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack snifferUnlockItem(int amount){
    ItemStack item = new ItemStack(Material.HEAVY_CORE, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.LIGHT_PURPLE + "Compañero Sniffer");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    meta.setCustomModelData(420);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GRAY.toString() + "Se utiliza para desbloquear al compañero Sniffer");
    lore.add("");
    lore.add(ChatColor.LIGHT_PURPLE.toString() + "Consumible \uD83D\uDDB1");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack pufferFishUnlockItem(int amount){
    ItemStack item = new ItemStack(Material.HEAVY_CORE, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.LIGHT_PURPLE + "Compañero Puffer Fish");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    meta.setCustomModelData(420);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GRAY.toString() + "Se utiliza para desbloquear al compañero Puffer Fish");
    lore.add("");
    lore.add(ChatColor.LIGHT_PURPLE.toString() + "Consumible \uD83D\uDDB1");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack breezeUnlockItem(int amount){
    ItemStack item = new ItemStack(Material.HEAVY_CORE, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.LIGHT_PURPLE + "Compañero Breeze");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    meta.setCustomModelData(420);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GRAY.toString() + "Se utiliza para desbloquear al compañero Breeze");
    lore.add("");
    lore.add(ChatColor.LIGHT_PURPLE.toString() + "Consumible \uD83D\uDDB1");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack armadilloUnlockItem(int amount){
    ItemStack item = new ItemStack(Material.HEAVY_CORE, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.LIGHT_PURPLE + "Compañero Armadillo");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    meta.setCustomModelData(420);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GRAY.toString() + "Se utiliza para desbloquear al compañero Armadillo");
    lore.add("");
    lore.add(ChatColor.LIGHT_PURPLE.toString() + "Consumible \uD83D\uDDB1");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack osoUnlockItem(int amount){
    ItemStack item = new ItemStack(Material.HEAVY_CORE, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.LIGHT_PURPLE + "Compañero Oso");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    meta.setCustomModelData(420);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GRAY.toString() + "Se utiliza para desbloquear al compañero Oso");
    lore.add("");
    lore.add(ChatColor.LIGHT_PURPLE.toString() + "Consumible \uD83D\uDDB1");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack physicalPrestige(int amount){
    ItemStack item = new ItemStack(Material.PUMPKIN_PIE, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Prestiño");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GOLD.toString() + "Vale por un prestigio ✦");
    lore.add("");
    lore.add(ChatColor.LIGHT_PURPLE.toString() + "Consumible \uD83D\uDDB1");
    lore.add(ChatColor.DARK_GRAY.toString() + "Funciona en la mesa de crafteo ⚃");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack comperGum(int amount){
    ItemStack item = new ItemStack(Material.RAW_COPPER, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Chicle de Cobre");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.LIGHT_PURPLE.toString() + "Muy rico sabor pero de corta duración");
    lore.add("");
    lore.add(ChatColor.LIGHT_PURPLE.toString() + "Consumible \uD83D\uDDB1");;
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack soccerBall(int amount){
    ItemStack item = new ItemStack(Material.POPPED_CHORUS_FRUIT, amount);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Bola");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GREEN.toString() + "Podrás jugar todo lo que quierás con ella!");
    lore.add("");
    lore.add(ChatColor.LIGHT_PURPLE.toString() + "Consumible \uD83D\uDDB1");;
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack physicalPrestigeNoAmount(){
    ItemStack item = new ItemStack(Material.PUMPKIN_PIE);
    item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setItemName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Prestiño");
    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add(ChatColor.GOLD.toString() + "Vale por un prestigio ✦");
    lore.add("");
    lore.add(ChatColor.LIGHT_PURPLE.toString() + "Consumible \uD83D\uDDB1");
    lore.add(ChatColor.DARK_GRAY.toString() + "Funciona en la mesa de crafteo ⚃");
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public static void allayCompanionCustomRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = allayUnlockItem(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "AllayUnlock");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', allayFragmentItem(1));
    recipe.setIngredient('0', physicalPrestigeNoAmount());

    // Registrar la receta en el servidor
    Bukkit.addRecipe(recipe);
  }

  public static void copperGumRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = comperGum(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "CopperGum");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', new ItemStack(Material.WAXED_COPPER_BLOCK));
    recipe.setIngredient('0', new ItemStack(Material.POPPED_CHORUS_FRUIT));

    // Registrar la receta en el servidor
    Bukkit.addRecipe(recipe);
  }

  public static void soccerBallRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = soccerBall(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "SoccerBall");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', new ItemStack(Material.LEATHER));
    recipe.setIngredient('0', new ItemStack(Material.WIND_CHARGE));

    // Registrar la receta en el servidor
    Bukkit.addRecipe(recipe);
  }

  public static Recipe getSoccerBallRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = soccerBall(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "SoccerBall");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', new ItemStack(Material.LEATHER));
    recipe.setIngredient('0', new ItemStack(Material.WIND_CHARGE));

    return recipe;
  }

  public static Recipe getCopperGumRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = comperGum(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "CopperGum");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', new ItemStack(Material.WAXED_COPPER_BLOCK));
    recipe.setIngredient('0', new ItemStack(Material.POPPED_CHORUS_FRUIT));

    return recipe;
  }

  public static void snifferCompanionCustomRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = snifferUnlockItem(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "SnifferUnlock");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', snifferFragmentItem(1));
    recipe.setIngredient('0', physicalPrestigeNoAmount());

    // Registrar la receta en el servidor
    Bukkit.addRecipe(recipe);
  }

  public static void pufferFishCompanionCustomRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = pufferFishUnlockItem(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "PufferUnlock");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', pufferFishFragment(1));
    recipe.setIngredient('0', physicalPrestigeNoAmount());

    // Registrar la receta en el servidor
    Bukkit.addRecipe(recipe);
  }

  public static Recipe getPufferFishCompanionCustomRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = snifferUnlockItem(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "PufferUnlock");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', pufferFishFragment(1));
    recipe.setIngredient('0', physicalPrestigeNoAmount());

    // Registrar la receta en el servidor
    return  recipe;
  }

  public static Recipe getSnifferCompanionCustomRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = snifferUnlockItem(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "SnifferUnlock");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', snifferFragmentItem(1));
    recipe.setIngredient('0', physicalPrestigeNoAmount());

    return recipe;
  }

  public static void osoCompanionCustomRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = osoUnlockItem(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "OsoUnlock");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', osoFragmentItem(1));
    recipe.setIngredient('0', physicalPrestigeNoAmount());

    // Registrar la receta en el servidor
    Bukkit.addRecipe(recipe);
  }

  public static Recipe getOsoCompanionCustomRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = osoUnlockItem(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "OsoUnlock");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', osoFragmentItem(1));
    recipe.setIngredient('0', physicalPrestigeNoAmount());

    // Registrar la receta en el servidor
    return recipe;
  }

  public static Recipe getAllayCompanionCustomRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = allayUnlockItem(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "AllayUnlock");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', allayFragmentItem(1));
    recipe.setIngredient('0', physicalPrestigeNoAmount());

    // Registrar la receta en el servidor
    return recipe;
  }

  public static void breezeCompanionCustomRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = breezeUnlockItem(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "BreezeUnlock");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', breezeFragmentItem(1));
    recipe.setIngredient('0', physicalPrestigeNoAmount());

    // Registrar la receta en el servidor
    Bukkit.addRecipe(recipe);
  }

  public static Recipe getBreezeCompanionCustomRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = armadilloUnlockItem(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "BreezeUnlock");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', breezeFragmentItem(1));
    recipe.setIngredient('0', physicalPrestigeNoAmount());

    // Registrar la receta en el servidor
    return recipe;
  }

  public static void armadilloCompanionCustomRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = armadilloUnlockItem(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "ArmadilloUnlock");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', armadilloFragmentItem(1));
    recipe.setIngredient('0', physicalPrestigeNoAmount());

    // Registrar la receta en el servidor
    Bukkit.addRecipe(recipe);
  }

  public static Recipe getArmadilloCompanionCustomRecipe() {
    JavaPlugin plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    // Crear el item que será el resultado del crafteo
    ItemStack customItem = armadilloUnlockItem(1);

    // Crear una receta con forma para el item
    NamespacedKey key = new NamespacedKey(plugin, "ArmadilloUnlock");
    ShapedRecipe recipe = new ShapedRecipe(key, customItem);

    // Definir el patrón de la receta
    recipe.shape("XXX", "X0X", "XXX");

    // Asignar los ingredientes a los caracteres del patrón
    recipe.setIngredient('X', armadilloFragmentItem(1));
    recipe.setIngredient('0', physicalPrestigeNoAmount());

    return recipe;
  }

}
