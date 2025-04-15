package controlledEntities.modeled.pets.types.sakura.goals;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Predicate;

public class PastureAnimalsGoal extends Goal {
    private final Mob mob;
    private final Player owner;
    private final org.bukkit.entity.Player bPlayer;
    private final double speed;
    private final double radius = 5.0;
    private final double pushStrength = 0.15;
    private final int minAnimals = 3;
    private final double maxDistanceFromOwner = 20;

    private final Predicate<LivingEntity> validAnimals = entity ->
            (entity instanceof Sheep || entity instanceof Cow || entity instanceof Pig || entity instanceof Chicken)
                    && !entity.isFallFlying();

    private List<LivingEntity> herdedAnimals = new ArrayList<>();
    private Vec3 center = null;
    private int circleTick = 0;
    private boolean herding = false;

    public PastureAnimalsGoal(Mob mob, Player owner, double speed) {
        this.mob = mob;
        this.owner = owner;
        this.speed = speed;
        this.bPlayer = (org.bukkit.entity.Player) owner.getBukkitEntity();
    }

    @Override
    public boolean canUse() {
        if (mob.distanceTo(owner) > maxDistanceFromOwner) return false;

        List<LivingEntity> nearby = mob.level().getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(15),
                validAnimals::test);

        if (nearby.size() >= minAnimals) {
            herdedAnimals = nearby;
            center = calculateCenter(herdedAnimals);
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        herding = true;
        circleTick = 0;
    }

    @Override
    public void stop() {
        herding = false;
        herdedAnimals.clear();
        center = null;
    }

    @Override
    public boolean canContinueToUse() {
        return herding && !herdedAnimals.isEmpty();
    }

    @Override
    public void tick() {
        if (center == null || herdedAnimals.isEmpty()) return;

        // Actualizar centro dinámicamente (opcional)
        center = calculateCenter(herdedAnimals);

    
        circleTick++;
        double angle = (circleTick * 4 % 360) * (Math.PI / 180); // velocidad angular ajustable
        double x = center.x + radius * Math.cos(angle);
        double z = center.z + radius * Math.sin(angle);
        mob.getNavigation().moveTo(x, center.y, z, speed);

       
        boolean allCentered = true;
        boolean allDeath = true;
        for (LivingEntity animal : herdedAnimals) {
            if(animal.isAlive()){
                allDeath = false;
            }
            double distance = animal.position().distanceTo(center);
            if (distance > 2.2) {
                allCentered = false;
                Vec3 direction = center.subtract(animal.position()).normalize();
                animal.setDeltaMovement(
                        animal.getDeltaMovement().add(direction.scale(pushStrength))
                );
            }
        }

       
        if (allCentered || allDeath) {
            for (LivingEntity animal : herdedAnimals){
                animal.kill((ServerLevel) animal.level());
            }
            returnToOwner();
            stop();
        }
    }

    private void returnToOwner() {
        mob.getNavigation().moveTo(owner, speed);
    }

    private Vec3 calculateCenter(List<LivingEntity> entities) {
        double x = 0, y = 0, z = 0;
        for (LivingEntity e : entities) {
            x += e.getX();
            y += e.getY();
            z += e.getZ();
        }
        int size = entities.size();
        return new Vec3(x / size, y / size, z / size);
    }

    private void announce() {
        bPlayer.sendMessage(net.kyori.adventure.text.Component.text("¡Los animales han sido pastoreados con éxito!"));
    }
}
