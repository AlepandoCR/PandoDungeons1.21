package displays.handlers;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pandodungeons.pandodungeons.Game.PlayerStatsManager;
import pandodungeons.pandodungeons.Game.Stats;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;

public class DungeonsTopHandler {
    public static List<DungeonsTopEntry> getTop5DungeonPlayersWithDisplayData() {
        List<Stats> allStats = PlayerStatsManager.loadAllPlayerStatsList();
        if (allStats == null) return Collections.emptyList();

        allStats.sort(Comparator
                .comparingInt(Stats::dungeonsCompleted).reversed()
                .thenComparing(Comparator.comparingInt(Stats::prestige).reversed()));


        List<DungeonsTopEntry> topList = new ArrayList<>();

        for (int i = 0; i < Math.min(5, allStats.size()); i++) {

            Stats stats = allStats.get(i);

            PlayerStatsManager statsManager = PlayerStatsManager.getPlayerStatsManagerByName(stats.playerName());

            if(statsManager == null) return Collections.emptyList();

            File file = statsManager.getPlayerFile();

            String filename = file.getName();
            String uuid = filename.contains(".") ? filename.substring(0, filename.lastIndexOf('.')) : filename;

            UUID playerUUID = UUID.fromString(uuid);

            OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);

            Supplier<String> displayText = () -> ChatColor.GRAY + player.getName() + " §f| "  + "§6Prestigio: §f" + stats.prestige() +
                    " §7| §bDungeons completadas: §f" + stats.dungeonsCompleted();

            topList.add(new DungeonsTopEntry(playerUUID, displayText));
        }

        return topList;
    }

    public record DungeonsTopEntry(UUID uuid, Supplier<String> textSupplier) {
    }
}
