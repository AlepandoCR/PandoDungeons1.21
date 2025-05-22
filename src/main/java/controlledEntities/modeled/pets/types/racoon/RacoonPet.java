package controlledEntities.modeled.pets.types.racoon;

import controlledEntities.modeled.pets.Pet;
import controlledEntities.modeled.pets.PetType;
import controlledEntities.modeled.pets.goals.FollowOwnerGoal;
import kr.toxicity.model.api.tracker.EntityTracker;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import pandodungeons.PandoDungeons;

import java.util.List;

public class RacoonPet extends Pet {
    public RacoonPet(Player owner, PandoDungeons plugin) {
        super(owner, plugin,false, "mapacho");
        this.goals = setGoals();
        applyGoals(getMob());
    }

    @Override
    public EntityTracker setModeledEntity() {
        EntityTracker modeled = builder.apply();

        modeled.refresh();

        modeled.updateBaseEntity();

        return modeled;
    }

    @Override
    public List<Goal> setGoals() {
        // Asegúrate de que 'mob' no sea null y tenga un handle válido
        if (mob != null && mob instanceof CraftMob) {
            return List.of(
                    new FollowOwnerGoal(((CraftMob) mob).getHandle(), ((CraftPlayer) owner).getHandle(), 1.4,0.6, 3, 0.5f)
            );
        }
        return List.of(); // Retorna una lista vacía si 'mob' es null
    }


    @Override
    public Mob setEntity() {
        Location spawnLocation = spawnLoc;
        World world = spawnLocation.getWorld();

        if (world == null) {
            return null; // Evita errores si el mundo no está cargado
        }

        PolarBear bear = (PolarBear) world.spawnEntity(spawnLocation, EntityType.POLAR_BEAR); // Oso polar porque es facil de manejar

        bear.setPersistent(true); // Para que no desaparezca
        bear.setSilent(true); // Sin sonidos


        return bear;
    }

    @Override
    public PetType setType() {
        return PetType.MAPACHE;
    }

    @Override
    public String setPermission() {
        return "mascota.mapache";
    }

    @Override
    public String getDisplayValue() {
        return "37bd204512728130b7f65ff2714b98d48735ff0288b6e9a50270002290fa58d7";
    }

}
