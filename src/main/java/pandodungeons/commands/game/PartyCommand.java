package pandodungeons.commands.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pandodungeons.PandoDungeons;
import pandodungeons.Utils.PlayerParty;
import pandodungeons.Utils.PlayerPartyList;

import java.util.ArrayList;
import java.util.List;

import static pandodungeons.Utils.LocationUtils.hasActiveDungeon;

public class PartyCommand implements CommandExecutor, TabCompleter {

    private final PandoDungeons plugin;

    public PartyCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("party")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
                return true;
            }

            if(hasActiveDungeon(player.getUniqueId().toString())){
                player.sendMessage(ChatColor.DARK_RED + "No puedes crear una party en medio de una dungeon");
                return true;
            }

            PlayerPartyList playerPartyList = plugin.playerPartyList;

            if(args.length < 1){
                return false;
            }

            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "create":
                    if (playerPartyList.isMember(player) || playerPartyList.isOwner(player)) {
                        player.sendMessage(ChatColor.RED + "Ya estás en una party. Debes salir antes de crear una nueva.");
                    } else {
                        playerPartyList.createParty(player);
                        player.sendMessage(ChatColor.GREEN + "Has creado una nueva party.");
                    }
                    break;

                case "invite":
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "Debes especificar a quién quieres invitar.");
                        return true;
                    }

                    Player invitee = Bukkit.getPlayer(args[1]);
                    if (invitee == null) {
                        player.sendMessage(ChatColor.RED + "El jugador especificado no está en línea.");
                        return true;
                    }

                    PlayerParty party = playerPartyList.getPartyByOwner(player);
                    if (party == null) {
                        player.sendMessage(ChatColor.RED + "No eres el líder de ninguna party.");
                    } else {
                        if (party.invitePlayer(invitee, plugin)) {
                            player.sendMessage(ChatColor.GREEN + "Has invitado a " + invitee.getName() + " a tu party.");
                        } else {
                            player.sendMessage(ChatColor.RED + "Ese jugador ya está en una party o ya fue invitado.");
                        }
                    }
                    break;

                case "accept":
                    for (PlayerParty playerParty : playerPartyList.getAllParties()) {
                        playerParty.handleInvitationResponse(player, true);
                    }
                    break;

                case "deny":
                    for (PlayerParty playerParty : playerPartyList.getAllParties()) {
                        playerParty.handleInvitationResponse(player, false);
                    }
                    break;

                case "info":
                    PlayerParty partyInfo = null;

                    if (playerPartyList.isOwner(player)) {
                        partyInfo = playerPartyList.getPartyByOwner(player);
                    } else if (playerPartyList.isMember(player)) {
                        partyInfo = playerPartyList.getPartyByMember(player);
                    }

                    if (partyInfo == null) {
                        player.sendMessage(ChatColor.RED + "No estás en ninguna party.");
                    } else {
                        String partyName = partyInfo.getOwner().getName() + "'s Party";
                        StringBuilder memberList = new StringBuilder();
                        for (Player member : partyInfo.getMembers()) {
                            memberList.append(member.getName()).append(", ");
                        }
                        // Elimina la última coma y espacio si hay miembros
                        if (!memberList.isEmpty()) {
                            memberList.setLength(memberList.length() - 2);
                        }

                        player.sendMessage(ChatColor.GREEN + "Nombre de la Party: " + ChatColor.YELLOW + partyName);
                        player.sendMessage(ChatColor.GREEN + "Miembros: " + ChatColor.YELLOW + memberList.toString());
                        if (partyInfo.getOwner().equals(player)) {
                            player.sendMessage(ChatColor.GREEN + "Eres el dueño de la party.");
                        } else {
                            player.sendMessage(ChatColor.GREEN + "Eres un miembro de la party.");
                        }
                    }
                    break;

                case "delete":
                    if (playerPartyList.isOwner(player)) {
                        playerPartyList.removePartyByOwner(player);
                        player.sendMessage(ChatColor.GREEN + "Tu party ha sido eliminada.");
                    } else {
                        player.sendMessage(ChatColor.RED + "No tienes permiso para eliminar la party.");
                    }
                    break;

                case "leave":
                    if (playerPartyList.isMember(player) && !playerPartyList.isOwner(player)) {
                        playerPartyList.getPartyByMember(player).removeMember(player);
                        player.sendMessage(ChatColor.GREEN + "Has abandonado la party.");
                    } else if (playerPartyList.isOwner(player)) {
                        player.sendMessage(ChatColor.RED + "No puedes abandonar la party porque eres el dueño. Usa /party delete para eliminarla.");
                    } else {
                        player.sendMessage(ChatColor.RED + "No estás en ninguna party.");
                    }
                    break;

                default:
                    player.sendMessage(ChatColor.RED + "Subcomando no reconocido. Usa: /party <create|invite|accept|deny|info|delete|leave>.");
                    break;
            }

            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new java.util.ArrayList<>(List.of());
        if (strings.length == 2) {
            if(strings[0].equalsIgnoreCase("invite")){
                completions.addAll(AllPlayerNamesToStringList());
            }
        }else{
            completions.add("invite");
            completions.add("create");
            completions.add("accept");
            completions.add("deny");
            completions.add("delete");
            completions.add("leave");
            completions.add("info");
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
