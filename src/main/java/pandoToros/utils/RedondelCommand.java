package pandoToros.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pandoToros.Commands.PlayRedondelCommand;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.List;

public class RedondelCommand implements CommandExecutor, TabCompleter {

    private PandoDungeons plugin;

    public RedondelCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("redondel")) {

            if(strings.length < 1){
                return false;
            }

            if (strings[0].equalsIgnoreCase("play")) {
                if (commandSender instanceof Player player) {
                    PlayRedondelCommand.playRedondel(player, plugin);
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new java.util.ArrayList<>(List.of());
        completions.add("play");
        return completions;
    }
}
