package pandoClass.golem.limbs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.golem.limb.Limb;
import pandodungeons.PandoDungeons;

public class Chest extends Limb {
    private Display lowerTorso, middleTorso, upperTorso, neck;
    private final double segmentHeight = 1.0;

    public Chest(Location baseLocation, PandoDungeons plugin) {
        super(baseLocation, plugin);
        this.lowerTorso = createPart(baseLocation, Material.IRON_BLOCK);
        this.middleTorso = createPart(baseLocation.clone().add(0, segmentHeight, 0), Material.IRON_BLOCK);
        this.upperTorso = createPart(baseLocation.clone().add(0, segmentHeight * 2, 0), Material.IRON_BLOCK);
        this.neck = createPart(baseLocation.clone().add(0, segmentHeight * 3, 0), Material.IRON_BLOCK);
    }

    /**
     * Mueve el torso suavemente a una nueva posición y ajusta las partes
     * @param targetLocation La nueva ubicación base del torso
     */
    public void moveTo(Location targetLocation) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location currentLoc = lowerTorso.getLocation();
                Vector direction = targetLocation.toVector().subtract(currentLoc.toVector()).normalize().multiply(0.2);

                if (currentLoc.distance(targetLocation) < 0.2) {
                    lowerTorso.teleport(targetLocation);
                    updateTorsoParts();
                    cancel();
                    return;
                }

                lowerTorso.teleport(currentLoc.add(direction));
                updateTorsoParts();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Ajusta las partes del torso para que se mantengan alineadas
     */
    private void updateTorsoParts() {
        Location lowerLoc = lowerTorso.getLocation();
        Location middleLoc = lowerLoc.clone().add(0, segmentHeight, 0);
        Location upperLoc = middleLoc.clone().add(0, segmentHeight, 0);
        Location neckLoc = upperLoc.clone().add(0, segmentHeight, 0);

        middleTorso.teleport(middleLoc);
        upperTorso.teleport(upperLoc);
        neck.teleport(neckLoc);
    }

    /**
     * Inclina el torso en una dirección dada
     * @param direction Vector de inclinación
     */
    public void tilt(Vector direction) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location upperLoc = upperTorso.getLocation();
                Location newUpperLoc = upperLoc.add(direction.clone().multiply(0.1));

                if (newUpperLoc.distance(upperLoc) > 0.5) {
                    cancel();
                    return;
                }

                upperTorso.teleport(newUpperLoc);
                updateTorsoParts();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void move() {
        updateTorsoParts();
    }
}
