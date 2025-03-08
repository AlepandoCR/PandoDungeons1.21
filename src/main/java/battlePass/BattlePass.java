package battlePass;

import battlePass.premium.PremiumBattlePass;
import battlePass.regular.DefaultBattlePass;
import battlePass.rewards.Reward;
import com.fastasyncworldedit.bukkit.util.image.BukkitImageViewer;
import org.bukkit.entity.Player;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.*;

public abstract class BattlePass {
    protected List<Reward> rewards;
    protected Map<Integer, Reward> dailyRewards;
    protected int currentMonth;

    public BattlePass(List<Reward> rewards) {
        this.rewards = rewards;
        this.dailyRewards = new HashMap<>();
        this.currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        generateMonthlyRewards();
    }

    private void generateMonthlyRewards() {
        Random random = new Random(currentMonth);
        List<Reward> shuffledRewards = new ArrayList<>(rewards);
        Collections.shuffle(shuffledRewards, random);
        int rewardCount = shuffledRewards.size();
        int maxDays = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);

        // Si la cantidad de premios es menor a 31, duplicamos los premios para llenar los días
        while (shuffledRewards.size() < maxDays) {
            shuffledRewards.addAll(shuffledRewards); // Duplicamos los premios
        }

        // Aseguramos que no haya más de los días posibles (hasta 31)
        if (shuffledRewards.size() > maxDays) {
            shuffledRewards = shuffledRewards.subList(0, maxDays); // Limitamos a 31
        }

        // Asignamos las recompensas a los días del mes
        for (int day = 1; day <= maxDays; day++) {
            dailyRewards.put(day, shuffledRewards.get(day - 1));
        }
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public Reward getRewardForDay(int day) {
        return dailyRewards.get(day);
    }

    protected void grantReward(Player player, int day) {
        getRewardForDay(day).applyTo(player);
    }

    public static BattlePass getPlayerBattlePass(Player player, PandoDungeons plugin) {
        if (player.hasPermission("pandodungeons.battlepass")) {
            return new PremiumBattlePass(plugin.defaultRewardManager.getRewards(), plugin.premiumRewardManager.getRewards());
        }
        return new DefaultBattlePass(plugin.defaultRewardManager.getRewards());
    }
}
