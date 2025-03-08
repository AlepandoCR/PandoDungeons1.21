package battlePass.premium;

import battlePass.BattlePass;
import battlePass.rewards.Reward;
import org.bukkit.entity.Player;

import java.util.*;

public class PremiumBattlePass extends BattlePass {
    private List<Reward> premiumRewards;
    private final Map<Integer, Reward> dailyPremiumRewards;

    public PremiumBattlePass(List<Reward> rewards, List<Reward> premiumRewards) {
        super(rewards);
        this.premiumRewards = premiumRewards;
        this.dailyPremiumRewards = new HashMap<>();
        generateMonthlyPremiumRewards();
    }

    private void generateMonthlyPremiumRewards() {

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
            dailyPremiumRewards.put(day, shuffledRewards.get(day - 1));
            premiumRewards.add(shuffledRewards.get(day - 1));
        }

    }

    public Reward getPremiumRewardForDay(int day) {
        return dailyPremiumRewards.get(day);
    }

    @Override
    public void grantReward(Player player, int day) {
        super.grantReward(player,day);
        Reward premiumReward = getPremiumRewardForDay(day);
        if (premiumReward != null) {
            premiumReward.applyTo(player);
        }
    }
}
