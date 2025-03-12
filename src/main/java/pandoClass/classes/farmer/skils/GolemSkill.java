package pandoClass.classes.farmer.skils;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class GolemSkill extends Skill {

    private static final List<Player> golemPlayers = new ArrayList<>();

    public GolemSkill(int lvl, Player player) {
        super(lvl, player);
        description = "Invocarás golems que te ayudan, la cantidad aumenta cada 10 mejoras";
        displayValue = "3985f70b5ac43de72aa80772e141e4f538b2320c7a3b2eaf3d94c09a332413f2";
    }

    @Override
    public String getName() {
        return "Golems";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        if(!golemPlayers.contains(owner)){
            golemPlayers.add(owner);
        }
    }

    @Override
    public void reset() {
        golemPlayers.remove(owner);
    }

    public static List<Player> getGolemPlayers() {
        return golemPlayers;
    }

    public static boolean isGolemPlayer(Player player){
        return golemPlayers.contains(player);
    }


    public static void removeGolemPlayer(Player player){
        golemPlayers.remove(player);
    }
}
