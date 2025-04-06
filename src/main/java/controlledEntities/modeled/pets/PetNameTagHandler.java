package controlledEntities.modeled.pets;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.mount.MountPairManager;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;

public class PetNameTagHandler {
    private final PandoDungeons plugin;
    private final ArmorStand nameTag;
    private final Pet pet;

    public PetNameTagHandler(PandoDungeons plugin, Pet pet, String name) {
        this.plugin = plugin;
        this.pet = pet;

        // Crear ArmorStand en la posición del mob
        Location spawnLocation = pet.getMob().getLocation();
        this.nameTag = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);

        // Configuración inicial del ArmorStand
        nameTag.setCustomName(name);
        nameTag.setCustomNameVisible(true);
        nameTag.setInvisible(true);
        nameTag.setInvulnerable(true);
        nameTag.setMarker(true);
        nameTag.setGravity(false);
        nameTag.setSmall(true); // Opcional: más compacto visualmente

        // Actualizar el nameTag cada tick
        new BukkitRunnable() {
            @Override
            public void run() {
                // Verificar si el pet y el nameTag siguen siendo válidos
                if(!pet.getModeledEntity().getBase().isAlive()){
                    cancel();
                    nameTag.remove();
                    return;
                }
                if (!pet.getMob().isValid() || nameTag.isDead()) {
                    nameTag.remove();
                    cancel(); // Detener la tarea si el pet o el nameTag son inválidos
                    return;
                }
                // Asegurarse de que el nameTag esté siempre en la misma ubicación que el pet
                updateNameTagPosition();

            }
        }.runTaskTimer(plugin, 0L, 1L); // Ejecutar cada tick (1L)
    }

    public ArmorStand getNameTag() {
        return nameTag;
    }

    public void remove() {
        if (!nameTag.isDead()) {
            nameTag.remove();
        }
    }

    private void updateNameTagPosition() {
        Location petLocation = pet.getModeledEntity().getBase().getLocation();
        nameTag.teleport(petLocation.add(0,0.5,0)); // Teletransportar el nameTag a la ubicación del pet
    }
}
