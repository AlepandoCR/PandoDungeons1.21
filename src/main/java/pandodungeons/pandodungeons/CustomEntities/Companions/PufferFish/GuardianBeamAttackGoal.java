package pandodungeons.pandodungeons.CustomEntities.Companions.PufferFish;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos; // Reemplazo para BlockPos
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.EnumSet;
import java.util.List;

public class GuardianBeamAttackGoal extends Goal {
    private final Pufferfish pufferfish;
    private final Level world;
    private final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    private final double beamRate;
    private Monster target;
    private long lastBeamTime;

    public GuardianBeamAttackGoal(Pufferfish pufferfish, int level)  {
        this.pufferfish = pufferfish;
        this.world = pufferfish.level();
        this.beamRate = Math.min(3.0, 1.0 + ((3.0 - 1.0) / 29) * (level - 1)); // beamRate para alcanzar 3.0 en el nivel 30
        this.setFlags(EnumSet.of(Goal.Flag.TARGET, Goal.Flag.LOOK));
        this.lastBeamTime = 0;
    }

    @Override
    public boolean canUse() {
        if (world == null) {
            return false;
        }

        List<Monster> nearbyMonsters = world.getEntitiesOfClass(Monster.class, pufferfish.getBoundingBox().inflate(10));
        if (!nearbyMonsters.isEmpty()) {
            this.target = nearbyMonsters.get(0); // Obtiene el primer objetivo de la lista
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        if (this.target != null && this.target.isAlive() && this.target != this.pufferfish.getTarget()) {
            plugin.getLogger().info("Setting target: " + this.target);
            this.pufferfish.setTarget(this.target, EntityTargetEvent.TargetReason.CUSTOM, true);
            freezePufferfish(); // Congelar al Pufferfish en su posición
        } else {
            plugin.getLogger().info("Target is invalid, resetting.");
            this.target = null;
        }
    }

    @Override
    public void stop() {
        this.pufferfish.setTarget(null);
        this.target = null;
        unfreezePufferfish(); // Descongelar al Pufferfish
    }

    @Override
    public void tick() {
        if (this.target != null && this.target.isAlive()) {
            this.pufferfish.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

            Vec3 targetPosition = new Vec3(this.target.getX(), this.target.getY(), this.target.getZ());
            Vec3 pufferfishPosition = new Vec3(this.pufferfish.getX(), this.pufferfish.getY(), this.pufferfish.getZ());

            // Solo considerar el eje Z para el movimiento opuesto
            Vec3 directionToTarget = new Vec3(0, 0, targetPosition.z - pufferfishPosition.z).normalize();

            // Calcular la posición deseada: 3 bloques en la dirección opuesta y 3 bloques arriba del objetivo
            Vec3 offsetFromTarget = directionToTarget.scale(-5); // Mover 3 bloques en la dirección opuesta
            Vec3 desiredPosition = targetPosition.add(offsetFromTarget).add(0, 3, 0); // Añadir 3 bloques arriba

            // Verificar y ajustar la posición para evitar colisiones con bloques
            BlockPos blockPos = new BlockPos((int) desiredPosition.x, (int) desiredPosition.y, (int) desiredPosition.z);
            if (world.getBlockState(blockPos).isCollisionShapeFullBlock(world, blockPos)) {
                // Ajustar la posición si hay una colisión
                // Mover solo 2 bloques arriba si hay colisión
                desiredPosition = targetPosition.add(offsetFromTarget).add(0, 2, 0);
            }

            // Mover al Pufferfish a la posición deseada
            Vec3 deltaMovement = desiredPosition.subtract(pufferfishPosition).normalize().scale(0.5);
            this.pufferfish.setDeltaMovement(deltaMovement);

            // Llamar al ataque del rayo del guardián
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBeamTime >= 1000 / beamRate) {
                this.shootGuardianBeam();
                this.pufferfish.doHurtTarget((ServerLevel) pufferfish.level(),target);
                lastBeamTime = currentTime;
            }
        } else {
            stop(); // Detener el objetivo si es inválido
        }
    }


    private void shootGuardianBeam() {
        LivingEntity targetBukkitEntity = (LivingEntity) this.target.getBukkitEntity();
        Location start = this.pufferfish.getBukkitEntity().getLocation().add(0, 1.0, 0);
        Location end = targetBukkitEntity.getLocation().add(0, 1.0, 0);
        Vec3 startVec = new Vec3(start.getX(), start.getY(), start.getZ());
        Vec3 endVec = new Vec3(end.getX(), end.getY(), end.getZ());
        Vec3 direction = endVec.subtract(startVec).normalize();

        double distance = startVec.distanceTo(endVec);
        double stepSize = 0.2;

        for (double t = 0; t <= distance; t += stepSize) {
            Vec3 particlePosition = startVec.add(direction.scale(t));
            Location particleLocation = new Location(
                    this.pufferfish.getBukkitEntity().getWorld(),
                    particlePosition.x,
                    particlePosition.y,
                    particlePosition.z
            );

            // Spawn End Rod particle for the bullet effect (as a single moving point)
            particleLocation.getWorld().spawnParticle(
                    Particle.DUST,
                    particleLocation,
                    1,
                    new Particle.DustOptions(Color.AQUA, 1.0F)
            );
        }
    }

    private void freezePufferfish() {
        // Congela al Pufferfish en su posición actual
        Vec3 currentMovement = this.pufferfish.getDeltaMovement();
        this.pufferfish.setDeltaMovement(Vec3.ZERO); // Detener cualquier movimiento
    }

    private void unfreezePufferfish() {
        // Restablecer el movimiento del Pufferfish si es necesario
        // Dependiendo del comportamiento deseado, podrías restaurar el movimiento previo aquí.
    }
}
