package pandoClass.classes.assasin.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class SilentStepSkill extends Skill {

    public static List<Player> silencedPlayers = new ArrayList<>();

    public SilentStepSkill(int lvl, Player player) {
        super(lvl, player);
        description = "% de no ser visto por mobs hostiles";
        displayValue = "58ef5f91a9429d272d9e0ea66e6a2e3ffaaa8fb81df9e4d3f2bc88aac0f7e75b";
    }

    @Override
    public String getName() {
        return "Sigilo";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
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
