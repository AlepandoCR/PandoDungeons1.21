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

import java.util.Random;

public class JetPackPrize extends PrizeItem {


    NamespacedKey jetPack;


    public JetPackPrize(PandoDungeons plugin){
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        jetPack = new NamespacedKey(plugin,"jetPack");
        return jetPack(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.MITICO;
    }

    private ItemStack jetPack(int amount){
        ItemStack item = new ItemStack(Material.ELYTRA, amount);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.AQUA + "JetPack");
        meta.getPersistentDataContainer().set(jetPack, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);

        return item;
    }
}