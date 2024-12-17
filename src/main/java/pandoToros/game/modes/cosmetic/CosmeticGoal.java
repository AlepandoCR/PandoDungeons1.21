package pandoToros.game.modes.cosmetic;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class CosmeticGoal {

    private final List<CosmeticAction> actions;
    private final Plugin plugin;

    public CosmeticGoal(Plugin plugin) {
        this.plugin = plugin;
        this.actions = new ArrayList<>();
    }

    /**
     * Agrega una acción cosmética que se ejecutará cuando se active el gol.
     *
     * @param action Acción cosmética a agregar.
     */
    public void addAction(CosmeticAction action) {
        actions.add(action);
    }

    /**
     * Ejecuta todas las acciones cosméticas en la ubicación especificada.
     *
     * @param player    Jugador que marcó el gol (puede ser nulo).
     * @param location  Ubicación del gol.
     */
    public void trigger(Player player, Location location) {
        for (CosmeticAction action : actions) {
            try {
                action.execute(player, location);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to execute cosmetic action: " + e.getMessage());
            }
        }
    }
}
