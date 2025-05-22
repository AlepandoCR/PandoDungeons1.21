package pandodungeons.Game;

import org.bukkit.entity.Player;

public record Stats(int mobsKilled, int dungeonsCompleted, int dungeonLevel, String playerName, int levelProgress,
                    int prestige) {

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
