package tcg.cards.skills.types

import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.RayTraceResult
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.text.Description
import tcg.util.text.SkillDescription
import kotlin.math.min

class EssenceDrainSkill(
    plugin: PandoDungeons,
    rarity: CardRarity
) : CardSkill(plugin, rarity) {

    override fun startCondition(): Boolean = true

    override fun shouldWaitForCondition(): Boolean = true

    override fun setSkillType(): String = "essence_drain"

    override fun setDescription(): Description {
        return SkillDescription("Lanza un rayo que roba esencia", rarity)
    }

    override fun task() {
        if (!hasPlayer()) return
        val caster = getDummyPlayer()
        val world = caster.world

        val targetEntity = getTarget(caster)

        if (!targetEntity.isValid) {
            caster.sendMessage("Objetivo Invalido")
            world.playSound(caster.location, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1.2f)
            return
        }

        val maxChannelDuration = 5 * 20 // 5 seconds
        val damagePerSecond = 10.0
        val healFactor = 1.0 // 100% of damage dealt
        val tickInterval = 10L // Apply effects every half-second (damage will be per-interval)
        val damagePerInterval = damagePerSecond * (tickInterval / 20.0)
        val maxDistanceSquared = 13.0 * 13.0 // Break if target > 13 blocks away (12 + buffer)

        caster.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, maxChannelDuration + 20, 0,false,false, false))
        world.playSound(caster.location, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 0.8f)
        world.playSound(targetEntity.location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 1.2f)

        object : BukkitRunnable() {
            var ticksChannelled = 0
            val casterInitialLocation = caster.location.clone() // To check if caster moved too much
            val casterInitialDirection = caster.location.direction.clone()

            override fun run() {
                // Stop conditions
                if (ticksChannelled >= maxChannelDuration ||
                    !caster.isValid || caster.isDead ||
                    !targetEntity.isValid || targetEntity.isDead ||
                    targetEntity.location.distanceSquared(caster.location) > maxDistanceSquared ||
                    caster.location.distanceSquared(casterInitialLocation) > 2.0*2.0 || // Caster moved more than 2 blocks
                    !caster.hasLineOfSight(targetEntity) || // LOS check
                     caster.location.direction.angle(casterInitialDirection) > 0.5 // Caster turned too much (approx 30 degrees)
                    ) {
                    caster.removePotionEffect(PotionEffectType.SLOWNESS) // Clean up slowness
                    world.playSound(caster.location, Sound.BLOCK_BEACON_DEACTIVATE, 1f, 1.0f) // Channel end
                    if (targetEntity.isValid && targetEntity.hasMetadata("soul_siphon_victim")) {
                        targetEntity.removeMetadata("soul_siphon_victim", plugin)
                    }
                    this.cancel()
                    return
                }
                
                if(ticksChannelled == 0) targetEntity.setMetadata("soul_siphon_victim", FixedMetadataValue(plugin, true))


                // Damage target
                targetEntity.damage(damagePerInterval, caster)
                val healedAmount = damagePerInterval * healFactor
                val casterMaxHealth = caster.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
                caster.health = min(caster.health + healedAmount, casterMaxHealth)

                // Visuals: Beam and particle flow
                drawSiphonBeam(caster.eyeLocation, targetEntity.location.add(0.0, targetEntity.height / 2, 0.0))
                world.spawnParticle(Particle.SOUL, targetEntity.location.add(0.0, targetEntity.height/2,0.0), 3,0.2,0.3,0.2,0.05) // Souls from target
                world.spawnParticle(Particle.HEART, caster.location.add(0.0, caster.height/2,0.0), 1,0.2,0.3,0.2,0.01) // Health to caster


                if (ticksChannelled % 20L == 0L) { // Sounds every second
                    world.playSound(caster.location, Sound.BLOCK_CONDUIT_AMBIENT_SHORT, 0.5f, 1.5f)
                    world.playSound(targetEntity.location, Sound.ENTITY_HUSK_HURT, 0.5f, 0.8f)
                }

                ticksChannelled += tickInterval.toInt()
            }
        }.runTaskTimer(plugin, 0L, tickInterval)
    }

    private fun drawSiphonBeam(start: Location, end: Location) {
        val world = start.world
        val distance = start.distance(end)
        if (distance < 0.5) return

        val direction = end.toVector().subtract(start.toVector()).normalize()
        val points = (distance * 4).toInt() // 4 particles per block for a denser beam

        for (i in 0..points) {
            val currentLoc = start.clone().add(direction.clone().multiply(i.toDouble() / 4.0))
            // Dark purple/black beam
            world.spawnParticle(Particle.DUST, currentLoc, 1,0.0,0.0,0.0 ,Particle.DustOptions(Color.PURPLE, 0.7f))
            if (i % 3 == 0) {
                 world.spawnParticle(Particle.DUST, currentLoc,  1,0.0,0.0,0.0 ,Particle.DustOptions(Color.BLACK, 0.9f))
            }
            if (Math.random() < 0.1) { // Flickering souls/energy within beam
                 world.spawnParticle(Particle.SOUL_FIRE_FLAME, currentLoc, 1,0.0,0.01,0.0)
            }
        }
    }
    
    private fun isHostile(entity: LivingEntity): Boolean {
        return entity !is Player
    }

    private fun getTarget(caster: Player, maxDistance: Double = 30.0, margin: Double = 3.0): LivingEntity {
        val eyeLocation = caster.eyeLocation
        val direction = eyeLocation.direction

        val result: RayTraceResult? = caster.world.rayTrace(
            eyeLocation,
            direction,
            maxDistance,
            FluidCollisionMode.NEVER,
            true,
            margin
        ) { entity ->
            entity is LivingEntity && isHostile(entity)
        }

        return result?.hitEntity as? LivingEntity ?: caster
    }
}
