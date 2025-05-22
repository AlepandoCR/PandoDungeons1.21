package controlledEntities.modeled;

import controlledEntities.ControlledEntity;
import kr.toxicity.model.api.tracker.EntityTracker;
import model.builder.ModelBuilder;
import org.bukkit.Location;
import pandodungeons.PandoDungeons;

public abstract class ModeledControlled extends ControlledEntity {

    protected EntityTracker modeledEntity;
    protected final String modelName;
    protected ModelBuilder builder;

    public ModeledControlled(PandoDungeons plugin, Location spawnLoc, boolean applyGoals, String modelName) {
        super(plugin,spawnLoc,applyGoals);
        this.modelName = modelName;
        this.builder = new ModelBuilder(modelName,this.mob);
        this.modeledEntity = setModeledEntity();
    }

    public abstract EntityTracker setModeledEntity();


    public String getModelName() {
        return modelName;
    }

    public EntityTracker getModeledEntity() {
        return modeledEntity;
    }

    @Override
    public void extraRestore(){
        modeledEntity.despawn();

        builder = new ModelBuilder(modelName,mob);

        modeledEntity = builder.apply();

        modeledEntity.forRemoval(false);

        modeledEntity.updateBaseEntity();
    }

    public void respawn(Location location) {
        // Destruir el modelo si existe
        if (modeledEntity != null) {
            modeledEntity.despawn();
        }

        // Eliminar la entidad si existe
        if (mob != null) {
            mob.remove();
        }

        this.spawnLoc = location;
        // Volver a crear la entidad
        this.mob = setEntity();
        applyGoalsMain();

        // Volver a crear el modelo
        this.builder = new ModelBuilder(modelName, mob);
        this.modeledEntity = builder.apply();

        modeledEntity.updateBaseEntity();
    }
}
