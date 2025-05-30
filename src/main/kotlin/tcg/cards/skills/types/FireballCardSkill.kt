package tcg.cards.skills.types

import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.display.HeadDisplayGenerator.spawnDisplayHead
import tcg.util.text.Description
import tcg.util.text.SkillDescription

class FireballCardSkill(
    plugin: PandoDungeons,
    rarity: CardRarity
) : CardSkill(plugin, rarity) {


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
        val maxTicks = 40

       object : BukkitRunnable(){
            override fun run() {
                if (!display.isValid) {
                    this.cancel()
                    return
                }

                if (ticksLived >= maxTicks) {
                    display.remove()
                    this.cancel()
                    return
                }

                val nextLocation = display.location.clone().add(velocity)
                val blockHit = !nextLocation.block.isPassable && !nextLocation.block.type.isAir

                checkHit(world, display, player, blockHit, this)

                display.teleport(nextLocation)
                world.spawnParticle(Particle.FLAME, display.location, 2, 0.05, 0.05, 0.05, 0.01)
                world.spawnParticle(Particle.SMOKE, display.location, 1, 0.05, 0.05, 0.05, 0.01)

                ticksLived++
            }

        }.runTaskTimer(plugin,0L,1L)

    }


    private fun checkHit(
        world: World,
        display: ItemDisplay,
        player: Player,
        blockHit: Boolean,
        task: BukkitRunnable
    ) {
        val hitEntities = world.getNearbyEntities(display.boundingBox.expand(0.7))
            .filterIsInstance<LivingEntity>()
            .filter { it != player }

        if (hitEntities.isNotEmpty() || blockHit) {
            world.spawnParticle(Particle.EXPLOSION, display.location, 8, 0.3, 0.3, 0.3, 0.1)
            world.playSound(display.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.2f)

            hitEntities.forEach {
                it.fireTicks = 60
                it.damage(4.0, player)
            }

            display.remove()
            task.cancel()
        }
    }

    override fun setSkillType(): String = "fireballSkill"

    override fun shouldWaitForCondition(): Boolean = false

    override fun setDescription(): Description {
        return SkillDescription("Lanza una bola de fuego ardiente que explota al impactar.", rarity)
    }
}
