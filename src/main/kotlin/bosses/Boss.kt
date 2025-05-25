package bosses

import goal.Goal
import model.builder.ModelBuilder
import org.bukkit.entity.LivingEntity

abstract class Boss(lvl: Int) {

    private val entity: LivingEntity by lazy { setEntity() }
    private val level: Int = lvl
    private val goals: List<Goal> by lazy { setGoals() }
    private val modelBuilder: ModelBuilder by lazy { setModel() }

    abstract fun setEntity(): LivingEntity

    abstract fun setGoals(): List<Goal>

    abstract fun setModel(): ModelBuilder

}