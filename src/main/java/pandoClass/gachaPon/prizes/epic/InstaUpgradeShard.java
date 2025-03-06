package pandoClass.gachaPon.prizes.epic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class InstaUpgradeShard extends PrizeItem {


    NamespacedKey upgradeShard;


    public InstaUpgradeShard(PandoDungeons plugin){
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        upgradeShard = new NamespacedKey(plugin,"upgradeShard");
        return reparationShardItem(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.EPICO;
    }

    private ItemStack reparationShardItem(int amount){
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD, amount);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Cristal de mejora");
        meta.getPersistentDataContainer().set(upgradeShard, PersistentDataType.BOOLEAN, true);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Vale por 1 mejora de item");
        lore.add(ChatColor.YELLOW + "/warp herrero");
        lore.add(ChatColor.RED + "Un solo uso");

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public static boolean isUpgradeShardItem(PandoDungeons plugin, ItemStack stack){
        return stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin,"upgradeShard"), PersistentDataType.BOOLEAN);
    }
}