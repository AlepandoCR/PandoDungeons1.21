package tcg.cards.skills.engine

import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.util.text.Description
import java.util.Optional

abstract class CardSkill(
    protected val plugin: PandoDungeons,
    protected val rarity: CardRarity
) {
    private var maxWaitTime = 20L * 5L // 5sg
    private var skillType = initSkillType()
    private var dummyPlayer: Optional<Player> = Optional.empty()
    internal val description: Description by lazy { setDescription() }

    fun trigger(initiator: Player)
    {
        val notNullPlayer: Player = initiator
        dummyPlayer = Optional.of(notNullPlayer)

        if(shouldWaitForCondition()) waitForCondition()

        executeIfCondition()
    }

    fun trigger()
    {
        if(shouldWaitForCondition()) waitForCondition()

        executeIfCondition()
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

    fun getType(): SkillType {
        return skillType
    }

    private fun waitForCondition() {
        object : BukkitRunnable() {
            override fun run() {
                if (executeIfCondition()) {
                    cancel()
                    return
                }
            }
        }.runTaskTimer(plugin, 0L, maxWaitTime)
    }

    private fun initSkillType(): SkillType {
        val aux = SkillType { setSkillType() }
        return aux
    }

    protected fun getDummyPlayer(): Player{
        return dummyPlayer.get()
    }

    fun hasPlayer():Boolean{
        return dummyPlayer.isPresent
    }
}