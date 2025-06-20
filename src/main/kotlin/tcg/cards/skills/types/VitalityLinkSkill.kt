package tcg.cards.skills.types

import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Breedable
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.RayTraceResult
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.text.Description
import tcg.util.text.SkillDescription
import kotlin.math.min

class VitalityLinkSkill(
    plugin: PandoDungeons,
    rarity: CardRarity
) : CardSkill(plugin, rarity) {

    override fun startCondition(): Boolean = true
    override fun shouldWaitForCondition(): Boolean = false

    override fun setSkillType(): String = "vitality_link"

    override fun setDescription(): Description {
        return SkillDescription("Te cura y al aliado que mires en cadena", rarity)
    }

    override fun task() {
        if (!hasPlayer()) return
        val caster = getDummyPlayer()

        // Target primary friendly (player looks at them, or self if no target)
        val currentTarget = getFriendlyTarget(caster)

        val initialHealAmount = 30.0
        val jumpRange = 8.0
        val falloffFactor = 0.75
        val maxJumps = 5

        val healedEntities = mutableSetOf<LivingEntity>()

        applyHealAndVisual(currentTarget, initialHealAmount, caster.eyeLocation, true)
        healedEntities.add(currentTarget)

        var lastHealedTarget = currentTarget
        var currentHealAmount = initialHealAmount

        for (jump in 0 until maxJumps) {
            currentHealAmount *= falloffFactor
            if (currentHealAmount < 1.0) break // Stop if heal amount is negligible

            val potentialTargets = lastHealedTarget.getNearbyEntities(jumpRange, jumpRange, jumpRange)
                .filterIsInstance<LivingEntity>()
                .filter { it.isValid && !healedEntities.contains(it) && isFriendlyOrSelf(it, caster) }
                .sortedBy { it.location.distanceSquared(lastHealedTarget.location) }
            
            if (potentialTargets.isEmpty()) break

            val nextTarget = potentialTargets.first()
            
            // Delay for visual chaining
            val delay = (jump + 1) * 10L // 0.5s, 1.0s delay for jumps
            object: BukkitRunnable() {
                override fun run() {
                    applyHealAndVisual(
                        nextTarget,
                        currentHealAmount,
                        lastHealedTarget.location.add(0.0, lastHealedTarget.height/2, 0.0)
                    )
                    healedEntities.add(nextTarget)
                }
            }.runTaskLater(plugin, delay)
            lastHealedTarget = nextTarget // Update for next potential jump, even if delayed for visual
        }
    }

    private fun getFriendlyTarget(caster: Player, maxDistance: Double = 15.0, margin: Double = 1.5): LivingEntity {
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
            entity is LivingEntity && isFriendlyOrSelf(entity, caster)
        }

        return result?.hitEntity as? LivingEntity ?: caster
    }

    private fun isFriendlyOrSelf(entity: LivingEntity, caster: Player): Boolean {
        if (entity.uniqueId == caster.uniqueId) return true
        if (entity is Player) {
            return true 
        }

        if(entity is Breedable) {
            return true
        }

        return false
    }

    private fun applyHealAndVisual(
        target: LivingEntity,
        healAmount: Double,
        beamStartLocation: Location,
        isPrimary: Boolean = false
    ) {
        val maxHealth = target.getAttribute(Attribute.MAX_HEALTH)?.value ?: target.health
        target.health = min(target.health + healAmount, maxHealth)

        val targetMidPoint = target.location.add(0.0, target.height / 2, 0.0)
        
        target.world.playSound(target.location, Sound.ENTITY_PLAYER_LEVELUP, 1.2f, if(isPrimary) 1.5f else 1.8f)
        target.world.spawnParticle(Particle.HEART, targetMidPoint, 10, 0.5, 0.5, 0.5, 0.1)
        target.world.spawnParticle(Particle.HAPPY_VILLAGER, targetMidPoint, 15, 0.5,0.5,0.5,0.1)

        drawHealingBeam(beamStartLocation, targetMidPoint)
    }

    private fun drawHealingBeam(start: Location, end: Location) {
        val world = start.world
        val distance = start.distance(end)
        if (distance < 0.5) return

        val direction = end.toVector().subtract(start.toVector()).normalize()
        val points = (distance * 3).toInt()

        for (i in 0..points) {
            val currentLoc = start.clone().add(direction.clone().multiply(i.toDouble() / 3.0))
            world.spawnParticle(Particle.DUST, currentLoc, 1, 0.0, 0.0, 0.0, 0.0, Particle.DustOptions(Color.LIME, 1.0f))
            if (i % 4 == 0) {
                world.spawnParticle(Particle.DUST, currentLoc, 1, 0.0,0.0,0.0,0.0, Particle.DustOptions(Color.YELLOW, 1.2f))
            }
        }
    }
}
