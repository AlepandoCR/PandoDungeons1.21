package displays.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import displays.DisplayData;
import org.eclipse.aether.util.listener.ChainedTransferListener;
import pandoClass.RPGPlayer;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CoinsTopProvider {

    public static List<DisplayData> getTopCoinPlayers(PandoDungeons plugin, int topCount) {
        List<RPGPlayer> allPlayers = plugin.rpgPlayerDataManager.loadAllPlayers();

        if (allPlayers == null || allPlayers.isEmpty()) return Collections.emptyList();

        // Ordenar por monedas (de mayor a menor)
        allPlayers.sort(Comparator.comparingInt(RPGPlayer::getCoins).reversed());

        return allPlayers.stream().limit(topCount).map(player -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getPlayerUUID());
            String name = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Jugador";

            Supplier<String> displayText = () -> "☃ " + ChatColor.GRAY + name + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Monedas: " + ChatColor.YELLOW + formatCoins(player.getCoins());
            return new DisplayData(displayText, player.getPlayerUUID());
        }).collect(Collectors.toList());
    }

    private static String formatCoins(int coins) {
        if (coins >= 1_000_000_000) {
            return String.format("%.1fB", coins / 1_000_000_000.0);
        } else if (coins >= 1_000_000) {
            return String.format("%.1fM", coins / 1_000_000.0);
        } else if (coins >= 1_000) {
            return String.format("%.1fk", coins / 1_000.0);
        } else {
            return String.valueOf(coins);
        }
    }

}
