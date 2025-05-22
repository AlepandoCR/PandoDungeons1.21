package controlledEntities.modeled.pets.types.jojo;

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

public class JojoPet extends Pet {
    public JojoPet(Player owner, PandoDungeons plugin) {
        super(owner, plugin,false, "yoyo");
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
            return null; // Evita errores si el mundo no está cargado
        }

        PolarBear bear = (PolarBear) world.spawnEntity(spawnLocation, EntityType.POLAR_BEAR); // Oso polar porque es facil de manejar

        bear.setPersistent(true); // Para que no desaparezca
        bear.setSilent(true); // Sin sonidos


        return bear;
    }

    @Override
    public PetType setType() {
        return PetType.JOJO;
    }

    @Override
    public String setPermission() {
        return "mascota.jojo";
    }

    @Override
    public String getDisplayValue() {
        return "a60e246bdfd263e545a90913e6f00e9b4156f1a8fa8711b439c124f30f3945c4";
    }

}
