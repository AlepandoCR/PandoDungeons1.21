package tcg.cards.skills.types

import org.bukkit.*
import org.bukkit.entity.LivingEntity
import org.bukkit.scheduler.BukkitRunnable
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.display.HeadDisplayGenerator.spawnDisplayHead
import tcg.util.text.Description
import tcg.util.text.SkillDescription
import kotlin.math.cos
import kotlin.math.sin

class GravitonPullCardSkill(
    plugin: PandoDungeons,
    rarity: CardRarity
) : CardSkill(plugin, rarity) {

    override fun startCondition(): Boolean = true

    override fun task() {
        if (!hasPlayer()) return
        val player = getDummyPlayer()
        val world = player.world
        val direction = player.location.direction.normalize()
        val startLoc = player.eyeLocation.clone().add(direction.multiply(1.2))

        val blackHole = spawnDisplayHead(
            "6e451a4365c49b42d7cd4e08f6ed7be6612ee4d79ceee1c210b63d06c0514b9c",
            startLoc
        )

        val velocity = direction.multiply(0.25)
        var ticks = 0
        val maxTicks = 100

        object : BukkitRunnable() {
            override fun run() {
                if (!blackHole.isValid) {
                    cancel()
                    return
                }

                if (ticks++ >= maxTicks) {
                    collapse(world, blackHole.location)
                    blackHole.remove()
                    cancel()
                    return
                }

                for (i in 0..6) {
                    val angle = ticks * 0.2 + i
                    val radius = 1.5 - (i * 0.2)
                    val x = cos(angle) * radius
                    val z = sin(angle) * radius
                    val particleLoc = blackHole.location.clone().add(x, 0.2 + i * 0.1, z)
                    world.spawnParticle(Particle.PORTAL, particleLoc, 0, -x / 4, -0.1, -z / 4, 0.1)
                }

                val affected = blackHole.getNearbyEntities(5.0,5.0,5.0).filterIsInstance<LivingEntity>().filter { livingEntity -> livingEntity != player }

                affected.forEach {
                    val pull = blackHole.location.toVector().subtract(it.location.toVector()).normalize().multiply(0.2)
                    it.velocity = it.velocity.add(pull)
                }

                val nextLoc = blackHole.location.clone().add(velocity)
                blackHole.teleport(nextLoc)
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    override fun setSkillType(): String = "black_hole"

    override fun shouldWaitForCondition(): Boolean = false

    override fun setDescription(): Description = SkillDescription("Atrae y colapsa", rarity)

    private fun collapse(world: World, location: Location) {
        world.spawnParticle(Particle.EXPLOSION, location, 1)
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.2f)

        val affected = location.getNearbyLivingEntities(5.0)
        affected.forEach {
            it.damage(6.0)
        }
    }
}
