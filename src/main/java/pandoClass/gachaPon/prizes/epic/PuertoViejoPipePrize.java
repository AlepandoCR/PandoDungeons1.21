package pandoClass.gachaPon.prizes.epic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PuertoViejoPipePrize extends PrizeItem {

    private NamespacedKey puertoViejoPipe;
    private NamespacedKey usosKey;
    private static final int USOS_MAXIMOS = 100;

    public PuertoViejoPipePrize(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        this.puertoViejoPipe = new NamespacedKey(plugin, "puertoViejoPipe");
        this.usosKey = new NamespacedKey(plugin, "puertoViejoPipeUsos");
        return createPuertoViejoPipe(USOS_MAXIMOS);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.EPICO;
    }

    private ItemStack createPuertoViejoPipe(int usos) {
        ItemStack item = new ItemStack(Material.GOAT_HORN, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setItemName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Pipa de Puerto Viejo");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "No sabes lo que contiene, pero la encontraste por ahí...");
        lore.add(ChatColor.DARK_PURPLE + "Quizás te haga bien... o mal.");
        lore.add("");
        lore.add(ChatColor.AQUA + "Jalones: " + usos);
        meta.setLore(lore);

        // Agregar NBT
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(puertoViejoPipe, PersistentDataType.BOOLEAN, true);
        data.set(usosKey, PersistentDataType.INTEGER, usos);

        // Custom Model Data
        CustomModelDataComponent component = meta.getCustomModelDataComponent();
        component.setStrings(List.of("puertoviejopipe"));
        meta.setCustomModelDataComponent(component);

        meta.setRarity(ItemRarity.EPIC);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isPuertoViejoPipe(ItemStack itemStack, PandoDungeons plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "puertoViejoPipe");
        return itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN);
    }



    public static void applyRandomEffect(Player player) {
        // Siempre da náuseas
        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 1)); // 10s de nausea

        // Lista de efectos adicionales posibles
        PotionEffectType[] effects = {
                PotionEffectType.SPEED,         // Velocidad
                PotionEffectType.FIRE_RESISTANCE, // Resistencia al fuego
                PotionEffectType.BLINDNESS,     // Ceguera
                PotionEffectType.REGENERATION,  // Regeneración
                PotionEffectType.HUNGER         // Pérdida de hambre
        };

        // Seleccionar uno aleatorio
        PotionEffectType randomEffect = effects[new Random().nextInt(effects.length)];
        player.addPotionEffect(new PotionEffect(randomEffect, 1600, 0));
    }

}
