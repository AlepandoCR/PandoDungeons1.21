package pandodungeons.pandodungeons.commands.admin.enchantments;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static pandoQuests.npc.human.variations.explorer.ExplorerSpawner.spawnExplorerNearPlayer;
import static pandoToros.Entities.toro.Toro.summonToro;
import static pandodungeons.pandodungeons.Game.enchantments.souleater.SoulEaterEnchantment.*;
import static pandodungeons.pandodungeons.Utils.DisplayModels.createMiniCrystal;
import static pandodungeons.pandodungeons.Utils.DisplayModels.spawnTable;
import static pandodungeons.pandodungeons.Utils.ItemUtils.*;

public class getEnchantment implements CommandExecutor {
    private JavaPlugin plugin;

    public getEnchantment(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }

        Player player = ((Player) sender).getPlayer();

        assert player != null;
        if(!player.isOp()){
            player.sendMessage(ChatColor.RED + "No tienes permiso para hacer esto");
            return true;
        }

        if(args.length == 3){
            if (args[1].equalsIgnoreCase("setSouls")) {
                int quantity = Integer.parseInt(args[2]);
                ItemStack item  = player.getInventory().getItemInMainHand();
                if(hasSoulEater(item)){
                    if(getSoulCount(item) > quantity){
                        reduceSouls(item, (getSoulCount(item) - quantity));
                    }else if(getSoulCount(item) < quantity) {
                        while(getSoulCount(item) < quantity){
                            addSoul(item);
                        }
                    }
                }
            }
            if(args[1].equalsIgnoreCase("setBateria")){
                int amount = Integer.parseInt(args[2]);
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if(isGarabiThor(itemStack)){
                    setBatery(itemStack, amount);
                }
            }
        }

        if (args.length != 2) {
            return true;
        }

        if(args[1].equalsIgnoreCase("souleater")){
            player.getInventory().addItem(createSoulEaterEnchantedBook());
        } else if (args[1].equalsIgnoreCase("garabithor")) {
            player.getInventory().addItem(garabiThor(1));
        }
        else if (args[1].equalsIgnoreCase("pergamino")) {
            player.getInventory().addItem(soulWritter(1));
        }
        else if (args[1].equalsIgnoreCase("cristal")) {
            createMiniCrystal(player.getWorld(), player.getLocation(), Material.DIAMOND_BLOCK);
        } else if (args[1].equalsIgnoreCase("toro")) {
            summonToro(player.getLocation());
        } else if (args[1].equalsIgnoreCase("mesa")){
            spawnTable(player.getLocation());
        } else if(args[1].equalsIgnoreCase("explorer")){
            spawnExplorerNearPlayer(player);
        }

        return false;
    }
}
