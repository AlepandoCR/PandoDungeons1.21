package model.builder

import kr.toxicity.model.api.BetterModel
import kr.toxicity.model.api.data.blueprint.ModelBlueprint
import kr.toxicity.model.api.data.renderer.ModelRenderer
import kr.toxicity.model.api.tracker.EntityTracker
import org.bukkit.entity.Mob

class ModelBuilder(modelId: String, entity: Mob) {
    private val blueprint: ModelBlueprint
    private val modeledEntity: EntityTracker
    private val mob: Mob
    private val renderer: ModelRenderer

    init {
        val betterModelPlugin = BetterModel.inst()

        this.mob = entity

         this.renderer = betterModelPlugin.modelManager().renderer(modelId)!!

        this.modeledEntity = renderer.create(mob)

        // Obtener el modelo
        this.blueprint = modeledEntity.renderer().parent

        modeledEntity.autoSpawn(true)
    }

    fun update(): ModelBuilder {
        modeledEntity.updateBaseEntity()
        return this
    }

    fun scale(scale: Float): ModelBuilder {
        val scaleSupplier: () -> Float = { scale }
        modeledEntity.modifier().toBuilder().scale(scaleSupplier).build()
        return this
    }

    fun scale(scaleSupplier: () -> Float): ModelBuilder {
        modeledEntity.modifier().toBuilder().scale(scaleSupplier).build()
        return this
    }

    fun shadow(boolean: Boolean): ModelBuilder{
        modeledEntity.modifier().toBuilder().shadow(boolean)
        return this
    }

    fun viewRange(float: Float): ModelBuilder{
        modeledEntity.modifier().toBuilder().viewRange(float)
        return this
    }

    fun apply(): EntityTracker {
        modeledEntity.spawnNearby(mob.location)

        modeledEntity.forRemoval(false)
        return modeledEntity
    }
}