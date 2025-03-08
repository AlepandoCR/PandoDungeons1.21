package battlePass.rewards.items.utility;

import battlePass.rewards.items.RewardCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.List;

public class GoldenAppleReward extends RewardCreator {
    public GoldenAppleReward(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack defineItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected String defineName() {
        return "goldenApple";
    }
}