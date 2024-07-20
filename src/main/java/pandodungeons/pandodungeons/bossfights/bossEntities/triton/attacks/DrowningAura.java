package pandodungeons.pandodungeons.bossfights.bossEntities.triton.attacks;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Location;
import pandodungeons.pandodungeons.bossfights.bossEntities.triton.entities.Triton;

public class DrowningAura {

    private final Triton triton;

    public DrowningAura(Triton triton) {
        this.triton = triton;
    }

    public void execute() {
        Location tritonLocation = triton.getDrowned().getLocation();

        // Generar partículas de agua en una esfera alrededor del Triton
        double radius = 10.0;
        for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 10) {
            for (double phi = 0; phi < Math.PI; phi += Math.PI / 10) {
                double x = radius * Math.cos(theta) * Math.sin(phi);
                double y = radius * Math.cos(phi);
                double z = radius * Math.sin(theta) * Math.sin(phi);

                Location particleLocation = tritonLocation.clone().add(x, y, z);
                tritonLocation.getWorld().spawnParticle(Particle.SPLASH, particleLocation, 1, 0, 0, 0, 0);
            }
        }

        // Aplicar el efecto Wither a los jugadores cercanos
        triton.getDrowned().getWorld().getNearbyEntities(tritonLocation, radius, radius, radius).stream()
                .filter(entity -> entity instanceof Player)
                .forEach(entity -> {
                    Player player = (Player) entity;
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 2));
                });
    }
}
