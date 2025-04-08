package controlledEntities.modeled.pets;

import com.ticxo.modelengine.api.ModelEngineAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PetNameTagHandler {
    private static final List<PetNameTagHandler> ACTIVE_HANDLERS = new ArrayList<>();

    private final ArmorStand nameTag;
    private final Pet pet;

    public PetNameTagHandler(PandoDungeons plugin, Pet pet, String name) {
        this.pet = pet;

        Location spawnLocation = pet.getMob().getLocation();
        this.nameTag = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);

        nameTag.setCustomName(name);
        nameTag.setCustomNameVisible(true);
        nameTag.setInvisible(true);
        nameTag.setInvulnerable(true);
        nameTag.setMarker(true);
        nameTag.setGravity(false);
        nameTag.setSmall(true);

        ACTIVE_HANDLERS.add(this);
    }

    public static void startGlobalUpdater(PandoDungeons plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<PetNameTagHandler> iterator = ACTIVE_HANDLERS.iterator();
                while (iterator.hasNext()) {
                    PetNameTagHandler handler = iterator.next();

                    if (!handler.isValid()) {
                        handler.remove();
                        iterator.remove();
                        continue;
                    }

                    handler.updateNameTagPosition();
                }
            }
        }.runTaskTimer(plugin, 0L, 3L); // Actualiza cada 3 ticks
    }

    public ArmorStand getNameTag() {
        return nameTag;
    }

    public void remove() {
        if (!nameTag.isDead()) nameTag.remove();
    }

    private boolean isValid() {
        return pet.getModeledEntity().getBase().isAlive() &&
                pet.getMob().isValid() &&
                !nameTag.isDead();
    }

    private void updateNameTagPosition() {
        Location loc = pet.getModeledEntity().getBase().getLocation();
        nameTag.teleport(loc.add(0, 0.5, 0));
    }
}
