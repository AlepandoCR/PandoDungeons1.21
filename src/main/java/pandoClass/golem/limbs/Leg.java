package pandoClass.golem.limbs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.golem.limb.Limb;
import pandodungeons.PandoDungeons;

public class Leg extends Limb {
    private Display foot, knee, muslo;
    private double longitudMusloRodilla = 1.0;
    private double longitudRodillaPie = 1.0;
    private boolean isWalking = false;

    public Leg(Location baseLocation, PandoDungeons plugin) {
        super(baseLocation, plugin);
        this.muslo = createPart(baseLocation, Material.IRON_BLOCK);
        this.knee = createPart(baseLocation.clone().add(0, -longitudMusloRodilla, 0), Material.IRON_BLOCK);
        this.foot = createPart(baseLocation.clone().add(0, -longitudMusloRodilla - longitudRodillaPie, 0), Material.IRON_BLOCK);
    }

    public void moveFootTo(Location target) {
        isWalking = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                Location footLoc = foot.getLocation();
                Vector direction = target.toVector().subtract(footLoc.toVector()).normalize().multiply(0.2);

                if (footLoc.distance(target) < 0.3) {
                    foot.teleport(target);
                    isWalking = false;
                    adjustFootToGround();
                    cancel();
                    return;
                }

                foot.teleport(footLoc.add(direction));
                updateLegPositions();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void adjustFootToGround() {
        Location footLoc = foot.getLocation();
        Block blockBelow = footLoc.getBlock().getRelative(0, -1, 0);

        if (blockBelow.isEmpty()) {
            // Baja el pie hasta encontrar un bloque
            while (blockBelow.isEmpty() && footLoc.getY() > 0) {
                footLoc.subtract(0, 0.1, 0);
                blockBelow = footLoc.getBlock().getRelative(0, -1, 0);
            }
        } else {
            // Sube el pie si está dentro de un bloque
            while (!blockBelow.isEmpty()) {
                footLoc.add(0, 0.1, 0);
                blockBelow = footLoc.getBlock().getRelative(0, -1, 0);
            }
            footLoc.subtract(0, 0.1, 0); // Ajuste final para tocar el suelo
        }

        foot.teleport(footLoc);
        updateLegPositions();
    }

    private void updateLegPositions() {
        Location footLoc = foot.getLocation();
        Location kneeLoc = adjustJointPosition(muslo.getLocation(), footLoc, longitudMusloRodilla);
        Location musloLoc = adjustJointPosition(kneeLoc, footLoc, longitudRodillaPie);

        knee.teleport(kneeLoc);
        muslo.teleport(musloLoc);
    }

    private Location adjustJointPosition(Location base, Location target, double length) {
        Vector dir = target.toVector().subtract(base.toVector()).normalize().multiply(length);
        return base.clone().add(dir);
    }

    @Override
    public void move() {
        if (!isWalking) {
            adjustFootToGround(); // Asegura que el pie toque el suelo si no está en movimiento
        }
    }
}
