package pandoClass.classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pandoClass.RPGPlayer;
import pandoClass.files.RPGPlayerDataManager;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.List;

public class ClassCommand implements CommandExecutor, TabCompleter {

    private PandoDungeons plugin;

    public ClassCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("stats")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player executingPlayer = (Player) sender;

        // Sin argumentos: mostrar estadísticas del propio jugador.
        if (args.length == 0) {
            RPGPlayer rpgPlayer = new RPGPlayer(executingPlayer, plugin);
            executingPlayer.sendMessage(rpgPlayer.toDecoratedString(executingPlayer));
            return true;
        }

        if(executingPlayer.isOp()){
            if(args.length == 3 && args[0].equalsIgnoreCase("addCoins")){
                Player target = Bukkit.getPlayer(args[1]);

                if(target != null){
                    RPGPlayer rpgPlayer = new RPGPlayer(target, plugin);
                    try{
                        int coinsToAdd = Integer.parseInt(args[2]);
                        rpgPlayer.addCoins(coinsToAdd);
                        return true;
                    }catch (NumberFormatException e){
                        executingPlayer.sendMessage("No ingresaste un numero");
                    }
                }
            }

            if(args.length == 3 && args[0].equalsIgnoreCase("addLevels")){
                Player target = Bukkit.getPlayer(args[1]);

                if(target != null){
                    RPGPlayer rpgPlayer = new RPGPlayer(target, plugin);
                    try{
                        int levelsToAdd = Integer.parseInt(args[2]);
                        rpgPlayer.addLevel(levelsToAdd);
                        return true;
                    }catch (NumberFormatException e){
                        executingPlayer.sendMessage("No ingresaste un numero");
                    }
                }
            }

            if(args.length == 3 && args[0].equalsIgnoreCase("addOrbs")){
                Player target = Bukkit.getPlayer(args[1]);

                if(target != null){
                    RPGPlayer rpgPlayer = new RPGPlayer(target, plugin);
                    try{
                        int orbsToAdd = Integer.parseInt(args[2]);
                        rpgPlayer.addOrb(orbsToAdd);
                        return true;
                    }catch (NumberFormatException e){
                        executingPlayer.sendMessage("No ingresaste un numero");
                    }
                }
            }
        }




        // Subcomando "top": muestra el top 3 de jugadores por nivel.
        if (args.length == 1 && args[0].equalsIgnoreCase("top")) {
            List<RPGPlayer> allPlayers = plugin.rpgPlayerDataManager.loadAllPlayers();
            if (allPlayers.isEmpty()) {
                executingPlayer.sendMessage(ChatColor.RED + "No hay jugadores registrados aún.");
                return true;
            }

            // Ordena de mayor a menor nivel.
            allPlayers.sort((p1, p2) -> Integer.compare(p2.getLevel(), p1.getLevel()));

            executingPlayer.sendMessage(ChatColor.GOLD + "Top 5 Jugadores:");
            for (int i = 0; i < Math.min(5, allPlayers.size()); i++) {
                RPGPlayer topPlayer = allPlayers.get(i);
                String name = Bukkit.getOfflinePlayer(topPlayer.getPlayerUUID()).getName();
                if (name == null) {
                    name = "Desconocido";
                }
                executingPlayer.sendMessage(ChatColor.LIGHT_PURPLE.toString() + (i + 1) + ". " + ChatColor.GOLD + name + ChatColor.YELLOW +" - Nivel " + topPlayer.getLevel());
            }
            return true;
        }

        // Con un argumento: se interpreta como el nombre de un jugador cuyo stats se desean ver.
        if (args.length == 1) {
            String targetName = args[0];
            List<RPGPlayer> allPlayers = plugin.rpgPlayerDataManager.loadAllPlayers();
            if (allPlayers.isEmpty()) {
                executingPlayer.sendMessage(ChatColor.RED + "No hay jugadores registrados aún.");
                return true;
            }
            // Se usa Bukkit.getOfflinePlayer para poder obtener la UUID incluso si el jugador no está en línea.
            OfflinePlayer targetOffline = Bukkit.getOfflinePlayer(targetName);
            if (targetOffline == null || targetOffline.getUniqueId() == null) {
                executingPlayer.sendMessage(ChatColor.RED + "No se encontró al jugador: " + targetName);
                return true;
            }
            for(RPGPlayer player : allPlayers){
                if(player.getPlayerUUID().equals(targetOffline.getUniqueId())){
                    executingPlayer.sendMessage(player.toDecoratedString(targetName));
                    return true;
                }
            }
        }

        executingPlayer.sendMessage(ChatColor.RED + "Uso correcto: /stats [top|nombre]");
        return true;
    }




    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        List<String> completions = new java.util.ArrayList<>(List.of());
        if(strings.length == 1){
            for(Player player : Bukkit.getOnlinePlayers()){
                completions.add(player.getName());
            }
            completions.add("top");

            if(commandSender instanceof Player player){
                if(player.isOp()){
                    completions.add("addCoins");
                    completions.add("addLevels");
                    completions.add("addOrbs");
                }
            }
        }
        return completions;
    }
}
