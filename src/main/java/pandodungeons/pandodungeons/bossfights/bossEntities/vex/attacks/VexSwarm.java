package pandodungeons.pandodungeons.bossfights.bossEntities.vex.attacks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.pandodungeons.bossfights.bossEntities.vex.entities.MinionVex;
import pandodungeons.pandodungeons.bossfights.bossEntities.vex.entities.VexBoss;

import java.util.ArrayList;
import java.util.List;

public class VexSwarm {
    private final VexBoss vexBoss;
    private final JavaPlugin plugin;
    private final List<MinionVex> minions;

    public VexSwarm(VexBoss vexBoss, JavaPlugin plugin) {
        this.vexBoss = vexBoss;
        this.plugin = plugin;
        this.minions = new ArrayList<>();
    }

    public void execute() {
        Location vexLocation = vexBoss.getVex().getLocation();
        for (int i = 0; i < 3; i++) {
            MinionVex minion = new MinionVex(plugin, vexLocation, vexBoss);
            minions.add(minion);
        }
        for (Player player : vexLocation.getWorld().getPlayers()) {
            player.sendMessage(ChatColor.DARK_PURPLE + "¡El Vex invoca una horda de Minions!");
        }
    }

    public List<MinionVex> getMinions() {
        return minions;
    }
}
