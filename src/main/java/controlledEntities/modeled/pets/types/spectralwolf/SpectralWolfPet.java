package controlledEntities.modeled.pets.types.spectralwolf;

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

public class SpectralWolfPet extends Pet {
    public SpectralWolfPet(Player owner, PandoDungeons plugin) {
        super(owner, plugin,false, "spectralwolf");
        this.goals = setGoals();
        applyGoals(getMob());


        // Animations : idle | walk | walk2 |  attack | jump | death | warning
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

        if (mob != null && mob instanceof CraftMob) {
            return List.of(
                    new FollowOwnerGoal(((CraftMob) mob).getHandle(), ((CraftPlayer) owner).getHandle(), 1.4,0.6, 3, 0.5f)
            );
        }
        return List.of();
    }


    @Override
    public Mob setEntity() {
        Location spawnLocation = spawnLoc;
        World world = spawnLocation.getWorld();

        if (world == null) {
            return null;
        }

        PolarBear bear = (PolarBear) world.spawnEntity(spawnLocation, EntityType.POLAR_BEAR);

        bear.setPersistent(true);
        bear.setSilent(true);


        return bear;
    }

    @Override
    public PetType setType() {
        return PetType.LOBOESPECTRAL;
    }

    @Override
    public String setPermission() {
        return "mascota.spectralwolf";
    }

    @Override
    public String getDisplayValue() {
        return "c36802f6b6233a027597add8bfc1dd8f006270db17eba42d46a9964da400c934";
    }

}
