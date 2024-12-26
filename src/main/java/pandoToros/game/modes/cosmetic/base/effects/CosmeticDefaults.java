package pandoToros.game.modes.cosmetic.base.effects;

import org.bukkit.Color;
import org.bukkit.Particle;
import pandoToros.game.modes.cosmetic.CosmeticAction;
import pandoToros.game.modes.cosmetic.base.ParticleAction;

import java.util.HashMap;
import java.util.Map;

public class CosmeticDefaults {

    private static final Map<String, CosmeticAction> defaults = new HashMap<>();

    static {
        // Explosión roja
        defaults.put("EXPLOSION_RED", new ParticleAction(Particle.DUST, 50, new Particle.DustOptions(Color.RED, 3)));

        // Explosión verde
        defaults.put("EXPLOSION_GREEN", new ParticleAction(Particle.DUST, 50, new Particle.DustOptions(Color.GREEN, 3)));

        // Explosión azul
        defaults.put("EXPLOSION_BLUE", new ParticleAction(Particle.DUST, 50, new Particle.DustOptions(Color.BLUE, 3)));
    }

    /**
     * Obtiene una acción cosmética por su clave.
     *
     * @param key La clave de la acción cosmética.
     * @return La acción cosmética correspondiente, o null si no existe.
     */
    public static CosmeticAction getDefault(String key) {
        return defaults.get(key);
    }

    /**
     * Agrega una acción cosmética personalizada al conjunto de valores predeterminados.
     *
     * @param key    La clave de la nueva acción cosmética.
     * @param action La acción cosmética.
     */
    public static void addDefault(String key, CosmeticAction action) {
        defaults.put(key, action);
    }
}
