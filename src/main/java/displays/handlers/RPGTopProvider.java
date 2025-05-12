package displays.handlers;

import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import displays.DisplayData;
import pandoClass.RPGPlayer;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RPGTopProvider {

    public static List<DisplayData> getTopRPGPlayers(PandoDungeons plugin, int topCount) {
        List<RPGPlayer> allPlayers = plugin.rpgPlayerDataManager.loadAllPlayers();

        if (allPlayers == null || allPlayers.isEmpty()) return Collections.emptyList();

        // Ordenar por nivel y luego por orbes de mejora
        allPlayers.sort((p1, p2) -> Integer.compare(p2.getLevel(), p1.getLevel()));

        return allPlayers.stream()
                .limit(topCount)
                .map(rpgPlayer -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(rpgPlayer.getPlayerUUID());

                    // Crear supplier que devuelve el string a mostrar debajo de la cabeza
                    Supplier<String> textSupplier = () -> ChatColor.GRAY + player.getName() + " §f| §eNivel: §7" + rpgPlayer.getLevel();

                    return new DisplayData(textSupplier, player.getUniqueId());
                })
                .collect(Collectors.toList());
    }
}
