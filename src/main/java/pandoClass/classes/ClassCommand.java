package pandoClass.classes;

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

public class ClassCommand implements CommandExecutor, TabCompleter {

    private final PandoDungeons plugin;

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

        if (exchangeLvls(args, executingPlayer)) return true;

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

        if ((args.length == 1 || args.length == 2 )&& args[0].equalsIgnoreCase("top")) {
            if (topStatsList(args, executingPlayer)) return true;
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("resetMejoras")){
            RPGPlayer rpgPlayer  = new RPGPlayer(executingPlayer,plugin);
            if(rpgPlayer.getCoins() >= 500){
                rpgPlayer.resetOrbs();
                rpgPlayer.removeCoins(500);
                executingPlayer.sendMessage(ChatColor.AQUA + "¡Has reiniciado tus orbes de mejora!");
            }else{
                executingPlayer.sendMessage(ChatColor.RED + "No tienes monedas para resetear tus orbes de mejora (500 monedas)");
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
            targetOffline.getUniqueId();
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

    private boolean exchangeLvls(@NotNull String[] args, Player executingPlayer) {
        if (args.length == 2 && args[0].equalsIgnoreCase("intercambio")) {
            try {
                int amount = Integer.parseInt(args[1]);

                // Verifica si el jugador está en el mundo "spawn"
                World world = executingPlayer.getWorld();
                if (!world.getName().equalsIgnoreCase("spawn")) {
                    executingPlayer.sendMessage(ChatColor.RED + "Debes estar en el mundo 'spawn' para intercambiar niveles.");
                    return true;
                }

                if(executingPlayer.getLocation().distance(new Location(world,309,92,439)) > 20){
                    executingPlayer.sendMessage(ChatColor.RED + "Debes ir al altar de intercambio /warp intercambio");
                    return true;
                }

                // Verifica si el bloque debajo del jugador es netherite
                Block blockBelow = executingPlayer.getLocation().subtract(0, 1, 0).getBlock();
                if (blockBelow.getType() != Material.NETHERITE_BLOCK) {
                    executingPlayer.sendMessage(ChatColor.RED + "Debes estar sobre el bloque de netherite para intercambiar niveles.");
                    return true;
                }

                // Ejecutar la animación e intercambio de niveles
                RPGPlayer rpgPlayer = new RPGPlayer(executingPlayer, plugin);
                rpgPlayer.animateAndApplyLevelExchange(amount);
                return true;

            } catch (NumberFormatException e) {
                executingPlayer.sendMessage(ChatColor.RED + "El valor ingresado no es un número válido.");
                return true;
            }
        }
        return false;
    }

    private boolean topStatsList(@NotNull String[] args, Player executingPlayer) {
        int page = 1; // Default page if no argument is provided

        // Check if the user has passed an argument for the page
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
                if (page < 1) {
                    executingPlayer.sendMessage(ChatColor.RED + "La página debe ser un número positivo.");
                    return true;
                }
            } catch (NumberFormatException e) {
                executingPlayer.sendMessage(ChatColor.RED + "Debes ingresar un número de página válido.");
                return true;
            }
        }

        List<RPGPlayer> allPlayers = plugin.rpgPlayerDataManager.loadAllPlayers();
        if (allPlayers.isEmpty()) {
            executingPlayer.sendMessage(ChatColor.RED + "No hay jugadores registrados aún.");
            return true;
        }

        // Ordena los jugadores por nivel (mayor a menor).
        allPlayers.sort((p1, p2) -> Integer.compare(p2.getLevel(), p1.getLevel()));

        // Configuración de paginación
        int playersPerPage = 10;
        int totalPlayers = allPlayers.size();
        int totalPages = (int) Math.ceil((double) totalPlayers / playersPerPage);

        if (page > totalPages) {
            executingPlayer.sendMessage(ChatColor.RED + "La página " + page + " no existe. Máximo: " + totalPages);
            return true;
        }

        executingPlayer.sendMessage(ChatColor.GOLD + "📜 Top Jugadores - Página " + page + "/" + totalPages + " 📜");

        int startIndex = (page - 1) * playersPerPage;
        int endIndex = Math.min(startIndex + playersPerPage, totalPlayers);

        for (int i = startIndex; i < endIndex; i++) {
            RPGPlayer topPlayer = allPlayers.get(i);
            String name = Bukkit.getOfflinePlayer(topPlayer.getPlayerUUID()).getName();
            if (name == null) {
                name = "Desconocido";
            }

            executingPlayer.sendMessage(ChatColor.YELLOW + "" + (i + 1) + ". " + ChatColor.GOLD + name +
                    ChatColor.WHITE + " - Nivel " + ChatColor.AQUA + topPlayer.getLevel());
        }

        // Flechas de navegación en el chat
        String prevPage = (page > 1) ? ChatColor.GREEN + "[⬅ Página Anterior]" : "";
        String nextPage = (page < totalPages) ? ChatColor.GREEN + "[Página Siguiente ➡]" : "";

        executingPlayer.spigot().sendMessage(
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(prevPage + " " + nextPage)
        );
        return false;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new java.util.ArrayList<>(List.of());

        if (strings.length == 1) {
            completions.add("top");
            completions.add("resetMejoras");
            completions.add("intercambio");

            if (commandSender instanceof Player player) {
                if (player.isOp()) {
                    completions.add("addCoins");
                    completions.add("addLevels");
                    completions.add("addOrbs");
                }
            }
        } else if (strings.length == 2 && strings[0].equalsIgnoreCase("intercambio")) {
            completions.add("<cantidad>");
        }

        return completions;
    }
}
