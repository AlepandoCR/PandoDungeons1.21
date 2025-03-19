package controlledEntities.modeled;

import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.generator.assets.ItemModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import controlledEntities.ControlledEntity;
import org.bukkit.Location;
import pandodungeons.pandodungeons.PandoDungeons;

public abstract class ModeledControlled extends ControlledEntity {

    protected ModeledEntity modeledEntity;
    protected final Hitbox hitbox;
    protected final String modelName;
    protected ModelBuilder builder;

    public ModeledControlled(PandoDungeons plugin, Location spawnLoc, boolean applyGoals, String modelName) {
        super(plugin,spawnLoc,applyGoals);
        this.modelName = modelName;
        this.hitbox = setHitbox();
        this.builder = new ModelBuilder(modelName,this.mob).setHitbox(hitbox);
        this.modeledEntity = setModeledEntity();
    }

    public abstract ModeledEntity setModeledEntity();

    public abstract Hitbox setHitbox();

    public ModeledEntity getModeledEntity() {
        return modeledEntity;
    }

    @Override
    public void extraRestore(){
        modeledEntity.destroy();

        builder = new ModelBuilder(modelName,mob).setHitbox(hitbox);

        modeledEntity = builder.apply().getModeledEntity();

        modeledEntity.getBase().setVisible(false);

        modeledEntity.getBase().save();
    }
}
