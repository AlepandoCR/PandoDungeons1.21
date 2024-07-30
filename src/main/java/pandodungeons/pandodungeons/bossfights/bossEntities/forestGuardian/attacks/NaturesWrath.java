package pandodungeons.pandodungeons.bossfights.bossEntities.forestGuardian.attacks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.pandodungeons.bossfights.bossEntities.forestGuardian.entities.ForestGuardian;

import java.util.List;

public class NaturesWrath {
    private final ForestGuardian forestGuardian;
    private final JavaPlugin plugin;

    public NaturesWrath(ForestGuardian forestGuardian, JavaPlugin plugin) {
        this.forestGuardian = forestGuardian;
        this.plugin = plugin;
    }

    public void execute() {
        Location location = forestGuardian.getEvoker().getLocation();
        List<Player> players = location.getWorld().getPlayers();

        for (Player player : players) {
            if(player.getWorld().equals(location.getWorld())){
                if (location.distance(player.getLocation()) < 20) {
                    player.sendMessage(ChatColor.DARK_GREEN + "¡El Guardián del Bosque desata la furia de la naturaleza!");
                    player.damage(5.0);
                    player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), 50);
                }
            }
        }
    }
}
