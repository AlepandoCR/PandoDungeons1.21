package pandoClass;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Squad {
    private final List<RPGPlayer> players = new ArrayList<>();

    private int avrglvl;

    public Squad(List<Player> players){
        sortPlayers(players);
        avrglvls();
    }

    public void sortPlayers(List<Player> players){
        for(Player player : players){
           // this.players.add(new RPGPlayer(player, ));
        }
    }

    public List<RPGPlayer> getRPGPlayers(){
        return players;
    }

    public List<Player> getPlayers(){
        List<Player> aux = new ArrayList<>();
        for(RPGPlayer rpgPlayer : players){
            aux.add(rpgPlayer.getPlayer());
        }
        return aux;
    }

    public int getAvrglvl(){
        return avrglvl;
    }

    private void avrglvls(){
        int aux = 0;
        for(RPGPlayer rpgPlayer : players){
            aux += rpgPlayer.getLevel();
        }
        aux = aux/players.size();
        avrglvl = aux;
    }
}
