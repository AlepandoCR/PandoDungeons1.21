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
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class MapachoBladePrize extends PrizeItem {

    NamespacedKey mapachoBlade;

    public MapachoBladePrize(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        mapachoBlade = new NamespacedKey(plugin, "mapachoBlade");
        return mapachoBlade(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.MITICO;
    }

    private ItemStack mapachoBlade(int amount) {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setItemName(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "Mapacho Blade   ");
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD.toString() + "Desata los poderes miticos de Mapacho " + ChatColor.BOLD + "🦝");
        lore.add("");
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(mapachoBlade, PersistentDataType.BOOLEAN, true);

        CustomModelDataComponent component = meta.getCustomModelDataComponent();

        component.setStrings(List.of("mapachoblade"));

        meta.setCustomModelDataComponent(component);

        meta.setRarity(ItemRarity.EPIC);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isMapachoBlade(ItemStack itemStack, PandoDungeons plugin){
        NamespacedKey mapachoBlade = new NamespacedKey(plugin, "mapachoBlade");

        if(itemStack.hasItemMeta()){
            return itemStack.getItemMeta().getPersistentDataContainer().has(mapachoBlade, PersistentDataType.BOOLEAN);
        }

        return false;
    }
}
