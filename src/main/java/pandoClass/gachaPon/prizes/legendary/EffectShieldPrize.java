package pandoClass.gachaPon.prizes.legendary;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EffectShieldPrize extends PrizeItem {


    NamespacedKey shieldEffect;


    public EffectShieldPrize(PandoDungeons plugin){
        super(plugin);

    }

    @Override
    protected ItemStack createItem() {
        shieldEffect = new NamespacedKey(plugin,"shieldEffect");
        return effectShield(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.LEGENDARIO;
    }

    private ItemStack effectShield(int amount){
        ItemStack item = new ItemStack(Material.SHIELD, amount);

        ItemMeta meta = item.getItemMeta();
        
        Random random = new Random();
        
        String effect = switch (random.nextInt(2)){
            case 0 -> "fire";
            case 1 -> "ice";
            default -> throw new IllegalStateException("Unexpected value: " + random.nextInt(2));
        };

       String name = switch (effect){
           case "fire" -> ChatColor.RED + "Escudo de fuego";
           case "ice" -> ChatColor.AQUA + "Escudo de hielo";
           default -> throw new IllegalStateException("Unexpected value: " + effect);
       };

       meta.setDisplayName(name);
       meta.getPersistentDataContainer().set(shieldEffect, PersistentDataType.STRING, effect);

       item.setItemMeta(meta);
        
       return item;
    }
}
