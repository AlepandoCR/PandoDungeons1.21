package controlledEntities.modeled.pets.types.miner;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.generator.assets.ItemModel;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import controlledEntities.modeled.ModelBuilder;
import controlledEntities.modeled.pets.Pet;
import controlledEntities.modeled.pets.goals.FollowOwnerGoal;
import controlledEntities.modeled.pets.types.miner.goals.CollectAndDeliverMineralsGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import org.apache.maven.model.Model;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.List;


public class MinerPet extends Pet {
    public MinerPet(Player owner, PandoDungeons plugin) {
        super(owner, plugin,false, "minero");
        this.goals = setGoals();
        applyGoals(getMob());
    }

    @Override
    public ModeledEntity setModeledEntity() {
        ModeledEntity modeled = builder.apply().getModeledEntity();

        modeled.save();

        modeled.setBaseEntityVisible(false);

        modeled.getBase().setVisible(false);

        modeled.getBase().save();

        return modeled;
    }

    @Override
    public List<Goal> setGoals() {
        // Asegúrate de que 'mob' no sea null y tenga un handle válido
        if (mob != null && mob instanceof CraftMob) {
            return List.of(
                    new CollectAndDeliverMineralsGoal(((CraftMob) mob).getHandle(), ((CraftPlayer) owner).getHandle(), 1.4f, 15,3.5f)
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
    public Hitbox setHitbox() {
        return new Hitbox(0.5,0.3,0.7,0.2);
    }
}
