package pandoClass.gachaPon.prizes.rare;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class SoulWritterPrize extends PrizeItem {

    NamespacedKey soulUses;
    NamespacedKey soulWritter;

    public SoulWritterPrize(PandoDungeons plugin){
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        soulUses = new NamespacedKey(plugin, "soulUses");
        soulWritter = new NamespacedKey(plugin, "soulWritter");
        return soulWritter(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.RARO;
    }

    private ItemStack soulWritter(int amount) {
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
}
