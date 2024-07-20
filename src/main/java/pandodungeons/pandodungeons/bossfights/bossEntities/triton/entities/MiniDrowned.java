package pandodungeons.pandodungeons.bossfights.bossEntities.triton.entities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Drowned;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MiniDrowned {
    private final List<Drowned> miniDrownedHorde = new ArrayList<>();
    private final Triton triton;

    public MiniDrowned(Triton triton) {
        this.triton = triton;
    }

    public void summonMiniDrownedHorde() {
        Location location = triton.getDrowned().getLocation();
        World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("La ubicación proporcionada no es válida");
        }

        for (int i = 0; i < 5; i++) { // Invocar 5 mini ahogados
            Drowned miniDrowned = world.spawn(location, Drowned.class, (drowned) -> {
                drowned.setMaxHealth(triton.getDrowned().getMaxHealth() /4);
                drowned.setHealth(triton.getDrowned().getMaxHealth() / 4);
                drowned.setCustomName(ChatColor.AQUA + "Mini Ahogado");
                drowned.setCustomNameVisible(true);
                drowned.addScoreboardTag("miniDrowned");
                drowned.addScoreboardTag("bossMob");
                drowned.setBaby();
                drowned.setTarget(triton.getDrowned().getTarget());
            });
            miniDrownedHorde.add(miniDrowned);
        }
    }

    public boolean areMiniDrownedAlive() {
        return miniDrownedHorde.stream().anyMatch(drowned -> !drowned.isDead());
    }

    public void removeMiniDrownedHorde() {
        for (Drowned miniDrowned : miniDrownedHorde) {
            if (miniDrowned != null && !miniDrowned.isDead()) {
                miniDrowned.remove();
            }
        }
        miniDrownedHorde.clear();
    }
}
