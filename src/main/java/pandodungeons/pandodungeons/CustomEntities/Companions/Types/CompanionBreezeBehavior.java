package pandodungeons.pandodungeons.CustomEntities.Companions.Types;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.EnumSet;
import java.util.UUID;

public class CompanionBreezeBehavior extends Breeze {
    private UUID ownerUUID;

    public CompanionBreezeBehavior(EntityType<? extends Monster> type, Level world) {
        super(type, world);
    }

    // Set the owner UUID for the Companion Breeze
    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    // Get the owner as a LivingEntity
    @Nullable
    public LivingEntity getOwner() {
        if (ownerUUID == null) {
            return null;
        }
        return this.level().getPlayerByUUID(ownerUUID);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Add goal to follow the owner
        this.goalSelector.addGoal(1, new CustomFollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
    }

    @Override
    public boolean canAttackType(@NotNull EntityType<?> type) {
        Set<EntityType<?>> hostileTypes = Set.of(
                EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
                EntityType.SPIDER, EntityType.WITCH, EntityType.ENDERMAN,
                EntityType.WITHER_SKELETON, EntityType.PILLAGER, EntityType.VINDICATOR,
                EntityType.BLAZE, EntityType.HUSK, EntityType.STRAY,
                EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.EVOKER,
                EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN, EntityType.HOGLIN,
                EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOGLIN, EntityType.SHULKER,
                EntityType.WITHER, EntityType.DROWNED, EntityType.GHAST,
                EntityType.MAGMA_CUBE, EntityType.RAVAGER, EntityType.SLIME, EntityType.VEX
        );

        return hostileTypes.contains(type);
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        LivingEntity currentTarget = super.getTarget();
        if (currentTarget instanceof Player && !currentTarget.getUUID().equals(this.ownerUUID)) {
            return null; // Do not target players except the owner
        }
        return currentTarget;
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.6299999952316284).add(Attributes.MAX_HEALTH, 30.0).add(Attributes.FOLLOW_RANGE, 24.0).add(Attributes.ATTACK_DAMAGE, 3.0);
    }

    // Custom goal to follow the owner
    private static class CustomFollowOwnerGoal extends Goal {
        private final CompanionBreezeBehavior entity;
        private final double speedModifier;
        private final float stopDistance;
        private final float startDistance;
        private LivingEntity owner;

        public CustomFollowOwnerGoal(CompanionBreezeBehavior entity, double speedModifier, float startDistance, float stopDistance) {
            this.entity = entity;
            this.speedModifier = speedModifier;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = this.entity.getOwner();
            if (owner == null) {
                return false;
            } else if (this.entity.distanceTo(owner) < this.startDistance) {
                return false;
            } else {
                this.owner = owner;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.owner != null && this.entity.distanceTo(this.owner) > this.stopDistance && !this.entity.getNavigation().isDone();
        }

        @Override
        public void start() {
            this.entity.getNavigation().moveTo(this.owner, this.speedModifier);
        }

        @Override
        public void stop() {
            this.owner = null;
            this.entity.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.owner != null) {
                this.entity.getLookControl().setLookAt(this.owner, 10.0F, (float)this.entity.getMaxHeadXRot());
                this.entity.getNavigation().moveTo(this.owner, this.speedModifier);
            }
        }
    }
}
