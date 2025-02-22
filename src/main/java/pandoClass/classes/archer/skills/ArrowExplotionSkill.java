package pandoClass.classes.archer.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class ArrowExplotionSkill extends Skill {

    public static List<Player> explosiveAmmo = new ArrayList<>();

    public ArrowExplotionSkill(int lvl, Player player) {
        super(lvl, player);
        description = "Las flechas normales explotan";
        displayValue = "9b20ff173bd17b2c4f2eb21f3c4b43841a14b31dfbfd354a3bec8263af562b";
    }

    @Override
    public String getName() {
        return "Flecha Explosiva";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
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
