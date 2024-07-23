package pandodungeons.pandodungeons.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

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
