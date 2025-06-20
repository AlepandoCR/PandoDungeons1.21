package tcg.cards.skills.engine

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.util.text.Description
import java.util.Optional

abstract class CardSkill(
    protected val plugin: PandoDungeons,
    protected val rarity: CardRarity
) {
    private var skillType = initSkillType()
    private var dummyPlayer: Optional<Player> = Optional.empty()
    internal val description: Description by lazy { setDescription() }
    private var listener: Optional<Listener> = Optional.empty()


    fun trigger(initiator: Player) {
        dummyPlayer = Optional.of(initiator)

        if (shouldWaitForCondition()) waitForCondition()
        bootListener()
        executeIfCondition()
    }

    fun trigger() {
        if (shouldWaitForCondition()) waitForCondition()
        bootListener()
        executeIfCondition()
    }

    private fun bootListener() {
        if (listener.isPresent) return

        val instance = listener()
        if (instance != null) {
            listener = Optional.of(instance)
            plugin.server.pluginManager.registerEvents(instance, plugin)

            object : BukkitRunnable() {
                override fun run() {
                    HandlerList.unregisterAll(instance)
                    listener = Optional.empty()
                }
            }.runTaskLater(plugin, listenerPeriod())
        }
    }

    private fun executeIfCondition(): Boolean {
        if (startCondition()) {
            task()
            return true
        }
        return false
    }

    protected abstract fun startCondition(): Boolean
    protected abstract fun task()
    protected abstract fun setSkillType(): String
    protected abstract fun shouldWaitForCondition(): Boolean
    protected abstract fun setDescription(): Description

    protected open fun listener(): Listener? = null
    protected open fun listenerPeriod(): Long = 20L

    protected open fun maxWaitTime(): Long = 20L * 5L

    fun getType(): SkillType = skillType

    private fun waitForCondition() {
        object : BukkitRunnable() {
            override fun run() {
                if (executeIfCondition()) cancel()
            }
        }.runTaskTimer(plugin, 0L, maxWaitTime())
    }

    private fun initSkillType(): SkillType {
        return SkillType { setSkillType() }
    }

    protected fun getDummyPlayer(): Player = dummyPlayer.get()

    fun hasPlayer(): Boolean = dummyPlayer.isPresent
}
