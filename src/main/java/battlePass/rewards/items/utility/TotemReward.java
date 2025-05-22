package battlePass.rewards.items.utility;

import battlePass.rewards.items.RewardCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pandodungeons.PandoDungeons;

public class TotemReward extends RewardCreator {
    public TotemReward(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack defineItem() {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected String defineName() {
        return "totem";
    }
}

