package pandodungeons.commands.admin.companions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pandodungeons.Utils.CompanionUtils;

public class CompanionUnlock implements CommandExecutor {

    private final JavaPlugin plugin;

    public CompanionUnlock(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por jugadores o la consola.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("Uso incorrecto del comando. Usa: /" + label + " <nombreJugador> <tipoCompanion>");
            return false;
        }

        Player targetPlayer = plugin.getServer().getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage("El jugador especificado no está en línea.");
            return true;
        }

        String companionType = args[2];
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOp()) {
                player.sendMessage("No tienes permiso para ejecutar este comando.");
                return true;
            }
        }
        if(CompanionUtils.isCompanionType(companionType)){
            CompanionUtils.unlockCompanion(targetPlayer, companionType, 1);
            sender.sendMessage("El acompañante " + companionType + " ha sido desbloqueado para " + targetPlayer.getName() + ".");
        }else{
            sender.sendMessage("No existe ese tipo de acompañante");
        }

        return true;
    }
}
