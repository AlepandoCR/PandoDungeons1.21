package pandoClass.quests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pandoClass.RPGPlayer;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.UUID;

public abstract class Mission<T> {
    protected final String missionName;
    protected int amountTo;
    protected int amount;
    protected UUID player;
    protected int level;
    protected RPGPlayer rpgPlayer;
    protected PandoDungeons plugin;

    /**
     * Constructor para crear una misión.
     *
     * @param missionName Nombre de la misión.
     */
    public Mission(String missionName, Player player, PandoDungeons plugin) {
        this.missionName = missionName;
        this.player = player.getUniqueId();
        this.plugin = plugin;

        rpgPlayer = new RPGPlayer(player);
        level = rpgPlayer.getLevel();

        // Fórmula dinámica para calcular la cantidad a completar
        int baseAmount = 5; // Cantidad mínima base
        double scalingFactor = 0.5; // Aumento por nivel
        this.amountTo = (int) (baseAmount + (level * scalingFactor));
    }

    public String getMissionName() {
        return missionName;
    }

    public int getLevel() {
        return level;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }

    public void addAmount() {
        amount++;
    }

    public boolean isCompleted() {
        return amount >= amountTo;
    }

    public int calculateReward() {
        int baseReward = 2;
        int scalingFactor = 3;
        return (amountTo * baseReward) + (level * scalingFactor);
    }

    public abstract void listener(T event);

    public abstract void rewardPlayer();
}
