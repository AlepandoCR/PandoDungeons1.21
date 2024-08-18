package pandodungeons.pandodungeons.CustomEntities.Companions.PufferFish;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LandOnOwnersShoulderGoal;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.EnumSet;
import java.util.List;

public class GuardianBeamAttackGoal extends Goal {
    private final Pufferfish pufferfish;
    private final Level world;
    private final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    private final double beamRate;
    private Monster target;
    private long lastBeamTime;

    public GuardianBeamAttackGoal(Pufferfish pufferfish, int level) throws ReflectiveOperationException {
        this.pufferfish = pufferfish;
        this.world = pufferfish.level();
        this.beamRate = Math.min(3.0, 1.0 + ((3.0 - 1.0) / 29) * (level - 1)); // beamRate para alcanzar 3.0 en el nivel 30this.setFlags(EnumSet.of(Goal.Flag.TARGET, Goal.Flag.LOOK));
        this.lastBeamTime = 0;
    }

    @Override
    public boolean canUse() {
        if (world == null) {
            return false;
        }

        List<Monster> nearbyMonsters = world.getEntitiesOfClass(Monster.class, pufferfish.getBoundingBox().inflate(10));
        if (!nearbyMonsters.isEmpty()) {
            this.target = nearbyMonsters.get(0);
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        if (this.target != null) {
            this.pufferfish.setTarget(this.target);
        }
    }

    @Override
    public void stop() {
        this.pufferfish.setTarget(null);
        this.target = null;
    }

    @Override
    public void tick() {
        if (this.target != null && this.target.isAlive()) {
            this.pufferfish.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if (this.pufferfish.distanceTo(this.target) <= 10.0F) {
                long currentTime= System.currentTimeMillis();
                if (currentTime - lastBeamTime >= 1000 / beamRate) {
                    try {
                        this.shootGuardianBeam();
                        this.pufferfish.doHurtTarget(target);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                    lastBeamTime = currentTime;
                }
            }
        } else {
            stop();
        }
    }

    private void shootGuardianBeam()throws ReflectiveOperationException {
        LivingEntity targetBukkitEntity= (LivingEntity) this.target.getBukkitEntity();
        Location start=this.pufferfish.getBukkitEntity().getLocation().add(0, 1.0, 0);
        Location end= targetBukkitEntity.getLocation().add(0, 1.0, 0); // End location (adjusted for visibility)
        Vec3 startVec=new Vec3(start.getX(), start.getY(), start.getZ());
        Vec3 endVec= new Vec3(end.getX(), end.getY(), end.getZ());
        Vec3 direction= endVec.subtract(startVec).normalize();

        double distance= startVec.distanceTo(endVec);
        double stepSize=0.5; // Adjust this value to make the beam smoother or more segmented
        for (double t=0; t <= distance; t += stepSize) {
            Vec3 particlePosition = startVec.add(direction.scale(t));
            world.addParticle(ParticleTypes.GLOW_SQUID_INK, particlePosition.x, particlePosition.y, particlePosition.z, 0, 0, 0);
        }
    }
}

