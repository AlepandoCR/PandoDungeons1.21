package pandoClass.classes.mage.skills;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.pandodungeons.PandoDungeons;
import pandoClass.Skill;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FrostNovaSkill extends Skill {

    private final PandoDungeons plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final int cooldownSeconds; // Base cooldown in seconds
    private final double radius;
    private final double damage;
    private final int slownessDuration; // In ticks
    private final int slownessAmplifier;

    public FrostNovaSkill(int lvl, Player player, PandoDungeons plugin) {
        super(lvl, player);
        this.plugin = plugin;
        this.description = "Emits a chilling nova, damaging and slowing nearby enemies.";

        // Level-based scaling
        this.cooldownSeconds = Math.max(5, 20 - lvl); // Cooldown decreases with level, min 5s
        this.radius = 5.0 + (lvl * 0.5); // Radius increases with level
        this.damage = 4.0 + lvl; // Damage increases with level
        this.slownessDuration = (3 + lvl) * 20; // Duration increases with level (3s base + 1s/lvl)
        this.slownessAmplifier = 1 + (lvl / 2); // Slowness II base, increases potency every 2 levels

        this.displayValue = String.format("%.1fs CD, %.1f DMG, %.1f RAD", (double)this.cooldownSeconds, this.damage, this.radius);
    }

    @Override
    public String getName() {
        return "Frost Nova";
    }

    @Override
    protected boolean canActivate() {
        if (owner == null || !owner.isOnline()) {
            return false;
        }
        if (cooldowns.containsKey(owner.getUniqueId())) {
            long timeLeft = ((cooldowns.get(owner.getUniqueId()) / 1000) + cooldownSeconds) - (System.currentTimeMillis() / 1000);
            if (timeLeft > 0) {
                // owner.sendMessage(String.format("Frost Nova on cooldown for %d seconds.", timeLeft)); // Optional: send cooldown message
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doAction() {
        if (owner == null || !owner.isOnline()) {
            return;
        }

        Location novaCenter = owner.getLocation();
        owner.getWorld().playSound(novaCenter, Sound.ENTITY_PLAYER_HURT_FREEZE, 1.0f, 0.5f);
        owner.getWorld().playSound(novaCenter, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);

        // Particle effects
        for (int i = 0; i < 360; i += 15) {
            double x = novaCenter.getX() + radius * Math.cos(Math.toRadians(i));
            double z = novaCenter.getZ() + radius * Math.sin(Math.toRadians(i));
            Location particleLoc = new Location(owner.getWorld(), x, novaCenter.getY() + 1.0, z);
            owner.getWorld().spawnParticle(Particle.SNOWFLAKE, particleLoc, 10, 0.5, 0.5, 0.5, 0.01);
            owner.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 5, 0.2, 0.2, 0.2, new Particle.DustOptions(Color.AQUA, 1.0f));
        }
        // Expanding sphere effect
        for (double r = 1; r <= radius; r += 0.5) {
            for (int i = 0; i < 360; i += 30) {
                double x = novaCenter.getX() + r * Math.cos(Math.toRadians(i)) * Math.sin(Math.toRadians(i % 90)); // More spherical
                double y = novaCenter.getY() + 1.0 + r * Math.sin(Math.toRadians(i));
                double z = novaCenter.getZ() + r * Math.sin(Math.toRadians(i)) * Math.cos(Math.toRadians(i % 90));
                 if(i%2 == 0) owner.getWorld().spawnParticle(Particle.SNOWFLAKE, x,y,z, 2,0.1,0.1,0.1,0);
            }
        }


        for (Entity entity : owner.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity && entity != owner && !(entity instanceof Player && !plugin.getServer().getPvP())) {
                LivingEntity livingEntity = (LivingEntity) entity;
                // Basic check for hostility, can be expanded
                boolean isHostile = !(entity instanceof org.bukkit.entity.Animals || entity instanceof org.bukkit.entity.Villager || entity.getScoreboardTags().contains("companionMob"));


                if (isHostile) {
                    livingEntity.damage(damage, owner);
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, slownessDuration, slownessAmplifier));
                }
            }
        }
        cooldowns.put(owner.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void reset() {
        // This skill doesn't have a persistent effect to reset beyond the cooldown,
        // but if it did (e.g., a toggle), this is where you'd handle it.
        // Cooldown is handled by canActivate.
    }
}
