package pandoClass.gambling;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pandodungeons.PandoDungeons;

import java.util.List;

public class GambleCommand implements CommandExecutor, TabCompleter {

    private final PandoDungeons plugin;

    public GambleCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("bet")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player executingPlayer = (Player) sender;

        // Sin argumentos: mostrar estadísticas del propio jugador.
        if (args.length == 0) {
            executingPlayer.sendMessage(ChatColor.YELLOW + "Usa /bet caballos <número> <monto> para hacer una apuesta.");
            return true;
        }

        // Subcomando "caballos": muestra el top 3 de jugadores por nivel.
        if (args[0].equalsIgnoreCase("caballos")) {
            if (args.length == 3) {
                try {
                    int horse = Integer.parseInt(args[1]);
                    int toBet = Integer.parseInt(args[2]);

                    // Verificar que la apuesta sea válida.
                    if (toBet < 100) {
                        executingPlayer.sendMessage(ChatColor.RED + "La cantidad de la apuesta debe ser mayor que 100.");
                        return true;
                    }

                    // Aquí agregamos la apuesta de la sesión.
                    plugin.gamblingSession.addBet(executingPlayer, horse, toBet);
                    return true;

                } catch (NumberFormatException exception) {
                    executingPlayer.sendMessage(ChatColor.RED + "Por favor, ingresa un número válido para el caballo y la apuesta.");
                    return false;
                }
            } else {
                executingPlayer.sendMessage(ChatColor.RED + "Uso incorrecto. Usa /bet caballos <número> <monto>");
                return false;
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new java.util.ArrayList<>(List.of());
        if (strings.length == 1) {
            completions.add("caballos");
        }
        if (strings.length == 2) {
            completions.add("1");
            completions.add("2");
            completions.add("3");
        }
        if (strings.length == 3) {
            completions.add("Apuesta");
        }
        return completions;
    }
}
