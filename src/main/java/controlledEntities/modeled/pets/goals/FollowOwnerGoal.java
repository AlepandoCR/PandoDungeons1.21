package controlledEntities.modeled.pets.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Particle;
import java.util.EnumSet;

public class FollowOwnerGoal extends Goal {
    private final Mob mob;
    private final Player targetPlayer;
    private final double groundSpeed;
    private final double airSpeed;
    private final float stopDistance;
    private final float startDistance;

    public FollowOwnerGoal(Mob mob, Player targetPlayer, double groundSpeed, double airSpeed, float startDistance, float stopDistance) {
        this.mob = mob;
        this.targetPlayer = targetPlayer;
        this.groundSpeed = groundSpeed;
        this.airSpeed = airSpeed;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.targetPlayer != null && this.mob.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPlayer != null && this.mob.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.targetPlayer, this.groundSpeed);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.mob.setNoGravity(false);
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(this.targetPlayer, 10.0F, (float) this.mob.getMaxHeadXRot());

        if (this.mob.distanceTo(this.targetPlayer) >= 15) {
            mob.getBukkitEntity().teleport(targetPlayer.getBukkitEntity());
            return;
        }

        org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) this.targetPlayer.getBukkitEntity();
        boolean playerFlying = bukkitPlayer.isFlying() || bukkitPlayer.isGliding();

        if (playerFlying) {
            this.mob.setNoGravity(true);

            Vec3 direction = new Vec3(
                    targetPlayer.getX() - mob.getX(),
                    targetPlayer.getY() - mob.getY(),
                    targetPlayer.getZ() - mob.getZ()
            ).normalize().scale(airSpeed);
            this.mob.setDeltaMovement(direction);

             if(mob.distanceTo(targetPlayer) > 5){
                 this.mob.getBukkitEntity().getWorld().spawnParticle(Particle.CLOUD,
                         this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ(),
                         5, 0.2, 0.2, 0.2, 0.02);
             }
        } else {
            this.mob.setNoGravity(false);

            if (this.mob.distanceTo(this.targetPlayer) >= startDistance) {
                this.mob.getNavigation().moveTo(this.targetPlayer, this.groundSpeed);
            } else {
                this.mob.getNavigation().stop();
            }
        }
    }
}
