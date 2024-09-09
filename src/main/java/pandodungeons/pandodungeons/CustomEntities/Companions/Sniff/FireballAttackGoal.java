package pandodungeons.pandodungeons.CustomEntities.Companions.Sniff;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;
import java.util.List;

public class FireballAttackGoal extends Goal {
    private final Sniffer sniffer;
    private final Level world;
    private final double fireRate;
    private Monster target;
    private long lastFireballTime;

    public FireballAttackGoal(Sniffer sniffer, int level) {
        this.sniffer = sniffer;
        this.world = sniffer.level();
        this.fireRate = Math.min(3.0, 1.0 + ((3.0 - 1.0) / 29) * (level - 1)); // fireRate para alcanzar 3.0 en el nivel 30
        this.setFlags(EnumSet.of(Goal.Flag.TARGET, Goal.Flag.LOOK));
        this.lastFireballTime = 0;
    }

    @Override
    public boolean canUse() {
        if (world == null) {
            return false;
        }

        List<Monster> nearbyMonsters = world.getEntitiesOfClass(Monster.class, sniffer.getBoundingBox().inflate(10));
        if (!nearbyMonsters.isEmpty()) {
            this.target = nearbyMonsters.get(0);
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        if (this.target != null) {
            try {
                this.sniffer.setTarget(this.target, EntityTargetEvent.TargetReason.CUSTOM, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        this.sniffer.setTarget(null);
        this.target = null;
    }

    @Override
    public void tick() {
        if (this.target != null && this.target.isAlive()) {
            this.sniffer.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if (this.sniffer.distanceTo(this.target) > 2.0F && this.sniffer.distanceTo(this.target) <= 10.0F) {
                double dx = this.target.getX() - this.sniffer.getX();
                double dy = this.target.getY(0.5) - (0.5 + this.sniffer.getY(0.5));
                double dz = this.target.getZ() - this.sniffer.getZ();

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastFireballTime >= 1000 / fireRate) {
                    Vec3 velocity = new Vec3(dx, dy, dz).normalize().scale(0.1);
                    SmallFireball fireball = new SmallFireball(world, this.sniffer, velocity);
                    fireball.setPos(this.sniffer.getX(), this.sniffer.getY(0.5) + 0.5, this.sniffer.getZ());
                    world.addFreshEntity(fireball);
                    lastFireballTime = currentTime;
                }
            }
        } else {
            stop();
        }
    }
}
