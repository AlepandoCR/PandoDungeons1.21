package tcg.cards.skills.types

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.LivingEntity
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.text.Description
import tcg.util.text.SkillDescription

class ChainLightningCardSkill(
    plugin: PandoDungeons,
    rarity: CardRarity
) : CardSkill(plugin, rarity) {

    override fun startCondition(): Boolean = true

    override fun task() {
        if (!hasPlayer()) return
        val player = getDummyPlayer()
        val world = player.world

        val maxTargets = 4
        var currentDamage = 8.0

        val initialTarget = player.world
            .getNearbyLivingEntities(player.location, 15.0, 15.0, 15.0)
            .firstOrNull { it != player } ?: return

        val targets = mutableListOf<LivingEntity>()
        targets.add(initialTarget)

        var currentSource: LivingEntity = initialTarget

        while (targets.size < maxTargets) {
            val next = player.world
                .getNearbyLivingEntities(currentSource.location, 8.0, 8.0, 8.0)
                .firstOrNull { it != player && it !in targets } ?: break

            targets.add(next)
            currentSource = next
        }

        var source = player as LivingEntity
        for (target in targets) {
            spawnLightningEffect(world, source.location, target.location)
            target.damage(currentDamage, player)
            world.playSound(target.location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.4f, 1.5f)
            currentDamage *= 0.75
            source = target
        }
    }


    override fun setSkillType(): String {
        return "chain_lightning"
    }

    override fun shouldWaitForCondition(): Boolean {
        return true
    }

    override fun setDescription(): Description {
        return SkillDescription("Lanza un rallo que rebota entre los cercanos", rarity)
    }

    private fun spawnLightningEffect(world: World, from: Location, to: Location) {
        val steps = 10
        val direction = to.clone().subtract(from).toVector().multiply(1.0 / steps)
        val loc = from.clone()

        repeat(steps) {
            loc.add(direction)
            world.spawnParticle(Particle.ELECTRIC_SPARK, loc, 2, 0.1, 0.1, 0.1, 0.01)
        }
    }
}
