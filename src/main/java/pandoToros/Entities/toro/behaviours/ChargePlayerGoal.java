package pandoToros.Entities.toro.behaviours;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ChargePlayerGoal extends Goal {
    private final Ravager toro;
    private final double speedModifier;
    private Player target;
    private int stareTicks; // Ticks for staring
    private boolean charging; // Indicates if the toro is currently charging
    private Vec3 chargeDirection; // Current charging direction

    public ChargePlayerGoal(Ravager toro, double speedModifier) {
        this.toro = toro;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.target = this.toro.level().getNearestPlayer(this.toro, 40.0);
        return this.target != null && this.toro.distanceToSqr(this.target) > 4.0;
    }

    @Override
    public void start() {
        this.stareTicks = 40; // Stare for 2 seconds (40 ticks)
        this.charging = false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target.isAlive() && this.toro.distanceToSqr(this.target) > 2.0;
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        this.toro.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        if (this.stareTicks > 0) {
            // Staring phase
            this.stareTicks--;
        } else if (!charging) {
            // Start charging after staring
            this.charging = true;
            this.chargeDirection = this.target.position().subtract(this.toro.position()).normalize();
            this.toro.getNavigation().moveTo(
                    this.toro.getX() + this.chargeDirection.x * 40, // Longer charge distance
                    this.toro.getY(),
                    this.toro.getZ() + this.chargeDirection.z * 40,
                    this.speedModifier
            );
        } else {
            // Check for obstacles in a 3x3 area
            Vec3 nextPosition = this.toro.position().add(this.chargeDirection.scale(0.5)); // Step forward
            BlockPos centerPos = new BlockPos(
                    (int) Math.floor(nextPosition.x),
                    (int) Math.floor(nextPosition.y),
                    (int) Math.floor(nextPosition.z)
            );

            // Iterate through a 3x3 area (3 high, 3 wide, centered on the next position)
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = 0; dy <= 2; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos blockPos = centerPos.offset(dx, dy, dz);

                        // If the block is not air, destroy it
                        if (!this.toro.level().getBlockState(blockPos).isAir()) {
                            this.toro.level().destroyBlock(blockPos, true); // Break the block
                        }
                    }
                }
            }

            // Check if the Ravager has reached the target
            if (this.toro.distanceTo(this.target) <= 2.0) {
                // Impact phase
                Vec3 knockbackDirection = calculateKnockbackDirection(this.toro, this.target);
                this.target.hurt(this.toro.damageSources().mobAttack(this.toro), 8.0F); // Damage on impact
                this.target.push(knockbackDirection.x, 1.0, knockbackDirection.z); // Knockback player
                this.resetCharge(); // Reset to prepare for next charge
            } else if (this.toro.getNavigation().isDone()) {
                // If charge misses, reset and retry
                this.resetCharge();
            }
        }
    }

    @Override
    public void stop() {
        this.toro.getNavigation().stop();
        this.target = null;
        this.charging = false;
    }

    /**
     * Resets the charge state to retry or prepare for another attack.
     */
    private void resetCharge() {
        this.target = this.toro.level().getNearestPlayer(this.toro, 40.0);
        this.stareTicks = 40; // Restart staring phase
        this.charging = false;
    }

    /**
     * Calculates the knockback direction based on the position of the player relative to the toro.
     *
     * @param toro   The toro entity.
     * @param target The player being charged.
     * @return The knockback direction as a Vec3.
     */
    private Vec3 calculateKnockbackDirection(Ravager toro, Player target) {
        Vec3 toroToPlayer = target.position().subtract(toro.position()).normalize();
        Vec3 perpendicular = new Vec3(-toroToPlayer.z, 0, toroToPlayer.x); // Perpendicular vector

        // Determine the side based on player's position relative to toro
        double side = toroToPlayer.cross(new Vec3(0, 1, 0)).y;
        if (side < 0) {
            return toroToPlayer.add(perpendicular.scale(-0.5)).normalize(); // Push right
        } else {
            return toroToPlayer.add(perpendicular.scale(0.5)).normalize(); // Push left
        }
    }
}
