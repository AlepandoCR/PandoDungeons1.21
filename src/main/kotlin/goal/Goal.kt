package goal

import net.minecraft.world.entity.ai.goal.Goal

abstract class Goal : Goal() {

    override fun tick() {
        task()
    }

    override fun stop() {
        end()
    }

    override fun start() {
        init()
    }

    override fun canUse(): Boolean {
        return startCondition()
    }

    override fun canContinueToUse(): Boolean {
        return persistentCondition()
    }

    abstract fun startCondition() : Boolean

    abstract fun persistentCondition() : Boolean

    abstract fun init()

    abstract fun end()

    abstract fun task()
}