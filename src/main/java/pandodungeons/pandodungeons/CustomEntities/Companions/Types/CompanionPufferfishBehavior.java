package pandodungeons.pandodungeons.CustomEntities.Companions.Types;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.level.Level;
import pandodungeons.pandodungeons.CustomEntities.Companions.PufferFish.FollowPlayerGoal;
import pandodungeons.pandodungeons.CustomEntities.Companions.PufferFish.GuardianBeamAttackGoal;

import java.util.Objects;

public class CompanionPufferfishBehavior extends Pufferfish {
    private final int lvl;

    public CompanionPufferfishBehavior(EntityType<? extends Pufferfish> type, Level world, int lvl) {
        super(type, world);
        this.lvl = lvl;
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) == null) {
            this.getAttributes().registerAttribute(Attributes.ATTACK_DAMAGE);
            Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(lvl * 2);
        }
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Método para inicializar los goalsprivatevoidinitializeGoals() {
        try {
            this.goalSelector.addGoal(0, new GuardianBeamAttackGoal(this, lvl));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        this.goalSelector.addGoal(1, new FollowPlayerGoal(this, 1.0D, 10.0F, 2.0F));
    }
}



