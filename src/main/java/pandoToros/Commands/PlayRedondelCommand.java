package pandoToros.Commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import pandoToros.game.RedondelGame;
import pandodungeons.PandoDungeons;
import pandodungeons.Utils.PlayerPartyList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static pandoToros.game.RedondelGame.hasActiveRedondel;
import static pandodungeons.Utils.LocationUtils.hasActiveDungeon;

public class PlayRedondelCommand {
    public static void playRedondel(Player player, PandoDungeons plugin, String[] args){

        PlayerPartyList partyList = plugin.playerPartyList;

        if(hasActiveRedondel(player)){
            return;
        }

        if(hasActiveDungeon(player.getUniqueId().toString())){
            return;
        }

        boolean classic = false;

        if(args.length == 2){
            if(args[1].equalsIgnoreCase("classic")){
                classic = true;
            }
        }

        if(partyList.isMember(player)){
            if(partyList.isOwner(player)){
                RedondelGame.StartRedondel(player.getName().toLowerCase(Locale.ROOT), partyList.getPartyByOwner(player).getMembers(), classic,plugin);
            } else {
                player.sendMessage(ChatColor.AQUA + "No puedes iniciar la partida porque estas en un party y no eres el dueño");
            }
        }else{
            List<Player> list = new ArrayList<>();
            list.add(player);
            RedondelGame.StartRedondel(player.getName().toLowerCase(Locale.ROOT), list, classic,plugin);
        }
    }
}
