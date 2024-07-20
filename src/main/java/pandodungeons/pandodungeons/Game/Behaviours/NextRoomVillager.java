package pandodungeons.pandodungeons.Game.Behaviours;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class NextRoomVillager extends Villager {

    public NextRoomVillager(Location loc, Player player) {
        super(EntityType.VILLAGER, ((CraftWorld) loc.getWorld()).getHandle());
        teleportTo(loc.getX(), loc.getY(), loc.getZ());

        this.brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(((CraftPlayer) player).getHandle(), true));
        this.brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(((CraftPlayer) player).getHandle(), true), 1.0F, 2));
    }


    @Override
    protected @NotNull Brain<?> makeBrain(Dynamic<?> dynamic) {
        Brain<Villager> behaviorcontroller = this.brainProvider().makeBrain(dynamic);
        this.cerebelo(behaviorcontroller);
        return behaviorcontroller;
    }

    @Override
    public void refreshBrain(@NotNull ServerLevel world) {
        Brain<Villager> behaviorcontroller = this.getBrain();
        super.brain = behaviorcontroller.copyWithoutBehaviors();
        this.cerebelo(this.getBrain());
    }

    private void cerebelo(Brain<Villager> brain) {
        brain.addActivity(Activity.IDLE, createBehaviorList());
        brain.setDefaultActivity(Activity.IDLE);
        brain.setActiveActivityIfPossible(Activity.IDLE);
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> createBehaviorList() {
        return ImmutableList.of(
                Pair.of(0, SetWalkTargetFromLookTarget.create(0.5F, 2)),
                Pair.of(1, new MoveToTargetSink())
        );
    }
}
