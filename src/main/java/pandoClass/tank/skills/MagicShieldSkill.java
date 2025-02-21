package pandoClass.tank.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import static pandoClass.RPGListener.isPlayerOnPvP;
import static pandoClass.RPGListener.magicShieldPlayers;

public class MagicShieldSkill extends Skill {
    public MagicShieldSkill(int lvl, Player player) {
        super(lvl, player);
    }

    @Override
    protected boolean canActivate() {
        return isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        if(!magicShieldPlayers.contains(owner)){
            magicShieldPlayers.add(owner);
        }
    }

    @Override
    public void reset() {
        magicShieldPlayers.remove(owner);
    }
}
