package pandoToros.Commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pandoToros.game.ToroStatManager;

public class ToroStatsCommand {

    public ToroStatsCommand(Player player, String[] args) {
    }

    public static void ToroCommandStats(Player player, String[] args ) {

        ToroStatManager.loadAllToroPlayerStats();
        ToroStatManager statsManager = ToroStatManager.getToroStatsManager(player);
        String statsMessage = statsManager.toString(player);
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Tus estadísticas:");
        player.sendMessage(statsMessage);
    }
}
