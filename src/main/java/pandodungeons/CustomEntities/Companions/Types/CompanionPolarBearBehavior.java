package pandodungeons.CustomEntities.Companions.Types;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.plugin.Plugin;
import pandodungeons.CustomEntities.Companions.PolarBear.FollowPlayerGoal;
import pandodungeons.CustomEntities.Companions.PolarBear.FreezeNearbyEntitiesGoal;

public class CompanionPolarBearBehavior extends PolarBear {
    private final Plugin plugin;
    private final Player player;

    public CompanionPolarBearBehavior(EntityType<? extends PolarBear> type, Level world, Plugin plugin, Player player) {
        super(type, world);
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        super.goalSelector.addGoal(0, new FloatGoal(this));
        super.goalSelector.addGoal(1, new FollowPlayerGoal(this, 1.25, 10.0F, 1.0F)); // Sigue al jugador
        super.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
        super.goalSelector.addGoal(3, new FreezeNearbyEntitiesGoal(this, plugin));
    }

    @Override
    public void tick() {
        super.tick();;
    }
}
