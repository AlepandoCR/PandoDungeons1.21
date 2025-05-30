package pandodungeons.commands.game;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pandodungeons.Game.PlayerStatsManager;
import pandodungeons.Utils.LocationUtils;

import static pandodungeons.Utils.ItemUtils.physicalPrestige;

public class PrestigeWithdraw implements CommandExecutor {
    private final JavaPlugin plugin;
    public PrestigeWithdraw(JavaPlugin plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        Player player = (((Player) sender).getPlayer());
        if(player == null){
            return false;
        }

        if(LocationUtils.isDungeonWorld(player.getWorld().getName()) || LocationUtils.hasActiveDungeon(player.getUniqueId().toString())){
            player.sendMessage(ChatColor.RED + "No puedes hacer esto en una dungeon");
            return true;
        }

        if(args.length == 2){
            int castedQuantity = 0;
            try{
                castedQuantity = Integer.parseInt(args[1]);
            }catch (NumberFormatException err){
                player.sendMessage("No dijitaste un numero como argumento");
            }

            if(castedQuantity > 0){
                PlayerStatsManager statsManager = PlayerStatsManager.getPlayerStatsManager(player);
                if(statsManager.getPrestige() >= castedQuantity){
                    boolean emptyStack = false;
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item == null) {
                            emptyStack = true;
                            statsManager.setPrestige(statsManager.getPrestige() - castedQuantity);
                            player.getInventory().addItem(physicalPrestige(castedQuantity));
                            break;
                        }
                    }
                    if(!emptyStack){
                        player.sendMessage(ChatColor.RED + "Tu inventario esta lleno");
                    }
                }else{
                    player.sendMessage(ChatColor.DARK_RED + "No tienes nivel de prestigio suficiente");
                }
            }else{
                player.sendMessage("Te deberia restar prestigios por esto...");
            }
        }else{
            PlayerStatsManager statsManager = PlayerStatsManager.getPlayerStatsManager(player);
            if(statsManager.getPrestige() > 0){
                boolean emptyStack = false;
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item == null) {
                        emptyStack = true;
                        statsManager.setPrestige(statsManager.getPrestige() - 1);
                        player.getInventory().addItem(physicalPrestige(1));
                        break;
                    }
                }
                if(!emptyStack){
                    player.sendMessage(ChatColor.RED + "Tu inventario esta lleno");
                }
            }else{
                player.sendMessage(ChatColor.DARK_RED + "No tienes nivel de prestigio virtual");
            }
        }
        return true;
    }
}
