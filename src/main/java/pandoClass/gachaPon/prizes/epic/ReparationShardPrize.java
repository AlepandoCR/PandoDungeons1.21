package pandoClass.gachaPon.prizes.epic;

import com.sk89q.worldedit.util.gson.BlockVectorAdapter;
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

public class ReparationShardPrize extends PrizeItem {


    NamespacedKey reparationShard;


    public ReparationShardPrize(PandoDungeons plugin){
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        reparationShard = new NamespacedKey(plugin,"reparationShard");
        return reparationShardItem(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.EPICO;
    }

    private ItemStack reparationShardItem(int amount){
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD, amount);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Cristal de reparación");
        meta.getPersistentDataContainer().set(reparationShard, PersistentDataType.BOOLEAN, true);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Repara en 100% tus herramientas y armaduras");
        lore.add(ChatColor.RED + "Un solo uso");

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public static boolean isReparationShardItem(PandoDungeons plugin, ItemStack stack){
        return stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin,"reparationShard"), PersistentDataType.BOOLEAN);
    }
}