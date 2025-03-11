package pandoClass.classes.farmer.skils;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class ExtraHarvestSkill extends Skill {

    private static final List<Player> farmingPlayers = new ArrayList<>();

    protected ExtraHarvestSkill(int lvl, UUID player) {
        super(lvl, player);
        description = "Extra Agricultura (Cada 5 niveles de esta habilidad dará 1 drop más)";
        displayValue = "ffb9b92efedfe28e449b5a2ded2e7836812d231aa7416dd9d52974ab84694b63";
    }

    @Override
    public String getName() {
        return "ExtraHarvest";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        if(!farmingPlayers.contains(owner)){
            farmingPlayers.add(owner);
        }
    }

    @Override
    public void reset() {
        farmingPlayers.remove(owner);
    }

    public static List<Player> getFarmingPlayers() {
        return farmingPlayers;
    }

    public static boolean isFarmingPlayer(Player player){
        return farmingPlayers.contains(player);
    }
}
