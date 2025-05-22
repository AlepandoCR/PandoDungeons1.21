package pandoClass.golem.limbs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.util.Vector;
import pandoClass.golem.limb.Limb;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.PandoDungeons;

public class Arm extends Limb {
    private Display mano, codo, hombro;
    private double longitudHombroCodo = 1.0;
    private double longitudCodoMano = 1.0;

    public Arm(Location baseLocation, PandoDungeons plugin) {
        super(baseLocation, plugin);
        this.hombro = createPart(baseLocation, Material.IRON_BLOCK);
        this.codo = createPart(baseLocation.clone().add(0, -longitudHombroCodo, 0), Material.IRON_BLOCK);
        this.mano = createPart(baseLocation.clone().add(0, -longitudHombroCodo - longitudCodoMano, 0), Material.IRON_BLOCK);
    }

    public void moveHandTo(Location target) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location handLoc = mano.getLocation();
                Vector direction = target.toVector().subtract(handLoc.toVector()).normalize().multiply(0.2);

                if (handLoc.distance(target) < 0.3) {
                    mano.teleport(target);
                    cancel();
                    return;
                }

                mano.teleport(handLoc.add(direction));
                updateArmPositions();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void updateArmPositions() {
        Location handLoc = mano.getLocation();
        Location codoLoc = adjustJointPosition(hombro.getLocation(), handLoc, longitudHombroCodo);
        Location hombroLoc = adjustJointPosition(codoLoc, handLoc, longitudCodoMano);

        codo.teleport(codoLoc);
        hombro.teleport(hombroLoc);
    }

    private Location adjustJointPosition(Location base, Location target, double length) {
        Vector dir = target.toVector().subtract(base.toVector()).normalize().multiply(length);
        return base.clone().add(dir);
    }

    @Override
    public void move() {
        // No se usa directamente, la lógica está en moveHandTo()
    }
}
