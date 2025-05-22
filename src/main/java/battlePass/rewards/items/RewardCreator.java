package battlePass.rewards.items;

import battlePass.rewards.Reward;
import org.bukkit.inventory.ItemStack;
import pandodungeons.PandoDungeons;

public abstract class RewardCreator {
    public final Reward reward;

    protected final PandoDungeons plugin;
    public RewardCreator(PandoDungeons plugin){
        this.plugin = plugin;
        reward = new Reward(setName(),setItem());
        plugin.defaultRewardManager.insertReward(reward);
        plugin.premiumRewardManager.insertReward(reward);
    }

    public ItemStack setItem(){
       return defineItem();
    }

    public String setName(){
        return defineName();
    }

    protected abstract ItemStack defineItem();

    protected abstract String defineName();
}
