package controlledEntities;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.Mob;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class ControlledEntity {
    protected static final Map<UUID, ControlledEntity> CONTROLLED_ENTITIES = new HashMap<>();

    protected final Mob mob;
    protected final PandoDungeons plugin;
    protected List<Goal> goals;
    protected final UUID uuid;
    protected final Location spawnLoc;

    public ControlledEntity(PandoDungeons plugin, Location spawnLoc, boolean appyGoals) {
        this.plugin = plugin;
        this.spawnLoc = spawnLoc;
        this.mob = setEntity();
        this.uuid = mob.getUniqueId();

        if(appyGoals){
            this.goals = setGoals();
            Bukkit.getScheduler().runTaskLater(plugin, this::applyGoalsMain, 10L);
        }

        CONTROLLED_ENTITIES.put(uuid, this);
    }

    public abstract List<Goal> setGoals();

    public List<Goal> getGoals() {
        return goals;
    }

    public Mob getMob() {
        return mob;
    }

    public UUID getUuid() {
        return uuid;
    }

    public abstract Mob setEntity();

    private void applyGoalsMain() {
        if (goals.isEmpty()) return;

        net.minecraft.world.entity.Mob nmsMob = ((CraftMob) mob).getHandle();

        // Remover cualquier goal anterior
        nmsMob.goalSelector.removeAllGoals(goal -> true);

        int i = 1;
        for (Goal goal : goals) {
            nmsMob.goalSelector.addGoal(i, goal);
            i++;
        }

        Bukkit.getLogger().info("✔ Goals aplicados a " + mob.getName());
    }

    protected void applyGoals(Mob mob) {
        if (goals.isEmpty()) return;

        net.minecraft.world.entity.Mob nmsMob = ((CraftMob) mob).getHandle();

        // Remover cualquier goal anterior
        nmsMob.goalSelector.removeAllGoals(goal -> true);

        int i = 1;
        for (Goal goal : goals) {
            nmsMob.goalSelector.addGoal(i, goal);
            i++;
        }

        Bukkit.getLogger().info("✔ Goals aplicados a " + mob.getName());
    }

    public void restoreIfNeeded() {
        net.minecraft.world.entity.Mob nmsMob = ((CraftMob) mob).getHandle();

        if (goals.isEmpty()) return;

        boolean missingGoals = goals.stream()
                .noneMatch(goal -> nmsMob.goalSelector.getAvailableGoals()
                        .stream()
                        .map(WrappedGoal::getGoal)
                        .anyMatch(existingGoal -> existingGoal.getClass().equals(goal.getClass())));

        if (missingGoals) {
            Bukkit.getLogger().warning("⚠ Goals perdidos en " + mob.getName() + ", restaurando...");
            applyGoals(mob);
            extraRestore();
        }
    }

    public abstract void extraRestore();


    public static void startMonitoringControlledEntities(PandoDungeons plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (ControlledEntity entity : CONTROLLED_ENTITIES.values()) {
                    if (entity.mob.isDead() || !entity.mob.isValid()) {
                        continue;
                    }
                    entity.restoreIfNeeded();
                }
            }
        }.runTaskTimer(plugin, 100, 100); // Cada 5 segundos
    }
}
