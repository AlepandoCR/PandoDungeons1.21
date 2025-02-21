package pandoClass.assasin.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class LifeStealSkill extends Skill {

    public static List<Player> lifeStealingPlayers = new ArrayList<>();

    public LifeStealSkill(int lvl, Player player) {
        super(lvl, player);
    }

    @Override
    protected boolean canActivate() {
        return isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        if(!lifeStealingPlayers.contains(owner)){
            lifeStealingPlayers.add(owner);
        }
    }

    @Override
    public void reset() {
        lifeStealingPlayers.remove(owner);
    }
}
