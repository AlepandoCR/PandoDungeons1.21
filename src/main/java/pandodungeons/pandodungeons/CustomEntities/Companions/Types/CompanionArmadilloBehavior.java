package pandodungeons.pandodungeons.CustomEntities.Companions.Types;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.level.Level;
import pandodungeons.pandodungeons.CustomEntities.Companions.Armadillo.AttackHostileMobsGoal;
import pandodungeons.pandodungeons.CustomEntities.Companions.Armadillo.FindHostileMobsGoal;
import pandodungeons.pandodungeons.CustomEntities.Companions.Armadillo.FollowPlayerGoal;

import java.util.Objects;

public class CompanionArmadilloBehavior extends Armadillo {
    private final Level world;

    public CompanionArmadilloBehavior(EntityType<? extends Armadillo> type, Level world, int level) {
        super(type, world);
        this.world = world;
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) == null) {
            this.getAttributes().registerAttribute(Attributes.ATTACK_DAMAGE);
            Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(level);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new AttackHostileMobsGoal(this, 1.5D, true));
        this.goalSelector.addGoal(1, new FindHostileMobsGoal(this));
        this.goalSelector.addGoal(2, new FollowPlayerGoal(this, 1.0D, 10.0F, 2.0F));
    }
}
