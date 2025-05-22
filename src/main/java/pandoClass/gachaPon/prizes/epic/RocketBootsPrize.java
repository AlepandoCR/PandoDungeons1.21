package pandoClass.gachaPon.prizes.epic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class RocketBootsPrize extends PrizeItem {


    NamespacedKey rocketBoot;


    public RocketBootsPrize(PandoDungeons plugin){
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        rocketBoot = new NamespacedKey(plugin,"rocketBoots");
        return rocketBoots(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.EPICO;
    }

    private ItemStack rocketBoots(int amount){
        ItemStack item = new ItemStack(Material.LEATHER_BOOTS, amount);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.AQUA + "Botas Cohete");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.YELLOW + "Te impulsará hacia arriba");
        lore.add(ChatColor.DARK_GRAY + "Muy util para construir");

        meta.getPersistentDataContainer().set(rocketBoot, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);

        return item;
    }
}