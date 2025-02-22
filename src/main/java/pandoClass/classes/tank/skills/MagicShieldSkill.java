package pandoClass.classes.tank.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;

import static pandoClass.RPGListener.isPlayerOnPvP;
import static pandoClass.RPGListener.magicShieldPlayers;

public class MagicShieldSkill extends Skill {
    public MagicShieldSkill(int lvl, Player player) {
        super(lvl, player);
        description = "Reduce el daño proveniente de efectos y/o proyectiles";
        displayValue = "8ec9687ccbe97eb3546e5f9e810a1c4ba82c522f9aa1a069db8871b23b023140";
    }

    @Override
    public String getName() {
        return "Escudo Mágico";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
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
