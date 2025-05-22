package pandoClass.classes.mage.skills.orb;

import org.bukkit.entity.Player;
import pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.Map;

public class OrbsManager {
    private final PandoDungeons plugin;
    private final Map<Player, Orb> orbs = new HashMap<>();

    public OrbsManager(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    public Map<Player, Orb> getOrbs() {
        return orbs;
    }

    public boolean hasOrb(Player player) {
        return orbs.containsKey(player);
    }

    /**
     * Registra un nuevo orbe para el jugador. Si ya existe un orbe, lo elimina y lo reemplaza.
     *
     * @param player El jugador.
     * @param orb    El nuevo orbe a asignar.
     */
    public void putOrb(Player player, Orb orb) {
        if (orbs.containsKey(player)) {
            Orb existingOrb = orbs.get(player);
            if (existingOrb != null) {
                existingOrb.remove(); // Elimina el orbe anterior
            }
        }
        orbs.put(player, orb);
    }

    /**
     * Remueve el orbe asociado al jugador, si coincide con el orbe indicado.
     *
     * @param player El jugador.
     * @param orb    El orbe a remover.
     */
    public void removeOrb(Player player, Orb orb) {
        // Sólo remueve si la referencia coincide
        if (orbs.get(player) == orb) {
            orbs.remove(player);
        }
    }
}
