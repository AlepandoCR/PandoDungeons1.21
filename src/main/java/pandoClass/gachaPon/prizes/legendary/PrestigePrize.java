package pandoClass.gachaPon.prizes.legendary;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.Utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class PrestigePrize extends PrizeItem {
    @Override
    protected ItemStack createItem() {
        return physicalPrestige(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.LEGENDARIO;
    }

    private ItemStack physicalPrestige(int amount){
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
}
