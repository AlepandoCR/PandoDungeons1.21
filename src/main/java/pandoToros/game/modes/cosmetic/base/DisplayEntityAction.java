package pandoToros.game.modes.cosmetic.base;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pandoToros.game.modes.cosmetic.CosmeticAction;
import pandodungeons.PandoDungeons;

public class DisplayEntityAction implements CosmeticAction {

    private final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    private final String text;

    public DisplayEntityAction(String text) {
        this.text = text;
    }

    @Override
    public void execute(Player player, Location location) {
        BlockDisplay display = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
        display.setBillboard(Display.Billboard.CENTER);
        display.setCustomNameVisible(false);

        // Opcional: Eliminar después de un tiempo
        Bukkit.getScheduler().runTaskLater(plugin, display::remove, 100L);
    }
}
