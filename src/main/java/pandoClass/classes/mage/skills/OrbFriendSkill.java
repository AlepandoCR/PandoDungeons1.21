package pandoClass.classes.mage.skills;

import org.bukkit.entity.Player;
import pandoClass.Skill;
import pandoClass.classes.mage.skills.orb.Orb;
import pandodungeons.PandoDungeons;

import java.net.MalformedURLException;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class OrbFriendSkill extends Skill {

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
        // Se activa solo si el jugador NO está en PvP, está vivo y EN LÍNEA
        return !isPlayerOnPvP(getPlayer()) && !owner.isDead() && owner.isOnline();
    }

    @Override
    protected void doAction() {
        // Verifica si el jugador ya tiene un orbe activo usando el mapa de orbes
        if (!plugin.orbsManager.hasOrb(owner)) {
            try {
                // Crea un nuevo orbe y lo registra en el orbsManager
                Orb newOrb = new Orb(plugin, owner, "46994d71b875f087e64dea9b4a0a5cb9f4eb9ab0e8d9060dfde7f6803baa1779", getLvl());
                plugin.orbsManager.putOrb(owner, newOrb);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Error al crear el orbe: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void reset() {
        Orb toRemove =  plugin.orbsManager.getOrbs().getOrDefault(owner,null);
       if(toRemove != null){
           toRemove.remove();
        }
    }
}
