package pandoClass.classes.mage.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;
import pandoClass.classes.mage.skills.orb.Orb;
import pandodungeons.pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.util.UUID;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class OrbFriendSkill extends Skill {

    private boolean hasOrb = false;

    private final PandoDungeons plugin;

    public OrbFriendSkill(int lvl, Player player, PandoDungeons plugin) {
        super(lvl, player);
        this.plugin = plugin;
        description = "Tienes un amigo orbe que te ayudará de distintas formas";
        displayValue = "46994d71b875f087e64dea9b4a0a5cb9f4eb9ab0e8d9060dfde7f6803baa1779";
    }

    @Override
    public String getName() {
        return "Amigo Orbe";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer()) && !owner.isDead();
    }

    @Override
    protected void doAction() {
        // Verifica si el jugador ya tiene un orbe activo
        if (!plugin.orbsManager.getOrbs().containsKey(owner)) {
            try {
                // Crea un nuevo orbe solo si no existe uno para el jugador
                plugin.orbsManager.putOrb(owner, new Orb(plugin, owner, "46994d71b875f087e64dea9b4a0a5cb9f4eb9ab0e8d9060dfde7f6803baa1779", getLvl()));
                hasOrb = true;
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void reset() {

    }
}
