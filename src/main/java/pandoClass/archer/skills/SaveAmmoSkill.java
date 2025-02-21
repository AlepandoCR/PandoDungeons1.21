package pandoClass.archer.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class SaveAmmoSkill extends Skill {

    public static List<Player> playersSavingAmmo = new ArrayList<>();

    public SaveAmmoSkill(int lvl, Player player) {
        super(lvl, player);
    }

    @Override
    protected boolean canActivate() {
        return isPlayerOnPvP(getPlayer());
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
