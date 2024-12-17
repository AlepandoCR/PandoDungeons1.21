package pandoToros.game.modes.cosmetic.base;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pandoToros.game.modes.cosmetic.CosmeticAction;

public class ExplosionAction implements CosmeticAction {

    private final float power;

    public ExplosionAction(float power) {
        this.power = power;
    }

    @Override
    public void execute(Player player, Location location) {
        location.getWorld().createExplosion(location, power, false, false);
    }
}
