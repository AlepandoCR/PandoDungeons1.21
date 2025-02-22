package pandoClass.classes.assasin.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class LifeStealSkill extends Skill {

    public static List<Player> lifeStealingPlayers = new ArrayList<>();

    public LifeStealSkill(int lvl, Player player) {
        super(lvl, player);
        description = "Roba un % del daño que infliges  a mobs";
        displayValue = "25a7007007d5a396d6049c71ab6ff5fedb6ca3e1753b3fd6f13bb6946a7e0daf";
    }

    @Override
    public String getName() {
        return "Robo de vida";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
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
