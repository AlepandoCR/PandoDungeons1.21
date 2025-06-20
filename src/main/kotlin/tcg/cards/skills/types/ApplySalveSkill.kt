package tcg.cards.skills.types

import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.scheduler.BukkitRunnable
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.text.Description
import tcg.util.text.SkillDescription
import kotlin.math.min

class ApplySalveSkill(
    plugin: PandoDungeons,
    rarity: CardRarity
) : CardSkill(plugin, rarity) {

    override fun startCondition(): Boolean = true

    override fun shouldWaitForCondition(): Boolean = false

    override fun setSkillType(): String = "apply_salve"

    override fun setDescription(): Description {
        return SkillDescription("Consumes una hierba que te cura", rarity)
    }

    override fun task() {
        if (!hasPlayer()) return
        val player = getDummyPlayer()

        val maxHealth = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0

        val healPerTick = maxHealth * 0.15
        val durationTicks = 20L * 3
        var ticksProcessed = 0

        player.world.playSound(player.location, Sound.ITEM_HONEY_BOTTLE_DRINK, 0.8f, 0.4f)

        object : BukkitRunnable() {
            override fun run() {
                if (!player.isValid || player.isDead) {
                    this.cancel()
                    return
                }

                if (ticksProcessed >= durationTicks) {
                    this.cancel()
                    return
                }

                player.health = min(player.health + healPerTick, maxHealth)
                
                player.world.spawnParticle(Particle.HEART, player.location.add(0.0, 1.0, 0.0), 1, 0.2, 0.2, 0.2, 0.01)

                ticksProcessed++
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }
}
