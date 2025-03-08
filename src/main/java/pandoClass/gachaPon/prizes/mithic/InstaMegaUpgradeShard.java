package pandoClass.gachaPon.prizes.mithic;

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

public class InstaMegaUpgradeShard extends PrizeItem {


    NamespacedKey upgradeMegaShard;


    public InstaMegaUpgradeShard(PandoDungeons plugin){
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        upgradeMegaShard = new NamespacedKey(plugin,"upgradeMegaShard");
        return reparationShardItem(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.MITICO;
    }

    private ItemStack reparationShardItem(int amount){
        ItemStack item = new ItemStack(Material.RESIN_BRICK, amount);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Lingote de mega mejora");
        meta.getPersistentDataContainer().set(upgradeMegaShard, PersistentDataType.BOOLEAN, true);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Vale por 1 mejora de item");
        lore.add(ChatColor.AQUA + "Ignora el limite de mejora del herrero");
        lore.add(ChatColor.YELLOW + "/warp herrero");
        lore.add(ChatColor.RED + "Un solo uso");

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public static boolean isMegaUpgradeShardItem(PandoDungeons plugin, ItemStack stack){
        return stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin,"upgradeMegaShard"), PersistentDataType.BOOLEAN);
    }
}