package pandodungeons.CustomEntities.Companions.Sniff;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.sniffer.Sniffer;

import java.util.EnumSet;

public class FollowPlayerGoal extends Goal {
    private final Sniffer sniffer;
    private Player targetPlayer;
    private final double speedModifier;
    private final float stopDistance;
    private final float startDistance;

    public FollowPlayerGoal(Sniffer sniffer, double speedModifier, float startDistance, float stopDistance) {
        this.sniffer = sniffer;
        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.targetPlayer = this.sniffer.level().getNearestPlayer(this.sniffer, startDistance);
        return this.targetPlayer != null && this.sniffer.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPlayer != null && this.sniffer.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public void start() {
        this.sniffer.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.sniffer.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.sniffer.getLookControl().setLookAt(this.targetPlayer, 10.0F, (float) this.sniffer.getMaxHeadXRot());
        if (this.sniffer.distanceTo(this.targetPlayer) > 2.0D) {
            this.sniffer.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
        } else {
            this.sniffer.getNavigation().stop();
        }
    }
}
