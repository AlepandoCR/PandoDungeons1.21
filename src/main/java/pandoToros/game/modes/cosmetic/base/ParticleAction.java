package pandoToros.game.modes.cosmetic.base;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.Particle.DustOptions;
import pandoToros.game.modes.cosmetic.CosmeticAction;

public class ParticleAction implements CosmeticAction {

    private final Particle particle;
    private final int count;
    private final Object particleData; // Opcional: puede ser DustOptions u otros datos

    /**
     * Constructor básico para partículas sin datos adicionales.
     *
     * @param particle La partícula a mostrar.
     * @param count    La cantidad de partículas.
     */
    public ParticleAction(Particle particle, int count) {
        this(particle, count, null);
    }

    /**
     * Constructor con datos opcionales (por ejemplo, DustOptions para partículas DUST).
     *
     * @param particle     La partícula a mostrar.
     * @param count        La cantidad de partículas.
     * @param particleData Datos opcionales específicos de la partícula (por ejemplo, DustOptions).
     */
    public ParticleAction(Particle particle, int count, Object particleData) {
        this.particle = particle;
        this.count = count;
        this.particleData = particleData;
    }

    @Override
    public void execute(Player player, Location location) {
        if (particleData != null) {
            // Usa datos de partícula si están presentes
            location.getWorld().spawnParticle(particle, location, count, particleData);
        } else {
            // Genera partículas normales sin datos adicionales
            location.getWorld().spawnParticle(particle, location, count);
        }
    }
}
