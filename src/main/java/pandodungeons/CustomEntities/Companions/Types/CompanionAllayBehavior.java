package pandodungeons.CustomEntities.Companions.Types;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.EnumSet;

public class CompanionAllayBehavior extends Allay {

    private Player targetPlayer;

    public CompanionAllayBehavior(EntityType<? extends Allay> type, org.bukkit.World bukkitWorld, Player player) {
        super(type, ((CraftWorld) bukkitWorld).getHandle());
        teleportTo(player.getX(), player.getY(), player.getZ());
        this.targetPlayer = player;
    }

    public Player getTargetPlayer(){
        return targetPlayer;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FollowPlayerGoal(this, 1.0, 0f, 0f));
    }

    private class FollowPlayerGoal extends Goal {
        private final Allay allay;
        private final double speedModifier;
        private final float stopDistance;
        private final float startDistance;

        public FollowPlayerGoal(Allay allay, double speedModifier, float startDistance, float stopDistance) {
            this.allay = allay;
            this.speedModifier = speedModifier;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (targetPlayer == null) {
                return false;
            } else if (targetPlayer.isSpectator() || targetPlayer.isDeadOrDying()) {
                return false;
            } else {
                return !(this.allay.distanceToSqr(targetPlayer) < (double)(this.stopDistance * this.stopDistance));
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !targetPlayer.isSpectator() && !targetPlayer.isDeadOrDying() && !(this.allay.distanceToSqr(targetPlayer) < (double)(this.startDistance * this.startDistance));
        }

        @Override
        public void stop() {
            targetPlayer = null;
        }

        @Override
        public void tick() {
            if (targetPlayer != null) {
                // Actualizar la posición del objetivo antes de moverse
                this.allay.getNavigation().moveTo(targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(), this.speedModifier);
            }
        }
    }
}
