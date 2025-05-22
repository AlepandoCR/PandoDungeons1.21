package pandodungeons.commands.admin.companions;

import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pandodungeons.CustomEntities.Ball.BallArmadillo;
import pandodungeons.CustomEntities.pandaMount.CustomPanda;
import pandodungeons.PandoDungeons;

import static pandodungeons.Utils.CompanionUtils.selectCompanion;
import static pandodungeons.Utils.CompanionUtils.summonSelectedCompanion;

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

        if(args[1].equalsIgnoreCase("panda")){
           CustomPanda panda = new CustomPanda(EntityType.PANDA, ((CraftWorld)player.getWorld()).getHandle(), player);
           panda.setRider(player);
           return true;
        }

        if(args[1].equalsIgnoreCase("bola")){
            BallArmadillo ball = new BallArmadillo(EntityType.ARMADILLO, ((CraftWorld)player.getWorld()).getHandle());
            ball.spawn(player);
            return true;
        }

        selectCompanion(player, args[1]);
        summonSelectedCompanion(player);
        return true;
    }
}
