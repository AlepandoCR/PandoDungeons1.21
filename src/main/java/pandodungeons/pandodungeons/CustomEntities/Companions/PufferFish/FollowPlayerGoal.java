package pandodungeons.pandodungeons.CustomEntities.Companions.PufferFish;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class FollowPlayerGoal extends Goal {
    private final Pufferfish pufferfish;
    private Player targetPlayer;
    private final double speedModifier;
    private final float stopDistance;
    private final float startDistance;

    public FollowPlayerGoal(Pufferfish pufferfish, double speedModifier, float startDistance, float stopDistance) {
        this.pufferfish = pufferfish;
        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.targetPlayer = this.pufferfish.level().getNearestPlayer(this.pufferfish, startDistance);
        return this.targetPlayer != null && this.pufferfish.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPlayer != null && this.pufferfish.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public void start() {
        this.pufferfish.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.pufferfish.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.pufferfish.getLookControl().setLookAt(this.targetPlayer, 10.0F, (float)this.pufferfish.getMaxHeadXRot());
        if (this.pufferfish.distanceTo(this.targetPlayer) > 2.0D) {
            this.pufferfish.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
        } else {
            this.pufferfish.getNavigation().stop();
        }

        // Control de vuelo, para que el pufferfish se desplace en el aireif (this.pufferfish.isOnGround()) {
        this.pufferfish.setNoGravity(true);
        this.pufferfish.setDeltaMovement(this.pufferfish.getDeltaMovement().add(0, 0.1, 0));
    }
}

