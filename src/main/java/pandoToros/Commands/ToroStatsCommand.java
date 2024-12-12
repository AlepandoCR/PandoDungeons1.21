package pandoToros.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import pandoToros.Entities.toro.Toro;
import pandoToros.game.ToroStatManager;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.Game.PlayerStatsManager;
import pandodungeons.pandodungeons.Game.Stats;

import java.util.Comparator;
import java.util.List;

import static pandoToros.game.RandomBox.giveHorseRide;

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
