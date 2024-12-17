package pandodungeons.pandodungeons.commands.Management;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pandodungeons.pandodungeons.Game.PlayerStatsManager;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.commands.admin.companions.CompanionSummonCommand;
import pandodungeons.pandodungeons.commands.admin.companions.CompanionUnlock;
import pandodungeons.pandodungeons.commands.admin.enchantments.getEnchantment;
import pandodungeons.pandodungeons.commands.game.*;
import pandodungeons.pandodungeons.commands.admin.ResetStatsCommand;
import pandodungeons.pandodungeons.commands.admin.bosses.BossSummonCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final getEnchantment enchantment;
    private final MountCommand mountCommand;
    private final DungeonsPlayCommand playCommand;
    private final DungeonsLeaveCommand leaveCommand;
    private final DungeonsStatsCommand statsCommand;
    private final ResetStatsCommand resetStatsCommand;
    private final BossSummonCommand bossSummon;
    private final DungeonsTopCommand topCommand;
    private final GiveStatsBookCommand statsBookCommand;
    private final CompanionSummonCommand companionCommand;
    private final CompanionSelection companionSelection;
    private final CompanionUnlock companionUnlock;
    private final PrestigeWithdraw prestigeWithdraw;
    private final PandoDungeons plugin;
    public CommandManager(PandoDungeons plugin) {
        this.playCommand = new DungeonsPlayCommand(plugin);
        this.leaveCommand = new DungeonsLeaveCommand(plugin);
        this.statsCommand = new DungeonsStatsCommand(plugin);
        this.resetStatsCommand = new ResetStatsCommand(plugin);
        this.bossSummon = new BossSummonCommand(plugin);
        this.topCommand = new DungeonsTopCommand(plugin);
        this.statsBookCommand = new GiveStatsBookCommand(plugin);
        this.companionCommand = new CompanionSummonCommand(plugin);
        this.companionSelection = new CompanionSelection(plugin);
        this.companionUnlock = new CompanionUnlock(plugin);
        this.prestigeWithdraw = new PrestigeWithdraw(plugin);
        this.enchantment = new getEnchantment(plugin);
        this.mountCommand = new MountCommand();
        this.plugin = plugin;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar si el comando ejecutado es el comando "dungeons"
        if (command.getName().equalsIgnoreCase("dungeons")) {
            // Verificar si no se proporcionaron argumentos
            if (args.length == 0) {
                sender.sendMessage("Comandos disponibles: /dungeons play, /dungeons leave, /dungeon stats");
                return true;
            }
        }

        return switch (args[0].toLowerCase()) {
            case "play" -> playCommand.onCommand(sender, command, label, args);
            case "leave" -> leaveCommand.onCommand(sender, command, label, args);
            case "stats", "statistics" -> statsCommand.onCommand(sender, command, label, args);
            case "resetstats" -> resetStatsCommand.onCommand(sender, command, label, args);
            case "boss" -> bossSummon.onCommand(sender, command, label, args);
            case "top" -> topCommand.onCommand(sender, command, label, args);
            case "statsbook" -> statsBookCommand.onCommand(sender, command, label, args);
            case "companion" -> companionCommand.onCommand(sender, command, label, args);
            case "companionmenu" -> companionSelection.onCommand(sender, command, label, args);
            case "unlockcompanion" -> companionUnlock.onCommand(sender, command, label, args);
            case "retirarprestigio" -> prestigeWithdraw.onCommand(sender, command, label, args);
            case "enchantment" -> enchantment.onCommand(sender, command, label, args);
            case "montura" -> mountCommand.onCommand(sender, command, label, args);
            default -> {
                sender.sendMessage("Comando desconocido. Uso: /dungeons play, /dungeons leave, /dungeons stats, /dungeons top, /dungeons statsbook, /dungeons companionmenu, /dungeons retirarprestigio");
                yield true;
            }
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        Player player = null;

        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (command.getName().equalsIgnoreCase("dungeons")) {
            if (args.length == 1) {
                if (player != null && player.isOp()) {
                    completions.add("resetstats");
                    completions.add("boss");
                    completions.add("companion");
                    completions.add("unlockcompanion");
                    completions.add("enchantment");
                }
                completions.add("play");
                completions.add("leave");
                completions.add("stats");
                completions.add("statistics");
                completions.add("top");
                completions.add("statsbook");
                completions.add("companionmenu");
                completions.add("retirarprestigio");
                completions.add("montura");
            } else if (args.length == 2) { // Verifica si es el segundo argumento
                if (args[0].equalsIgnoreCase("stats") || args[1].equalsIgnoreCase("resetstats")) {
                    completions.addAll(PlayerStatsManager.getAllPlayerNames());
                }
            } else if (args.length == 3) {
                if(args[1].equalsIgnoreCase("invite")){
                    completions.addAll(AllPlayerNamesToStringList());
                }
            }

        }
        return completions;
    }

    private List<String> AllPlayerNamesToStringList(){
        List<? extends Player> players = plugin.getServer().getOnlinePlayers().stream().toList();
        List<String> playersNames = new ArrayList<>();
        for(Player player1 : players){
            playersNames.add(player1.getName());
        }
        return playersNames;
    }
}
