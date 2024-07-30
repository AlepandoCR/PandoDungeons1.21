package pandodungeons.pandodungeons.CustomEntities.Companions.Types;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import pandodungeons.pandodungeons.CustomEntities.Companions.Sniff.FireballAttackGoal;
import pandodungeons.pandodungeons.CustomEntities.Companions.Sniff.FollowPlayerGoal;

public class CompanionSnifferBehavior extends Sniffer {
    private final int lvl;

    public CompanionSnifferBehavior(EntityType<? extends Animal> type, Level world, int lvl) {
        super(type, world);
        this.lvl = lvl;
        this.initializeGoals();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    // Nuevo método para inicializar los goals
    private void initializeGoals() {
        this.goalSelector.addGoal(0, new FireballAttackGoal(this, lvl));
        this.goalSelector.addGoal(1, new FollowPlayerGoal(this, 1.0D, 10.0F, 2.0F));
    }
}
