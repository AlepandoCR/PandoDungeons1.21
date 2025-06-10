package pandoClass.classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import pandoClass.RPGPlayer;
import pandodungeons.PandoDungeons;

public class CobrarCommand implements CommandExecutor {

    private final PandoDungeons plugin;

    public CobrarCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Solo admins o consola
        if (!(sender instanceof ConsoleCommandSender || sender.isOp())) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Uso correcto: /cobrar <jugador> <cantidad>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "El jugador '" + args[0] + "' no existe.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount < 1) {
                sender.sendMessage(ChatColor.RED + "La cantidad debe ser mayor que 0.");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Cantidad inválida: debe ser un número.");
            return true;
        }

        RPGPlayer rpgPlayer = new RPGPlayer(target.getUniqueId(), plugin);
        Long currentCoins = rpgPlayer.getCoins();

        if (currentCoins < amount) {
            sender.sendMessage(ChatColor.YELLOW + "El jugador tiene solo " + currentCoins + "⛃");
        }

        rpgPlayer.setCoins(currentCoins - amount);
        plugin.rpgPlayerDataManager.save(rpgPlayer);

        sender.sendMessage(ChatColor.GREEN + "Se han cobrado " + amount + "⛃ a " + target.getName() + ".");
        if (target.isOnline()) {
            target.getPlayer().sendMessage(ChatColor.RED + "Has sido cobrado: -" + amount + "⛃");
        }

        return true;
    }
}
