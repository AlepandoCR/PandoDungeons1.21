package pandoClass.gachaPon.prizes.mithic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

public class KatanaPrize extends PrizeItem {

    private NamespacedKey katanaKey;

    public KatanaPrize(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        katanaKey = new NamespacedKey(plugin, "katana");
        return createKatanaItem(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.MITICO;
    }

    private ItemStack createKatanaItem(int amount) {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        // Nombre
        meta.setDisplayName(ChatColor.DARK_RED + "Katana");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Esta hoja ancestral aumenta su poder");
        lore.add(ChatColor.GRAY + "con cada golpe acertado en cadena.");
        lore.add("");
        lore.add(ChatColor.DARK_GRAY + "Forjada en el " + ChatColor.GOLD + "Barrio Chino " + ChatColor.RED + ChatColor.BOLD + "🏮");

        meta.setLore(lore);

        meta.setRarity(ItemRarity.EPIC);

        CustomModelDataComponent component = meta.getCustomModelDataComponent();

        component.setStrings(List.of("katana"));

        meta.setCustomModelDataComponent(component);

        meta.getPersistentDataContainer().set(katanaKey, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }

    public static NamespacedKey getKatanaKey(PandoDungeons plugin) {
        return new NamespacedKey(plugin, "katana");
    }
}
