package pandoClass.golem.limb;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public abstract class Limb {
    protected List<Display> parts = new ArrayList<>();
    protected Location baseLocation;
    protected final PandoDungeons plugin;

    public Limb(Location baseLocation, PandoDungeons plugin) {
        this.baseLocation = baseLocation;
        this.plugin = plugin;
    }

    protected Display createPart(Location loc, Material material) {
        Display display = (Display) loc.getWorld().spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        // Configuración del display aquí (material, tamaño, etc.)
        return display;
    }

    public abstract void move();
}
