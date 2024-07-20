package pandodungeons.pandodungeons.Game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class PlayerStatsBook {

    public static ItemStack getPlayerStatsBook(Player player) {
        Stats playerStats = Stats.fromPlayer(player);

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        assert meta != null;

        meta.setTitle(ChatColor.GOLD + "Dungeon Stats");
        meta.setAuthor(ChatColor.AQUA + playerStats.getPlayerName());

        // Página 1: Información general
        meta.addPage(
                ChatColor.BOLD + "" + ChatColor.GOLD + "Dungeons Stats\n\n" +
                        ChatColor.RESET + ChatColor.AQUA + "Player: " + ChatColor.RESET + ChatColor.WHITE + player.getName() + "\n" +
                        ChatColor.AQUA + "Mobs Killed: " + ChatColor.RESET + ChatColor.GREEN + playerStats.getMobsKilled() + "\n" +
                        ChatColor.AQUA + "Dungeons Completed: " + ChatColor.RESET + ChatColor.GREEN + playerStats.getDungeonsCompleted() + "\n" +
                        ChatColor.AQUA + "Dungeon Level: " + ChatColor.RESET + ChatColor.GREEN + playerStats.getDungeonLevel() + "\n" +
                        ChatColor.AQUA + "Level Progress: " + ChatColor.RESET + ChatColor.GREEN + playerStats.getLevelProgress() + "\n" +
                        ChatColor.AQUA + "Prestige: " + ChatColor.RESET + ChatColor.GREEN + playerStats.getPrestige() + ChatColor.RESET + ChatColor.GOLD + " ✦"
        );

        // Página 2: Promedios y gráfico de progreso
        meta.addPage(
                ChatColor.BOLD.toString() + ChatColor.GOLD + "Stats Details\n\n" +
                        ChatColor.RESET + ChatColor.GOLD + "Kills per Dungeon ≈ " + ChatColor.GREEN + formatDouble(getAverageKillsPerDungeon(playerStats)) + "\n" +
                        ChatColor.RESET + ChatColor.GOLD + "Dungeons per Level ≈ " + ChatColor.GREEN + formatDouble(getAverageDungeonsPerLevel(playerStats)) + "\n\n" +
                        ChatColor.BOLD + ChatColor.GOLD + "Level Progress\n\n" + getGraph(playerStats.getLevelProgress(), 3, 20) + "\n\n" +
                        ChatColor.BOLD + ChatColor.GOLD + "Prestige \n\n" + getGraph(playerStats.getDungeonLevel(), 5, 20)


        );

        meta.setGeneration(BookMeta.Generation.ORIGINAL);
        book.setItemMeta(meta);

        return book;
    }

    private static double getAverageKillsPerDungeon(Stats stats) {
        if (stats.getDungeonsCompleted() == 0) {
            return 0;
        }
        return (double) stats.getMobsKilled() / stats.getDungeonsCompleted();
    }

    private static double getAverageDungeonsPerLevel(Stats stats) {
        if (stats.getDungeonLevel() == 0) {
            return 0;
        }
        return (double) stats.getDungeonsCompleted() / (stats.getDungeonLevel() + (stats.getPrestige() * 4));
    }

    private static String formatDouble(double value) {
        return String.format("%.2f", value);
    }
    private static String getGraph(int value, int maxValue, int length) {
        int filledLength = (int) ((double) value / maxValue * length);
        StringBuilder graph = new StringBuilder();
        graph.append(ChatColor.GREEN);

        for (int i = 0; i < filledLength; i++) {
            graph.append("|");
        }

        graph.append(ChatColor.RED);

        for (int i = filledLength; i < length; i++) {
            graph.append("|");
        }

        return graph.toString();
    }
}
