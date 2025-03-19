package controlledEntities.modeled.pets.goals;

import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.model.ModeledEntity;
import controlledEntities.modeled.ModelBuilder;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

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
        if( this.mob.level() != this.targetPlayer.level()){
            mob.remove(Entity.RemovalReason.DISCARDED);
        }
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
