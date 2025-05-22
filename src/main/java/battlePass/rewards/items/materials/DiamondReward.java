package battlePass.rewards.items.materials;

import battlePass.rewards.items.RewardCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pandodungeons.PandoDungeons;

public class DiamondReward extends RewardCreator {
    public DiamondReward(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack defineItem() {
        ItemStack item = new ItemStack(Material.DIAMOND, 3);
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected String defineName() {
        return "diamond";
    }
}
