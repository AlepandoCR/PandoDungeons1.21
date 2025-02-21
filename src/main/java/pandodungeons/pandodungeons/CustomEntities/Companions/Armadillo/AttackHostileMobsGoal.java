package pandodungeons.pandodungeons.CustomEntities.Companions.Armadillo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import org.bukkit.Bukkit;

import java.util.Objects;

public class AttackHostileMobsGoal extends MeleeAttackGoal {
    private final Armadillo armadillo;

    public AttackHostileMobsGoal(Armadillo armadillo, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(armadillo, speedModifier, followingTargetEvenIfNotSeen);
        this.armadillo = armadillo;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.armadillo.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        this.armadillo.getNavigation().moveTo(Objects.requireNonNull(this.armadillo.getTarget()), this.armadillo.getSpeed());
    }

    @Override
    public void stop() {
        super.stop();
        this.armadillo.getNavigation().stop();
        this.armadillo.setTarget(null);
    }

    @Override
    public void tick() {
        LivingEntity target = this.armadillo.getTarget();
        if (target != null) {
            this.armadillo.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.armadillo.getLookControl().setLookAt(target, 10.0F, (float)this.armadillo.getMaxHeadXRot());
            if(target instanceof Spider){
                if (this.armadillo.distanceTo(target) > 2.0F) {
                    this.armadillo.getNavigation().moveTo(target, 1.0f);
                } else {
                    this.armadillo.getNavigation().stop();
                    this.armadillo.swing(InteractionHand.MAIN_HAND);
                    this.armadillo.doHurtTarget((ServerLevel) armadillo.level(),target);
                }
            }else{
                if (this.armadillo.distanceTo(target) > 1.5F) {
                    this.armadillo.getNavigation().moveTo(target, 1.0f);
                } else {
                    this.armadillo.getNavigation().stop();
                    this.armadillo.swing(InteractionHand.MAIN_HAND);
                    this.armadillo.doHurtTarget((ServerLevel) armadillo.level(),target);
                }
            }

        }
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return this.armadillo.getBbWidth() * 2.0F * this.armadillo.getBbWidth() * 2.0F + attackTarget.getBbWidth();
    }
}
