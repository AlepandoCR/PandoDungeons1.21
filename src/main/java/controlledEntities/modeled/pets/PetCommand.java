package controlledEntities.modeled.pets;

import org.bukkit.*;
import org.bukkit.block.Block;
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

public class PetCommand implements CommandExecutor, TabCompleter {

    private final PandoDungeons plugin;

    public PetCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player executingPlayer)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        if(args.length == 2){
            if(args[1].equalsIgnoreCase("quitar")){
                plugin.petsManager.destroyPets(executingPlayer);
                executingPlayer.sendMessage( ChatColor.YELLOW + "[" + ChatColor.GOLD + "Macotas" + ChatColor.YELLOW + "]" + "Haz quitado tu mascota");
            }
        }else{
            PetSelectionMenu.openMenu(executingPlayer);
        }

        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new java.util.ArrayList<>(List.of());

        completions.add("quitar");

        return completions;
    }
}
