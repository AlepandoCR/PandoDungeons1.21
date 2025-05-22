package pandoToros.game.modes.cosmetic.base.effects.team;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamPlayerEffect {

    PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    private final Particle.DustOptions dustOptions;
    private final double sphereRadius;
    private final int particleCount;
    private BukkitRunnable task;
    private static final Map<UUID, TeamPlayerEffect> activeEffects = new HashMap<>();
    private ArmorStand bannerStand;

    public TeamPlayerEffect(Color dustColor, double sphereRadius, int particleCount) {
        this.dustOptions = new Particle.DustOptions(dustColor, 1.0f);
        this.sphereRadius = sphereRadius;
        this.particleCount = particleCount;
    }

    public void applyEffect(Player player) {
        // Sincronizar la posición del ArmorStand
        Location playerLocation = player.getLocation();

        // Generar partículas en forma de esfera
        Location particleCenter = playerLocation.clone().add(0, 0, 0);
        for (int i = 0; i < particleCount; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double yOffset = (Math.random() * 2 - 1) * sphereRadius;
            double xOffset = Math.cos(angle) * Math.sqrt(sphereRadius * sphereRadius - yOffset * yOffset);
            double zOffset = Math.sin(angle) * Math.sqrt(sphereRadius * sphereRadius - yOffset * yOffset);

            Location particleLocation = particleCenter.clone().add(xOffset, yOffset, zOffset);
            player.getWorld().spawnParticle(Particle.DUST, particleLocation, 1, dustOptions);
        }
    }
}
