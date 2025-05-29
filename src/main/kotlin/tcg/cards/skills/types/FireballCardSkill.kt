package tcg.cards.skills.types

import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.LivingEntity
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.display.HeadDisplayGenerator.spawnDisplayHead
import tcg.util.text.Description
import tcg.util.text.SkillDescription
import kotlin.properties.Delegates

class FireballCardSkill(
    plugin: PandoDungeons,
    rarity: CardRarity
) : CardSkill(plugin, rarity) {

    private var taskId by Delegates.notNull<Int>()

    override fun startCondition(): Boolean = true

    override fun task() {
        if (!hasPlayer()) return
        val player = getDummyPlayer()
        val world = player.world

        val startLoc = player.eyeLocation.clone().add(player.location.direction.multiply(1.5))
        val display = spawnDisplayHead(
            "4c3c2f2f9477865253112c5a1b6c3b1236ae23461e596af7a5982d5e2b85de15",
            startLoc
        )

        val velocity = player.location.direction.normalize().multiply(0.6)
        var ticksLived = 0
        val maxTicks = 80L

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, Runnable {
            if (!display.isValid) return@Runnable

            val nextLocation = display.location.clone().add(velocity)

            val blockAtNext = nextLocation.block
            val blockHit = !blockAtNext.isPassable && !blockAtNext.type.isAir

            display.teleport(nextLocation)

            world.spawnParticle(Particle.FLAME, display.location, 2, 0.05, 0.05, 0.05, 0.01)
            world.spawnParticle(Particle.SMOKE, display.location, 1, 0.05, 0.05, 0.05, 0.01)

            ticksLived++

            val hitEntities = world.getNearbyEntities(display.boundingBox.expand(0.7))
                .filterIsInstance<LivingEntity>()
                .filter { it != player }

            if (hitEntities.isNotEmpty() || ticksLived >= maxTicks || blockHit) {
                world.spawnParticle(Particle.EXPLOSION, display.location, 8, 0.3, 0.3, 0.3, 0.1)
                world.playSound(display.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.2f)

                hitEntities.forEach {
                    it.fireTicks = 60
                    it.damage(4.0, player)
                }

                for (itemDisplay in display.getNearbyEntities(3.0, 3.0, 3.0)
                    .filterIsInstance<ItemDisplay>()
                    .filter { it.scoreboardTags.contains("pando.display") }) {
                    itemDisplay.remove()
                }

                display.remove()
                Bukkit.getScheduler().cancelTask(taskId)
            }

        }, 0L, 1L)
    }

    override fun setSkillType(): String = "fireballSkill"

    override fun shouldWaitForCondition(): Boolean = false

    override fun setDescription(): Description {
        return SkillDescription("Lanza una bola de fuego ardiente que explota al impactar.", rarity)
    }
}
