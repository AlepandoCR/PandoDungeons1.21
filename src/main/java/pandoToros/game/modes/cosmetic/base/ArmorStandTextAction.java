package pandoToros.game.modes.cosmetic.base;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pandoToros.game.modes.cosmetic.CosmeticAction;
import pandodungeons.PandoDungeons;

public class ArmorStandTextAction implements CosmeticAction {

    PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    private final String text;

    public ArmorStandTextAction(String text) {
        this.text = text;
    }

    @Override
    public void execute(Player player, Location location) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setCustomName(text);
        armorStand.setCustomNameVisible(true);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);

        // Elimina el ArmorStand después de 5 segundos
        Bukkit.getScheduler().runTaskLater(plugin, armorStand::remove, 100L);
    }
}
