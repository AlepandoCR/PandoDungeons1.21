package pandoClass.classes.mage.skills.orb;

import org.bukkit.entity.Player;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.Map;

public class OrbsManager {
    private final PandoDungeons plugin;

    private final Map<Player,Orb> orbs = new HashMap<>();

    public OrbsManager(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    public Map<Player, Orb> getOrbs() {
        return orbs;
    }

    public boolean hasOrb(Player player){
        return orbs.containsKey(player);
    }

    public void putOrb(Player player, Orb orb){
        if (!hasOrb(player)) {
            orbs.put(player,orb);
        }
    }

    public void removeOrb(Player player, Orb orb){
        orb.remove();
        orbs.remove(player,orb);
    }

}
