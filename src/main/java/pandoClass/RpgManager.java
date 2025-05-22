package pandoClass;

import org.bukkit.entity.Player;
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class RpgManager {

    private final PandoDungeons plugin;

    private final List<RPGPlayer> players = new ArrayList<>();

    public RpgManager(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    public List<RPGPlayer> getPlayers() {
        return players;
    }

    public void addPlayer(RPGPlayer player){
        if(players.contains(player))return;
        players.add(player);
    }

    public RPGPlayer getPlayer(Player player){

        RPGPlayer returned = null;

        for(RPGPlayer rpgPlayer : players){
            if(rpgPlayer.getPlayerUUID().equals(player.getUniqueId())){
                returned = rpgPlayer;
            }
        }

        if(returned == null){
            returned = new RPGPlayer(player,plugin);
        }

        return returned;
    }

    public void removePlayer(Player player) {
        players.removeIf(rpgPlayer -> rpgPlayer.getPlayerUUID().equals(player.getUniqueId()));
    }


    public boolean isPlayerRegistered(Player player){
        for(RPGPlayer rpgPlayer : players){
            if(rpgPlayer.getPlayerUUID().equals(player.getUniqueId())){
                return true;
            }
        }
        return false;
    }


}
