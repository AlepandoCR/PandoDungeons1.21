package pandoClass.quests;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pandodungeons.PandoDungeons;

import java.util.List;

public class QuestCommand implements CommandExecutor, TabCompleter {

    private final PandoDungeons plugin;

    public QuestCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("encargo")) {
            if(commandSender instanceof  Player player){
                Mission<?> mission = plugin.missionManager.getMission(player);
                if(mission == null)return false;

                mission.sendMissionMessage();
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
