package pandodungeons.pandodungeons.commands.game;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pandodungeons.pandodungeons.Elements.LootTableManager;
import pandodungeons.pandodungeons.Game.PlayerStatsManager;
import pandodungeons.pandodungeons.Utils.LocationUtils;
import pandodungeons.pandodungeons.Utils.StructureUtils;

import static pandodungeons.pandodungeons.Utils.ItemUtils.physicalPrestige;

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
            return false;
        }

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
        return true;
    }
}
