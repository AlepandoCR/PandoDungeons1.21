package pandodungeons.pandodungeons.Game;

import org.bukkit.entity.Player;

public class Stats {
    private final int mobsKilled;
    private final int dungeonsCompleted;
    private final int dungeonLevel;
    private final String playerName;
    private final int levelProgress;
    private final int prestige;

    public Stats(int mobsKilled, int dungeonsCompleted, int dungeonLevel, String playerName, int levelProgress, int prestige) {
        this.mobsKilled = mobsKilled;
        this.dungeonsCompleted = dungeonsCompleted;
        this.dungeonLevel = dungeonLevel;
        this.playerName = playerName;
        this.levelProgress = levelProgress;
        this.prestige = prestige;
    }

    public static Stats fromPlayer(Player player) {
        PlayerStatsManager statsManager = PlayerStatsManager.getPlayerStatsManager(player);
        return new Stats(
                statsManager.getMobsKilled(),
                statsManager.getDungeonsCompleted(),
                statsManager.getDungeonLevel(),
                statsManager.getPlayerName(),
                statsManager.getLevelProgress(),
                statsManager.getPrestige()
        );
    }

    public static Stats fromStatsManager(PlayerStatsManager statsManager) {
        String playerName = statsManager.getPlayerName();
        int prestige = statsManager.getPrestige();
        int dungeonLevel = statsManager.getDungeonLevel();
        int levelProgress = statsManager.getLevelProgress();
        int dungeonsCompleted = statsManager.getDungeonsCompleted();
        int mobsKilled = statsManager.getMobsKilled();
        return new Stats(mobsKilled, dungeonsCompleted, dungeonLevel, playerName, levelProgress, prestige);
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public int getDungeonsCompleted() {
        return dungeonsCompleted;
    }

    public int getDungeonLevel() {
        return dungeonLevel;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getLevelProgress() {
        return levelProgress;
    }

    public int getPrestige() {
        return prestige;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "mobsKilled=" + mobsKilled +
                ", dungeonsCompleted=" + dungeonsCompleted +
                ", dungeonLevel=" + dungeonLevel +
                ", playerName='" + playerName + '\'' +
                ", levelProgress=" + levelProgress +
                ", prestige=" + prestige +
                '}';
    }
}
