package pandoClass.gachaPon.prizes.mithic;

import it.unimi.dsi.fastutil.chars.Char2ShortRBTreeMap;
import net.minecraft.network.chat.ChatDecorator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.components.CraftCustomModelDataComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        CustomModelDataComponent component = meta.getCustomModelDataComponent();

        component.setStrings(List.of("jetpack"));

        meta.setCustomModelDataComponent(component);

        meta.setDisplayName(ChatColor.AQUA + "JetPack");
        meta.getPersistentDataContainer().set(jetPack, PersistentDataType.BOOLEAN, true);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Manten " + ChatColor.BOLD + "espacio" + ChatColor.GOLD + " para impulsarte");
        lore.add(ChatColor.RED + "Requiere combustible (" + ChatColor.DARK_GRAY + "carbon" + ChatColor.RED + ")");

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public static boolean isJetPack(ItemStack itemStack, PandoDungeons plugin){
        NamespacedKey mapachoBlade = new NamespacedKey(plugin, "jetPack");

        if(itemStack.hasItemMeta()){
            return itemStack.getItemMeta().getPersistentDataContainer().has(mapachoBlade, PersistentDataType.BOOLEAN);
        }

        return false;
    }
}