package pandodungeons.commands.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import pandodungeons.PandoDungeons;
import pandodungeons.Game.PlayerStatsManager;
import pandodungeons.Game.Stats;

import java.util.Comparator;
import java.util.List;

public class DungeonsStatsCommand implements CommandExecutor, Listener {

    public DungeonsStatsCommand(PandoDungeons plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar si el comando se ejecutó por un jugador
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;
        Player targetPlayer = player;

        PlayerStatsManager.loadAllPlayerStats();

        if (args.length > 1 && args[0].equalsIgnoreCase("stats")) {
            String targetName = args[1];
            targetPlayer = Bukkit.getPlayerExact(targetName);
            if (targetPlayer == null) {
                PlayerStatsManager targetStatsManager = PlayerStatsManager.getPlayerStatsManagerByName(targetName);
                if (targetStatsManager != null) {
                    player.sendMessage(targetStatsManager.toString(player));
                } else {
                    player.sendMessage(ChatColor.RED + "Jugador no encontrado o no tiene estadísticas registradas.");
                }
                return true;
            }
        }

        List<Stats> allStats = PlayerStatsManager.loadAllPlayerStatsList();

        if(allStats != null){
            allStats.sort(Comparator.comparingInt(Stats::prestige)
                    .thenComparingInt(Stats::dungeonLevel)
                    .thenComparingInt(Stats::levelProgress)
                    .thenComparingInt(Stats::dungeonsCompleted)
                    .reversed());

            PlayerStatsManager statsManager = PlayerStatsManager.getPlayerStatsManager(targetPlayer);
            String statsMessage = statsManager.toString(targetPlayer);

            int position = -1;
            for (int i = 0; i < allStats.size(); i++) {
                if (allStats.get(i).playerName().equals(targetPlayer.getName())) {
                    position = i + 1;
                    break;
                }
            }

            if (position != -1) {
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Tus estadísticas:");
                player.sendMessage(statsMessage);
                player.sendMessage(ChatColor.GREEN + "Estás en la posición " + ChatColor.YELLOW + ChatColor.BOLD + position + ChatColor.GREEN + " del top de mazmorras.");
            } else {
                player.sendMessage(ChatColor.RED + "No se pudo determinar tu posición en el top de mazmorras.");
            }
        }


        return true;
    }
}
