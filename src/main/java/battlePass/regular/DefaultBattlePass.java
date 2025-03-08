package battlePass.regular;

import battlePass.BattlePass;
import battlePass.rewards.Reward;
import org.bukkit.entity.Player;

import java.util.List;

public class DefaultBattlePass extends BattlePass {

    public DefaultBattlePass(List<Reward> rewards) {
        super(rewards);
    }

    @Override
    public void grantReward(Player player, int day) {
        Reward reward = getRewardForDay(day);
        if (reward != null) {
            reward.applyTo(player);
        }
    }
}
