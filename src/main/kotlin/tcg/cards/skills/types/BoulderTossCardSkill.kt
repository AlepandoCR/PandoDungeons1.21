package tcg.cards.skills.types

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.LivingEntity
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.display.HeadDisplayGenerator.spawnDisplayHead
import tcg.util.text.Description
import tcg.util.text.SkillDescription

class BoulderTossCardSkill(
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
            "f8d818e65ee36cdcab6e315f499d3f5dde2323bf2df56f1e914089cebefa37f6",
            startLoc,
            2.5
        )

        val velocity = player.location.direction.normalize().multiply(0.8)
        var ticksLived = 0
        var bounces = 0

        object : BukkitRunnable() {
            override fun run() {
                if (!display.isValid) {
                    cancel()
                    return
                }

                if (ticksLived >= 60 || bounces > 5 || velocity.length() < 0.2) {
                    explode(display.location, world, player)
                    display.remove()
                    cancel()
                    return
                }

                val currentLoc = display.location
                val nextLoc = currentLoc.clone().add(velocity)

                val block = nextLoc.block
                val blockHit = !block.isPassable

                val entityHit = world.getNearbyEntities(nextLoc, 1.3, 1.3, 1.3)
                    .filterIsInstance<LivingEntity>()
                    .firstOrNull { it != player }

                if (entityHit != null) {
                    entityHit.damage(6.0, player)
                    explode(entityHit.location, world, player)
                    display.remove()
                    cancel()
                    return
                }

                if (blockHit) {
                    velocity.multiply(0.6)
                    velocity.setY(velocity.y * -0.8)
                    bounces++
                    world.playSound(currentLoc, Sound.BLOCK_STONE_HIT, 1f, 0.8f)
                    world.spawnParticle(Particle.BLOCK_CRUMBLE, currentLoc, 20, block.blockData)
                }

                display.teleport(currentLoc.add(velocity))
                velocity.setY(velocity.y - 0.04)
                ticksLived++
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }


    override fun setSkillType(): String {
        return "boulder_toss"
    }

    override fun shouldWaitForCondition(): Boolean {
        return false
    }

    override fun setDescription(): Description {
        return SkillDescription("Lanza una roca grande", rarity)
    }

    private fun explode(loc: Location, world: World, caster: LivingEntity) {

        world.spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1)
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)

        val shockwaveRadius = 5.0
        val damageRadius = 2.5

        for (entity in world.getNearbyEntities(loc, shockwaveRadius, shockwaveRadius, shockwaveRadius)) {
            if (entity is LivingEntity && entity != caster) {
                val direction = entity.location.clone().subtract(loc).toVector().normalize()
                val distance = entity.location.distance(loc)

                val strength = (1.0 - (distance / shockwaveRadius)).coerceAtLeast(0.2)
                if (direction.isFinite() && strength.isFinite()) {
                    val velocity = direction.multiply(strength * 0.8).setY(0.4 + strength * 0.5)
                    if (velocity.isFinite()) {
                        entity.velocity = velocity
                    }
                }


                if (distance <= damageRadius) {
                    entity.damage(4.0, caster)
                }
            }
        }
    }

    private fun Vector.isFinite(): Boolean {
        return x.isFinite() && y.isFinite() && z.isFinite()
    }


}
