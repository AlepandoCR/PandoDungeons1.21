package tcg.cards.skills.types

import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.display.HeadDisplayGenerator.spawnDisplayHead
import tcg.util.text.SkillDescription

class CrystalBarrageCardSkill(
    plugin: PandoDungeons,
    rarity: CardRarity
) : CardSkill(plugin, rarity) {

    override fun startCondition(): Boolean = true

    override fun task() {
        if (!hasPlayer()) return
        val player = getDummyPlayer()
        val world = player.world

        val startLoc = player.eyeLocation.clone().add(player.location.direction.multiply(1.2))
        val baseDirection = player.location.direction.normalize()

        val hitCounts = mutableMapOf<LivingEntity, Int>()

        repeat(8) {
            val angleOffset = (Math.random() - 0.5) * 0.5
            val dir = baseDirection.clone().rotateAroundY(angleOffset)

            val display = spawnDisplayHead(
                "c8e2faf25fb4001b4ed42c8b4508bb806ee3cf7a4b13f3820ee134ebf82c128c",
                startLoc
            )

            val velocity = dir.multiply(0.8)
            val ticksMax = 30
            var ticks = 0

            object : BukkitRunnable() {
                override fun run() {
                    if (!display.isValid || ticks >= ticksMax) {
                        display.remove()
                        cancel()
                        return
                    }

                    val nextLoc = display.location.clone().add(velocity)
                    val nearby = world.getNearbyEntities(nextLoc, 0.6, 0.6, 0.6)
                        .filterIsInstance<LivingEntity>()
                        .filter { it != player }

                    if (nearby.isNotEmpty()) {
                        val hit = nearby.first()
                        hit.damage(2.5, player)
                        hit.world.spawnParticle(Particle.CRIT, hit.location, 10, 0.2, 0.2, 0.2)
                        hit.world.playSound(hit.location, Sound.BLOCK_AMETHYST_BLOCK_HIT, 0.4f, 0.1f)

                        hitCounts[hit] = (hitCounts[hit] ?: 0) + 1

                        if (hitCounts[hit]!! >= 3) {
                            hit.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 60, 0))
                        }

                        display.remove()
                        cancel()
                        return
                    }

                    display.teleport(nextLoc)
                    world.spawnParticle(Particle.END_ROD, nextLoc, 1, 0.01, 0.01, 0.01)
                    ticks++
                }
            }.runTaskTimer(plugin, 0, 1)
        }
    }

    override fun setSkillType(): String = "Bombardeo Cristal"

    override fun shouldWaitForCondition(): Boolean = false

    override fun setDescription(): SkillDescription = SkillDescription("Dispara 8 fragmentos de cristal en cono", rarity)
}
