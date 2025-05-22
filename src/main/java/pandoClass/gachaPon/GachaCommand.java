package pandoClass.gachaPon;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pandodungeons.PandoDungeons;

import java.util.Collections;
import java.util.List;

public class GachaCommand implements CommandExecutor, TabCompleter {

    private PandoDungeons plugin;

    public GachaCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("gachatoken")) {

            if(commandSender instanceof Player player){
                if(!player.isOp()){
                    return false;
                }
            }

            if(strings.length == 2){
                Player player = Bukkit.getPlayer(strings[0]);
                if(player != null && player.isOnline()){
                    int amount = Integer.parseInt(strings[1]);
                    player.getInventory().addItem(Collections.nCopies(amount, plugin.prizeManager.gachaToken()).toArray(new ItemStack[0]));
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
