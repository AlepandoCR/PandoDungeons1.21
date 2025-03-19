package controlledEntities.modeled;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.entity.Mob;

public class ModelBuilder {

    private final ModelBlueprint blueprint;
    private final ActiveModel activeModel;
    private final ModeledEntity modeledEntity;

    public ModelBuilder(String modelId, Mob entity) {
        this.modeledEntity = ModelEngineAPI.createModeledEntity(entity);

        // Obtener el modelo
        this.blueprint = ModelEngineAPI.getAPI().getModelRegistry().get(modelId);
        if (this.blueprint == null) {
            throw new IllegalArgumentException("No se encontró el modelo con ID: " + modelId);
        }

        // Crear el ActiveModel
        this.activeModel = ModelEngineAPI.createActiveModel(blueprint);
    }

    public ModelBuilder setHitbox(Hitbox hitbox) {
        this.blueprint.setMainHitbox(hitbox);
        return this;
    }

    public ActiveModel apply() {

        activeModel.save();

        modeledEntity.addModel(activeModel,true);

        return activeModel;
    }
}
