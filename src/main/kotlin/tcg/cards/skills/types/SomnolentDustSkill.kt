package tcg.cards.skills.types

import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.text.Description
import tcg.util.text.SkillDescription

class SomnolentDustSkill(
    plugin: PandoDungeons,
    rarity: CardRarity
) : CardSkill(plugin, rarity) {

    companion object {
        const val METADATA_KEY_IS_SLEEPING = "is_sleeping_active_until"
        const val SLEEP_DURATION_TICKS = 50
    }

    override fun startCondition(): Boolean = true
    override fun shouldWaitForCondition(): Boolean = false

    override fun setSkillType(): String = "somnolent_dust"

    override fun setDescription(): Description {
        return SkillDescription("Lanza una nube adormecedora", rarity)
    }

    override fun task() {
        if (!hasPlayer()) return
        val player = getDummyPlayer()
        val world = player.world

        val startLoc = player.eyeLocation.clone().add(player.location.direction.multiply(1.0))
        val velocity = player.location.direction.normalize().multiply(0.4) // Slow projectile
        var ticksLived = 0
        val maxTicks = 40

        player.world.playSound(player.location, Sound.ENTITY_PLAYER_BREATH, 0.5f, 1.5f) // Puff sound

        object : BukkitRunnable() {
            override fun run() {
                if (ticksLived >= maxTicks) {
                    this.cancel()
                    return
                }

                val currentLocation = startLoc.clone().add(velocity.clone().multiply(ticksLived))

                world.spawnParticle(Particle.DUST, currentLocation, 5, 0.2, 0.2, 0.2, 0.0, Particle.DustOptions(Color.SILVER, 1.5f))

                // Check for collision
                val hitEntities = world.getNearbyEntities(currentLocation, 2.5, 2.5, 2.5)
                    .filterIsInstance<LivingEntity>()
                    .filter { it != player && it.uniqueId != player.uniqueId && isHostile(it) }

                if (hitEntities.isNotEmpty()) {
                    for (entity in hitEntities) {
                        applySleepEffects(entity)
                        world.playSound(entity.location, Sound.ENTITY_CAT_PURR, 1f, 0.8f) // Sleepy sound
                    }
                    this.cancel()
                    return
                }

                if (!currentLocation.block.isPassable && !currentLocation.block.type.isAir && ticksLived > 2) { // allow passing through own block initially
                    world.spawnParticle(Particle.CLOUD, currentLocation, 5, 0.2,0.2,0.2,0.01)
                    this.cancel()
                    return
                }
                ticksLived++
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    private fun applySleepEffects(entity: LivingEntity) {
        val activeUntil = System.currentTimeMillis() + (SLEEP_DURATION_TICKS / 20 * 1000)
        entity.setMetadata(METADATA_KEY_IS_SLEEPING, FixedMetadataValue(plugin, activeUntil))

        entity.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, SLEEP_DURATION_TICKS, 6))
        entity.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, SLEEP_DURATION_TICKS, 0))
        entity.addPotionEffect(PotionEffect(PotionEffectType.DARKNESS, SLEEP_DURATION_TICKS, 0))
        entity.addPotionEffect(PotionEffect(PotionEffectType.JUMP_BOOST, SLEEP_DURATION_TICKS, 255))

        // Zzz particles
        object : BukkitRunnable() {
            var effectTicks = 0
            override fun run() {
                if (effectTicks >= SLEEP_DURATION_TICKS || !entity.isValid || !entity.hasMetadata(METADATA_KEY_IS_SLEEPING)) {
                    // If sleep ended (e.g. by damage via listener removing metadata), stop particles
                    if (entity.isValid && entity.hasMetadata(METADATA_KEY_IS_SLEEPING)) { // natural expiry
                        entity.removeMetadata(METADATA_KEY_IS_SLEEPING, plugin)
                    }
                    this.cancel()
                    return
                }
                val loc = entity.location.add(0.0, entity.height + 0.3, 0.0)
                entity.world.spawnParticle(Particle.NOTE, loc, 1, 0.1,0.1,0.1,0.1) // Using NOTE for Zzz
                effectTicks += 20 // Spawn Zzz every second
            }
        }.runTaskTimer(plugin, 0L, 20L)
    }
    
    private fun isHostile(entity: LivingEntity): Boolean {
        return entity !is Player // Placeholder
    }
}
