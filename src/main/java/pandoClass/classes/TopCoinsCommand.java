package pandoClass.classes;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pandoClass.RPGPlayer;
import pandodungeons.PandoDungeons;

import java.util.Comparator;
import java.util.List;

public class TopCoinsCommand implements CommandExecutor {

    private final PandoDungeons plugin;

    public TopCoinsCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por jugadores.");
            return true;
        }

        Player player = (Player) sender;
        int page = 1;

        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Número de página inválido.");
                return true;
            }
        }

        List<RPGPlayer> allPlayers = plugin.rpgPlayerDataManager.loadAllPlayers();
        allPlayers.sort(Comparator.comparingLong(RPGPlayer::getCoins).reversed());

        int playersPerPage = 10;
        int totalPages = (int) Math.ceil((double) allPlayers.size() / playersPerPage);

        if (page > totalPages) {
            player.sendMessage(ChatColor.RED + "La página " + page + " no existe. Máximo: " + totalPages);
            return true;
        }

        int startIndex = (page - 1) * playersPerPage;
        int endIndex = Math.min(startIndex + playersPerPage, allPlayers.size());

        player.sendMessage(ChatColor.GOLD + "⛃ Top Jugadores con más monedas - Página " + page + "/" + totalPages + " ⛃");

        for (int i = startIndex; i < endIndex; i++) {
            RPGPlayer rpgPlayer = allPlayers.get(i);
            String name = Bukkit.getOfflinePlayer(rpgPlayer.getPlayerUUID()).getName();
            if (name == null) name = "Desconocido";

            player.sendMessage(ChatColor.YELLOW + "" + (i + 1) + ". " + ChatColor.GREEN + name
                    + ChatColor.GRAY + " - " + ChatColor.GOLD + rpgPlayer.getCoins() + "⛃");
        }

        // Botones de navegación
        TextComponent navigationMessage = new TextComponent();

        if (page > 1) {
            TextComponent previousPage = new TextComponent(ChatColor.GREEN + "[⬅ Página Anterior] ");
            previousPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/topmonedas " + (page - 1)));
            navigationMessage.addExtra(previousPage);
        }

        if (page < totalPages) {
            TextComponent nextPage = new TextComponent(ChatColor.GREEN + "[Página Siguiente ➡]");
            nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/topmonedas " + (page + 1)));
            navigationMessage.addExtra(nextPage);
        }

        player.spigot().sendMessage(navigationMessage);

        return true;
    }

}
