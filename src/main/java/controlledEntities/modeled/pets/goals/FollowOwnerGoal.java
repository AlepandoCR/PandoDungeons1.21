package controlledEntities.modeled.pets.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;


import java.util.EnumSet;


public class FollowOwnerGoal extends Goal {
    private final Mob mob;
    private final Player targetPlayer;
    private final double speedModifier;
    private final float stopDistance;
    private final float startDistance;

    public FollowOwnerGoal(Mob mob, Player targetPlayer, double speedModifier, float startDistance, float stopDistance) {
        this.mob = mob;
        this.targetPlayer = targetPlayer;
        this.speedModifier = speedModifier;
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
        // Continúa siguiendo al jugador mientras esté a una distancia mayor que stopDistance
        return this.targetPlayer != null && this.mob.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public void start() {
        // Comienza a moverse hacia el jugador
        this.mob.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        // Hace que el mob mire al jugador
        this.mob.getLookControl().setLookAt(this.targetPlayer, 10.0F, (float)this.mob.getMaxHeadXRot());
        // Mueve al mob hacia el jugador mientras esté a más de 2 bloques de distancia
        if (this.mob.distanceTo(this.targetPlayer) >= 15) {
          mob.getBukkitEntity().teleport(targetPlayer.getBukkitEntity());
        }
        if (this.mob.distanceTo(this.targetPlayer) >= startDistance) {
            this.mob.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
        }
        else {
            this.mob.getNavigation().stop();
        }

    }


}
