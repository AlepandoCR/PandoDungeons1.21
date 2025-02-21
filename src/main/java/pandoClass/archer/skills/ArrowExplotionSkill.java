package pandoClass.archer.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class ArrowExplotionSkill extends Skill {

    public static List<Player> explosiveAmmo = new ArrayList<>();

    public ArrowExplotionSkill(int lvl, Player player) {
        super(lvl, player);
    }

    @Override
    protected boolean canActivate() {
        return isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        if(!explosiveAmmo.contains(owner)){
            explosiveAmmo.add(owner);
        }
    }

    @Override
    public void reset() {
        explosiveAmmo.remove(owner);
    }
}
