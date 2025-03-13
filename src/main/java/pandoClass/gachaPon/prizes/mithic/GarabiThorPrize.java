package pandoClass.gachaPon.prizes.mithic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.Utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class GarabiThorPrize extends PrizeItem {

    NamespacedKey garabiThor;
    NamespacedKey bateria;

    public GarabiThorPrize(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        garabiThor = new NamespacedKey(plugin, "garabiThor");
        bateria = new NamespacedKey(plugin, "bateria");
        return garabiThor(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.MITICO;
    }

    private ItemStack garabiThor(int amount) {
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
        CustomModelDataComponent component = meta.getCustomModelDataComponent();

        component.setStrings(List.of("garabithor"));

        meta.setCustomModelDataComponent(component);

        meta.setRarity(ItemRarity.EPIC);
        item.setItemMeta(meta);
        return item;
    }
}
