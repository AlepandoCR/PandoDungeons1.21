package goal.builders

import goal.BuildableGoal

class SimpleGoalBuilder {

    private val goal: BuildableGoal = BuildableGoal()

    fun task(action: () -> Unit): SimpleGoalBuilder{
        goal.onTask(action)
        return this
    }

    fun init(action: () -> Unit): SimpleGoalBuilder{
        goal.onInit(action)
        return this
    }

    fun end(action: () -> Unit): SimpleGoalBuilder{
        goal.onEnd(action)
        return this
    }

    fun startCondition(condition: () -> Boolean): SimpleGoalBuilder{
        goal.startCondition(condition)
        return this
    }

    fun persistentCondition(condition: () -> Boolean): SimpleGoalBuilder{
        goal.persistentCondition(condition)
        return this
    }

    fun checkPersistent(): Boolean{
        return goal.persistentCondition()
    }

    fun checkStart(): Boolean{
        return goal.startCondition()
    }

    fun build(): BuildableGoal{
        return goal
    }
}