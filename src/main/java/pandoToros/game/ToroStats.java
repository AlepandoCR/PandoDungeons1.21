package pandoToros.game;

import org.bukkit.entity.Player;

public class ToroStats {
    private final int kills;
    private final int TimeSurvived;
    private final int Upgrades;
    private final String playerName;
    private final int Effects;
    private final int dodges;

    public ToroStats(int mobsKilled, int dungeonsCompleted, int dungeonLevel, String playerName, int levelProgress, int prestige) {
        this.kills = mobsKilled;
        this.TimeSurvived = dungeonsCompleted;
        this.Upgrades = dungeonLevel;
        this.playerName = playerName;
        this.Effects = levelProgress;
        this.dodges = prestige;
    }

    public static ToroStats fromPlayer(Player player) {
        ToroStatManager statsManager = ToroStatManager.getToroStatsManager(player);
        return new ToroStats(
                statsManager.getKills(),
                statsManager.getTimeSurvived(),
                statsManager.getUpgrades(),
                statsManager.getPlayerName(),
                statsManager.getEffects(),
                statsManager.getDodges()
        );
    }

    public static ToroStats fromToroStatsManager(ToroStatManager statsManager) {
        String playerName = statsManager.getPlayerName();
        int prestige = statsManager.getDodges();
        int dungeonLevel = statsManager.getUpgrades();
        int levelProgress = statsManager.getEffects();
        int dungeonsCompleted = statsManager.getTimeSurvived();
        int mobsKilled = statsManager.getKills();
        return new ToroStats(mobsKilled, dungeonsCompleted, dungeonLevel, playerName, levelProgress, prestige);
    }

    public int getKills() {
        return kills;
    }

    public int getTimeSurvived() {
        return TimeSurvived;
    }

    public int getUpgrades() {
        return Upgrades;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getEffects() {
        return Effects;
    }

    public int getDodges() {
        return dodges;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "Kills=" + kills +
                ", Tiempo sobrevivido=" + TimeSurvived +
                ", Mejoras=" + Upgrades +
                ", playerName='" + playerName + '\'' +
                ", Effectos=" + Effects +
                ", Esquivos=" + dodges +
                '}';
    }
}
