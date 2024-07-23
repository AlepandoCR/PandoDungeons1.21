package pandodungeons.pandodungeons.CustomEntities.Companions.Types;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.level.Level;
import pandodungeons.pandodungeons.CustomEntities.Companions.Sniff.FollowPlayerGoal;

public class CompanionSnifferBehavior extends Sniffer {
    public CompanionSnifferBehavior(EntityType<? extends Animal> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FollowPlayerGoal(this, 1.0D, 10.0F, 2.0F));
    }
}
