package pandoToros.Commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import pandoToros.game.RedondelGame;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.Utils.PlayerParty;
import pandodungeons.pandodungeons.Utils.PlayerPartyList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayRedondelCommand {
    public static void playRedondel(Player player, PandoDungeons plugin){

        PlayerPartyList partyList = plugin.playerPartyList;

        if(partyList.isMember(player)){
            if(partyList.isOwner(player)){
                RedondelGame.StartRedondel(player.getName().toLowerCase(Locale.ROOT), partyList.getPartyByOwner(player).getMembers());
            } else {
                player.sendMessage(ChatColor.AQUA + "No puedes iniciar la partida porque estas en un party y no eres el dueño");
            }
        }else{
            List<Player> list = new ArrayList<>();
            list.add(player);
            RedondelGame.StartRedondel(player.getName().toLowerCase(Locale.ROOT), list);
        }
    }
}
