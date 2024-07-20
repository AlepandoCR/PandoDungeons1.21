package pandodungeons.pandodungeons.CustomEntities.Companions.Armadillo;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.armadillo.Armadillo;

import java.util.EnumSet;

public class FollowPlayerGoal extends Goal {
    private final Armadillo armadillo;
    private Player targetPlayer;
    private final double speedModifier;
    private final float stopDistance;
    private final float startDistance;

    public FollowPlayerGoal(Armadillo armadillo, double speedModifier, float startDistance, float stopDistance) {
        this.armadillo = armadillo;
        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.targetPlayer = this.armadillo.level().getNearestPlayer(this.armadillo, startDistance);
        return this.targetPlayer != null && this.armadillo.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPlayer != null && this.armadillo.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public void start() {
        this.armadillo.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.armadillo.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.armadillo.getLookControl().setLookAt(this.targetPlayer, 10.0F, (float)this.armadillo.getMaxHeadXRot());
        if (this.armadillo.distanceTo(this.targetPlayer) > 2.0D) {
            this.armadillo.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
        } else {
            this.armadillo.getNavigation().stop();
        }
    }
}
