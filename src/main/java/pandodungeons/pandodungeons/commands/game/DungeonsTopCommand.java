package pandodungeons.pandodungeons.commands.game;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.pandodungeons.Game.PlayerStatsManager;
import pandodungeons.pandodungeons.Game.Stats;
import pandodungeons.pandodungeons.PandoDungeons;

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
            allStats.sort(Comparator.comparingInt(Stats::getPrestige)
                    .thenComparingInt(Stats::getDungeonLevel)
                    .thenComparingInt(Stats::getLevelProgress)
                    .thenComparingInt(Stats::getDungeonsCompleted)
                    .reversed());

            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Top 3 Jugadores de Mazmorras:");
            for (int i = 0; i < Math.min(3, allStats.size()); i++) {
                Stats stats = allStats.get(i);
                player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + (i + 1) + ". " +
                        ChatColor.AQUA + "" + ChatColor.BOLD + stats.getPlayerName() +
                        ChatColor.WHITE + "\n" +
                        ChatColor.GOLD + "" + ChatColor.BOLD + "Prestigio: " +
                        ChatColor.WHITE + stats.getPrestige() + "\n" +
                        ChatColor.GOLD + "" + ChatColor.BOLD + "Nivel: " +
                        ChatColor.WHITE + stats.getDungeonLevel() + "\n" +
                        ChatColor.GOLD + "" + ChatColor.BOLD + "Progreso de Nivel: " +
                        ChatColor.WHITE + stats.getLevelProgress() + "\n" +
                        ChatColor.GOLD + "" + ChatColor.BOLD + "Mazmorras Completadas: " +
                        ChatColor.WHITE + stats.getDungeonsCompleted() + "\n" +
                        ChatColor.GRAY + "------------------------------");
            }
        }

        return true;
    }
}
