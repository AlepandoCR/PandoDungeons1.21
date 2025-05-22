package pandoClass.classes.mage.skills.orb.skills;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.classes.mage.skills.orb.Orb;
import pandodungeons.PandoDungeons;

public abstract class OrbSkill {
    protected final PandoDungeons plugin;
    protected final Orb orb;
    protected final Player owner;
    protected BukkitRunnable task;

    public OrbSkill(PandoDungeons plugin, Orb orb) {
        this.plugin = plugin;
        this.orb = orb;
        this.owner = orb.getOwner();
    }

    public abstract void start(int level);

    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }
}
