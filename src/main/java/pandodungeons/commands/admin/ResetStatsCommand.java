package pandodungeons.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import pandodungeons.PandoDungeons;
import pandodungeons.Game.PlayerStatsManager;

public class ResetStatsCommand implements CommandExecutor, Listener {

    public ResetStatsCommand(PandoDungeons plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }
        Player player = (Player) sender;

        // Verificar permisos de operador
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "No tienes permiso para ejecutar este comando.");
            return true;
        }

        // Verificar argumentos
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Uso: /dungeons resetstats <jugador>");
            return true;
        }

        // Obtener el jugador objetivo
        String targetName = args[1];


        // Resetear las estadísticas del jugador
        PlayerStatsManager targetStatsManager = PlayerStatsManager.getPlayerStatsManagerByName(targetName);
        if (targetStatsManager == null) {
            player.sendMessage(ChatColor.RED + "No hay stats existentes para ese jugador");
            return true;
        }
        targetStatsManager.resetStats();

        player.sendMessage(ChatColor.GREEN + "Se han reseteado las estadísticas de " + targetName + ".");


        return true;
    }
}
