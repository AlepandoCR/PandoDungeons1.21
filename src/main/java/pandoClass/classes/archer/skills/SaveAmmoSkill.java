package pandoClass.classes.archer.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class SaveAmmoSkill extends Skill {

    public static List<Player> playersSavingAmmo = new ArrayList<>();

    public SaveAmmoSkill(int lvl, Player player) {
        super(lvl, player);
        description = "% de no gastar proyectiles";
        displayValue = "822a48a5759eddef9e2918fc85996f8491cc92578d54dcd62e2b6d913bfb421e";
    }

    @Override
    public String getName() {
        return "Ahorrar Munición";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        if(!playersSavingAmmo.contains(owner)){
            playersSavingAmmo.add(owner);
        }
    }

    @Override
    public void reset() {
        playersSavingAmmo.remove(owner);
    }
}
