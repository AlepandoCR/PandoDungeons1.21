package pandoClass.assasin.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class SilentStepSkill extends Skill {

    public static List<Player> silencedPlayers = new ArrayList<>();

    public SilentStepSkill(int lvl, Player player) {
        super(lvl, player);
    }

    @Override
    protected boolean canActivate() {
        return isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        if(!silencedPlayers.contains(owner)){
            silencedPlayers.add(owner);
        }
    }

    @Override
    public void reset() {
        silencedPlayers.remove(owner);
    }
}
