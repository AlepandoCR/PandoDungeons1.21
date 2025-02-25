package pandoClass.classes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pandoClass.RPGPlayer;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.List;

public class ClassCommand implements CommandExecutor, TabCompleter {

    private PandoDungeons plugin;

    public ClassCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("stats")) {

            if(commandSender instanceof Player player){
                RPGPlayer rpgPlayer = new RPGPlayer(player);
                if(strings.length == 0){
                    player.sendMessage(rpgPlayer.toDecoratedString(player));
                }
            }
        }


        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new java.util.ArrayList<>(List.of());
        return completions;
    }
}
