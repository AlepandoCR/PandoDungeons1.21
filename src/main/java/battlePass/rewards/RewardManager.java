package battlePass.rewards;

import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public abstract class RewardManager {
    protected final List<Reward> rewards = new ArrayList<>();

    protected final PandoDungeons plugin;

    protected RewardManager(PandoDungeons plugin){
        this.plugin =  plugin;
    }

    public List<Reward> getRewards() {
        return rewards;
    }


    public void insertReward(Reward reward){
        rewards.add(reward);
    }

}
