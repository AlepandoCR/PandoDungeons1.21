package controlledEntities.modeled.pets.types.sakura;

import controlledEntities.modeled.pets.Pet;
import controlledEntities.modeled.pets.PetType;
import controlledEntities.modeled.pets.goals.FollowOwnerGoal;
import controlledEntities.modeled.pets.types.sakura.goals.PastureAnimalsGoal;
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

public class SakuraPet extends Pet {
    public SakuraPet(Player owner, PandoDungeons plugin) {
        super(owner, plugin,false, "sakura");
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
       
        if (mob != null && mob instanceof CraftMob) {
            return List.of(
                    new FollowOwnerGoal(((CraftMob) mob).getHandle(), ((CraftPlayer) owner).getHandle(), 1.4,0.6, 3, 0.5f),
                    new PastureAnimalsGoal(((CraftMob) mob).getHandle(), ((CraftPlayer) owner).getHandle(),1.7)

            );
        }
        return List.of(); 
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
        return PetType.SAKURA;
    }

    @Override
    public String setPermission() {
        return "mascota.sakura";
    }

    @Override
    public String getDisplayValue() {
        return "db61521259c6c3402f9cae3b6867ddb481fd7a834674f26bf4fcf11c978e0051";
    }

}
