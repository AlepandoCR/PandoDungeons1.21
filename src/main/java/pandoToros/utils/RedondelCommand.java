package pandoToros.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pandoToros.Commands.PlayRedondelCommand;
import pandodungeons.PandoDungeons;

import java.util.List;

import static pandoToros.Commands.ToroStatsCommand.ToroCommandStats;

public class RedondelCommand implements CommandExecutor, TabCompleter {

    private PandoDungeons plugin;

    public RedondelCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("redondel")) {

            if(strings.length < 1){
                return true;
            }

            if (strings[0].equalsIgnoreCase("play")) {
                if (commandSender instanceof Player player) {
                    PlayRedondelCommand.playRedondel(player, plugin, strings);
                    return true;
                }
            }
            else if(strings[0].equalsIgnoreCase("stats")){
                if (commandSender instanceof Player player) {
                    ToroCommandStats(player, strings);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new java.util.ArrayList<>(List.of());
        if (strings.length == 1) {
            completions.add("play");
            completions.add("stats");
        }else if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("play")){
                completions.add("classic");
                completions.add("normal");
            }

        }

        return completions;
    }
}
