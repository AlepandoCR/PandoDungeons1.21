package pandodungeons.pandodungeons.CustomEntities.Companions.PolarBear;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.PolarBear;

import java.util.EnumSet;

public class FollowPlayerGoal extends Goal {
    private final PolarBear polarBear;
    private Player targetPlayer;
    private final double speedModifier;
    private final float stopDistance;
    private final float startDistance;

    public FollowPlayerGoal(PolarBear polarBear, double speedModifier, float startDistance, float stopDistance) {
        this.polarBear = polarBear;
        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.targetPlayer = this.polarBear.level().getNearestPlayer(this.polarBear, startDistance);
        return this.targetPlayer != null && this.polarBear.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPlayer != null && this.polarBear.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public void start() {
        this.polarBear.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.polarBear.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.polarBear.getLookControl().setLookAt(this.targetPlayer, 10.0F, (float) this.polarBear.getMaxHeadXRot());
        if (this.polarBear.distanceTo(this.targetPlayer) > 2.0D) {
            this.polarBear.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
        } else {
            this.polarBear.getNavigation().stop();
        }
    }
}
