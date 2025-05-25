package goal

class BuildableGoal : Goal() {

    private var init: (() -> Unit)? = null
    private var end: (() -> Unit)? = null
    private var task: (() -> Unit)? = null
    private var startConditionSupplier: (() -> Boolean)? = null
    private var persistentConditionSupplier: (() -> Boolean)? = null

    override fun canUse(): Boolean {
        return startConditionSupplier?.invoke() ?: false
    }

    override fun canContinueToUse(): Boolean {
        return persistentConditionSupplier?.invoke() ?: false
    }

    override fun startCondition(): Boolean {
        return canUse()
    }

    override fun persistentCondition(): Boolean {
        return canContinueToUse()
    }

    override fun init() {
        init?.invoke()
    }

    override fun end() {
        end?.invoke()
    }

    override fun task() {
        task?.invoke()
    }

    fun startCondition(condition: () -> Boolean) {
        startConditionSupplier = condition
    }

    fun persistentCondition(condition: () -> Boolean) {
        persistentConditionSupplier = condition
    }

    fun onInit(action: () -> Unit) {
        init = action
    }

    fun onEnd(action: () -> Unit) {
        end = action
    }

    fun onTask(action: () -> Unit) {
        task = action
    }
}
