package pandodungeons.commands.game;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pandodungeons.Game.PlayerStatsManager;
import pandodungeons.Game.Stats;
import pandodungeons.PandoDungeons;

import java.util.Comparator;
import java.util.List;

public class DungeonsTopCommand implements CommandExecutor {

    private final PandoDungeons plugin;

    public DungeonsTopCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;

        PlayerStatsManager.loadAllPlayerStats();

        List<Stats> allStats = PlayerStatsManager.loadAllPlayerStatsList();

        if(allStats != null){
            allStats.sort(Comparator.comparingInt(Stats::prestige)
                    .thenComparingInt(Stats::dungeonLevel)
                    .thenComparingInt(Stats::levelProgress)
                    .thenComparingInt(Stats::dungeonsCompleted)
                    .reversed());

            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Top 3 Jugadores de Mazmorras:");
            for (int i = 0; i < Math.min(3, allStats.size()); i++) {
                Stats stats = allStats.get(i);
                player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + (i + 1) + ". " +
                        ChatColor.AQUA + "" + ChatColor.BOLD + stats.playerName() +
                        ChatColor.WHITE + "\n" +
                        ChatColor.GOLD + "" + ChatColor.BOLD + "Prestigio: " +
                        ChatColor.WHITE + stats.prestige() + "\n" +
                        ChatColor.GOLD + "" + ChatColor.BOLD + "Nivel: " +
                        ChatColor.WHITE + stats.dungeonLevel() + "\n" +
                        ChatColor.GOLD + "" + ChatColor.BOLD + "Progreso de Nivel: " +
                        ChatColor.WHITE + stats.levelProgress() + "\n" +
                        ChatColor.GOLD + "" + ChatColor.BOLD + "Mazmorras Completadas: " +
                        ChatColor.WHITE + stats.dungeonsCompleted() + "\n" +
                        ChatColor.GRAY + "------------------------------");
            }
        }

        return true;
    }
}
