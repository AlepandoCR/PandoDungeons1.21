package pandoClass.classes.farmer.skils;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class TameSkill extends Skill {

    private static final List<Player> tamingPlayers = new ArrayList<>();

    protected TameSkill(int lvl, UUID player) {
        super(lvl, player);
        description = "¡Los mobs neutrales y amigos te ayudarán a luchar!";
        displayValue = "1f7b6b2d509b3b7a9338e7596aa7fbd640ae39d25746a923a63e87f5f500b55d";
    }

    @Override
    public String getName() {
        return "Tame";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        if(!tamingPlayers.contains(owner)){
            tamingPlayers.add(owner);
        }
    }

    @Override
    public void reset() {
        tamingPlayers.remove(owner);
    }

    public static List<Player> getTamingPlayers() {
        return tamingPlayers;
    }

    public static boolean isTamingPlayer(Player player){
        return tamingPlayers.contains(player);
    }
}
