package pandodungeons.CustomEntities.Companions.Armadillo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;
import java.util.List;

public class FindHostileMobsGoal extends Goal {
    private final Mob armadillo;
    private final ServerLevel world;
    private LivingEntity target;

    public FindHostileMobsGoal(Mob armadillo) {
        this.armadillo = armadillo;
        this.world = (ServerLevel) armadillo.level();
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (world == null) {
            return false;
        }

        List<Monster> nearbyMonsters = world.getEntitiesOfClass(Monster.class, armadillo.getBoundingBox().inflate(10));

        if (!nearbyMonsters.isEmpty()) {
            this.target = nearbyMonsters.get(0);
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        if (this.target != null) {
            try {
                this.armadillo.setTarget(this.target, EntityTargetEvent.TargetReason.CUSTOM);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        this.armadillo.setTarget(null);
        this.target = null;
    }
}
