package battlePass.rewards.items.tools;

import battlePass.rewards.items.RewardCreator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.List;

public class EnchantedPickaxeReward extends RewardCreator {
    public EnchantedPickaxeReward(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack defineItem() {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.EFFICIENCY, 5, true);
        meta.addEnchant(Enchantment.UNBREAKING, 3, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected String defineName() {
        return "Enchanted Pickaxe";
    }
}