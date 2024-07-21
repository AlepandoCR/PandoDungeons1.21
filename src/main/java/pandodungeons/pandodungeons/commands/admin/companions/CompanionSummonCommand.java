package pandodungeons.pandodungeons.commands.admin.companions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pandodungeons.pandodungeons.CustomEntities.CompanionLoops.*;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.Utils.CompanionUtils;
import pandodungeons.pandodungeons.commands.game.CompanionSelection;

public class CompanionSummonCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;

    public CompanionSummonCommand(PandoDungeons plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por jugadores.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "No tienes permiso para hacer esto.");
            return true;
        }
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Uso: /dungeons companion <companionName>");
            return true;
        }

        Companion compa;

        Location location = player.getLocation();
        if (args[1].equalsIgnoreCase("allay")) {
            if(CompanionUtils.hasUnlockedCompanion(player, "allay")){
                compa = new CompanionAllay(player);
                player.sendMessage(ChatColor.GREEN + "Has invocado un Allay companion.");
            }
        }
        else if(args[1].equalsIgnoreCase("breeze")){
            if(CompanionUtils.hasUnlockedCompanion(player, "breeze")){
                compa = new CompanionBreeze(player);
                player.sendMessage(ChatColor.AQUA + "Has invocado un Breeze companion.");
            }
        }
        else if(args[1].equalsIgnoreCase("armadillo")){
            if(CompanionUtils.hasUnlockedCompanion(player, "armadillo")){
                compa = new CompanionArmadillo(player);
                player.sendMessage(ChatColor.AQUA + "Has invocado un Armadillo companion.");            }
        }
        else if(args[1].equalsIgnoreCase("oso")){
            if(CompanionUtils.hasUnlockedCompanion(player, "oso")){
                compa = new CompanionPolarBear(player);
                player.sendMessage(ChatColor.AQUA + "Has invocado un Oso companion.");
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "Companion no reconocido. Los companions disponibles son: allay.");
        }

        return true;
    }
}
