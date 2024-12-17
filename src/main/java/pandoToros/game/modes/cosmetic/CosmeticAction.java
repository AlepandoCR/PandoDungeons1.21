package pandoToros.game.modes.cosmetic;

import org.bukkit.Location;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface CosmeticAction {
    /**
     * Ejecuta la acción cosmética.
     *
     * @param player   El jugador que marcó el gol (puede ser nulo).
     * @param location La ubicación donde ocurre el gol.
     */
    void execute(Player player, Location location);
}
