package tcg.cards.skills.types

import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.scheduler.BukkitRunnable
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.text.Description
import tcg.util.text.SkillDescription

class ChargeSkill(
    plugin: PandoDungeons,
    rarity: CardRarity
) : CardSkill(plugin, rarity) {

    override fun startCondition(): Boolean {
        return true
    }

    override fun task() {
        if (!hasPlayer()) return
        val player = getDummyPlayer()
        val world = player.world

        val dashVector = player.location.direction.normalize().multiply(1.5)
        val durationTicks = 10
        var ticksPassed = 0

        object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    cancel()
                    return
                }

                player.velocity = dashVector
                player.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.7f, 1.2f)
                player.spawnParticle(Particle.CLOUD, player.location, 6, 0.2, 0.2, 0.2, 0.01)

                val nearby = world.getNearbyEntities(player.boundingBox.expand(1.2)).filterIsInstance<LivingEntity>()
                    .filter { it != player && it !is org.bukkit.entity.ArmorStand }

                for (entity in nearby) {
                    val pushVec = player.location.direction.normalize().multiply(1.5)
                    entity.velocity = pushVec
                    entity.damage(2.0, player)
                    entity.world.playSound(entity.location, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 0.6f, 1.5f)
                }

                ticksPassed++
                if (ticksPassed >= durationTicks) {
                    Bukkit.getScheduler().cancelTask(taskId)
                }
            }
        }.runTaskTimer(plugin,0L,1L)
    }



    override fun setSkillType(): String {
        return "chargeSkill"
    }

    override fun shouldWaitForCondition(): Boolean {
        return false
    }

    override fun setDescription(): Description {
        val r = SkillDescription(
            "Cargas hacia la dirección que estas viendo.",
            rarity
        )
        return r
    }
}
