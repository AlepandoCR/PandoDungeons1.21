package pandodungeons.pandodungeons.commands.admin.bosses;

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
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.bossfights.fights.*;

public class BossSummonCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    public BossSummonCommand(PandoDungeons plugin) {
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
        if(!player.isOp()){player.sendMessage( ChatColor.RED + "No tienes permiso para hacer esto"); return true;}
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Uso: /dungeons boss <bossName>");
            return true;
        }
        Location location = player.getLocation();
        if(args[1].equalsIgnoreCase("queenbee")){
            QueenBeeFight fight = new QueenBeeFight(location);
            fight.startQueenBeeFight();
        }
        else if(args[1].equalsIgnoreCase("triton")){
            TritonFight fight = new TritonFight(location);
            fight.startTritonFight();
        }
        else if(args[1].equalsIgnoreCase("vex")){
            VexBossFight fight = new VexBossFight(location);
            fight.startVexBossFight();
        }
        else if(args[1].equalsIgnoreCase("guardian")){
            ForestGuardianBossFight fight = new ForestGuardianBossFight(location);
            fight.startForestGuardianBossFight();;
        }
        else if(args[1].equalsIgnoreCase("spider")){
            SpiderBossFight fight = new SpiderBossFight(location);
            fight.startSpiderBossFight();
        }

        return false;
    }
}
