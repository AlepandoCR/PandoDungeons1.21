package pandoClass.quests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pandoClass.RPGPlayer;
import pandodungeons.PandoDungeons;

import java.util.*;

public abstract class Mission<T> {
    protected final String missionName;
    protected int amountTo;
    protected int amount;
    protected UUID player;
    protected int level;
    protected RPGPlayer rpgPlayer;
    protected PandoDungeons plugin;

    private static final Set<UUID> firstMissionCompleted = new HashSet<>();


    public Mission(String missionName, Player player, PandoDungeons plugin) {
        this.missionName = missionName;
        this.player = player.getUniqueId();
        this.plugin = plugin;

        rpgPlayer = plugin.rpgManager.getPlayer(player);
        level = rpgPlayer.getLevel();


        int baseAmount = 5;
        double scalingFactor = 0.5;
        this.amountTo = Math.max(1 ,(int) (baseAmount + (level * scalingFactor))); // Escala con nivel
    }

    public abstract void sendMissionMessage();

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
        this.rpgPlayer = plugin.rpgManager.getPlayer(getPlayer());
    }

    public boolean isCompleted() {
        return amount >= amountTo;
    }

    public int calculateReward() {
        int baseReward = 5;
        int scalingFactor = 10;
        return (amountTo * baseReward) + (level * scalingFactor); // Escala con nivel
    }

    public abstract void listener(T event);

    public abstract void rewardPlayer();

    protected boolean isFirstMissionOfInstance(Player player) {
        if (player == null) {
            return false;
        }
        UUID uuid = player.getUniqueId();

        if (!firstMissionCompleted.contains(uuid)) {
            firstMissionCompleted.add(uuid);
            return true;
        }
        return false;
    }
}
