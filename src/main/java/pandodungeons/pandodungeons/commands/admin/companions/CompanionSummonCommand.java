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
import pandodungeons.pandodungeons.CustomEntities.CompanionLoops.CompanionArmadillo;
import pandodungeons.pandodungeons.CustomEntities.CompanionLoops.CompanionBreeze;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.CustomEntities.CompanionLoops.CompanionAllay;
import pandodungeons.pandodungeons.Utils.CompanionUtils;

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

        Location location = player.getLocation();
        if (args[1].equalsIgnoreCase("allay")) {
            if(CompanionUtils.hasUnlockedCompanion(player, "allay")){
                CompanionAllay allayCompanion = new CompanionAllay(player);
                player.sendMessage(ChatColor.GREEN + "Has invocado un Allay companion.");
            }
        }
        else if(args[1].equalsIgnoreCase("breeze")){
            if(CompanionUtils.hasUnlockedCompanion(player, "breeze")){
                CompanionBreeze companionBreeze = new CompanionBreeze(player);
                player.sendMessage(ChatColor.AQUA + "Has invocado un Breeze companion.");
            }
        }
        else if(args[1].equalsIgnoreCase("armadillo")){
            if(CompanionUtils.hasUnlockedCompanion(player, "armadillo")){
                CompanionArmadillo companionArmadillo = new CompanionArmadillo(player);
                player.sendMessage(ChatColor.AQUA + "Has invocado un Armadillo companion.");
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "Companion no reconocido. Los companions disponibles son: allay.");
        }

        return true;
    }
}
