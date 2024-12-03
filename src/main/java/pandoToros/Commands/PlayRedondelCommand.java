package pandoToros.Commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pandoToros.game.RedondelGame;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.Utils.PlayerParty;
import pandodungeons.pandodungeons.Utils.PlayerPartyList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static pandoToros.game.ArenaMaker.extractUsername;
import static pandoToros.game.RedondelGame.hasActiveRedondel;
import static pandodungeons.pandodungeons.Utils.LocationUtils.hasActiveDungeon;

public class PlayRedondelCommand {
    public static void playRedondel(Player player, PandoDungeons plugin){

        PlayerPartyList partyList = plugin.playerPartyList;

        if(hasActiveRedondel(player)){
            return;
        }

        if(hasActiveDungeon(player.getUniqueId().toString())){
            return;
        }

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
