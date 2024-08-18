package pandodungeons.pandodungeons.CustomEntities.Ball;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class BallArmadillo extends Armadillo {
    public static final double MAX_CHARGE = 100.0;
    private static final double CHARGE_RATE = 5.0; // Increase per tick
    private final HashMap<UUID, Double> chargeMap = new HashMap<>();
    private Vector previousDeltaMovement = new Vector(0,0,0); // Guarda la velocidad anterior

    public BallArmadillo(EntityType<? extends Animal> type, Level world) {
        super(type, world);
        this.setNoAi(false);
        this.switchToState(ArmadilloState.SCARED);
        this.setSilent(true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    public void spawn(Player player) {
        CraftEntity entity = this.getBukkitEntity();
        entity.addScoreboardTag("bolaFut");
        Bukkit.getLogger().info("Spawn bola: " + ((CraftWorld) player.getLocation().getWorld()).getHandle().addFreshEntity(entity.getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM));
        entity.teleport(player.getLocation());
    }

    public void spawnLocation(Location location) {
        CraftEntity entity = this.getBukkitEntity();
        entity.addScoreboardTag("bolaFut");
        Bukkit.getLogger().info("Spawn bola: " + ((CraftWorld) location.getWorld()).getHandle().addFreshEntity(entity.getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM));
        entity.teleport(location);
    }

    @Override
    public void tick() {
        super.tick();

        // Detecta colisiones antes de actualizar la velocidad
        boolean wasVerticalCollision = this.verticalCollision;
        boolean wasHorizontalCollision = this.horizontalCollision;

        // Guarda la velocidad actual antes de actualizarla
        if (!wasVerticalCollision && !wasHorizontalCollision) {
            previousDeltaMovement = new Vector(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
        }

        if (!this.getState().equals(ArmadilloState.SCARED)) {
            this.switchToState(ArmadilloState.SCARED);
        }

        // Encuentra el jugador más cercano dentro de un rango de 1.5 bloques
        net.minecraft.world.entity.player.Player nearestPlayerEntity = this.level().getNearestPlayer(this, 1.5);
        if (nearestPlayerEntity != null) {
            Player nearestPlayer = (Player) nearestPlayerEntity.getBukkitEntity();

            // Incrementa la carga si el jugador está manteniendo el click derecho
            if (chargeMap.containsKey(nearestPlayer.getUniqueId())) {
                double currentCharge = chargeMap.get(nearestPlayer.getUniqueId());
                if (currentCharge < MAX_CHARGE) {
                    currentCharge += CHARGE_RATE;
                    chargeMap.put(nearestPlayer.getUniqueId(), currentCharge);
                }
            }

            // Regatea la pelota
            Vector direction = this.getBukkitEntity().getLocation().toVector().subtract(nearestPlayer.getLocation().toVector()).normalize();
            this.setDeltaMovement(new Vec3(direction.getX() * 0.5, this.getDeltaMovement().y, direction.getZ() * 0.5));
            this.move(MoverType.SELF, this.getDeltaMovement());
        }

        // Detecta colisiones y maneja rebote horizontal
        if (this.horizontalCollision) {
            double xSpeed = -previousDeltaMovement.getX() * 0.4;
            double zSpeed = -previousDeltaMovement.getZ() * 0.4;

            // Asegura una velocidad mínima para que el rebote sea efectivo
            if (Math.abs(xSpeed) < 0.1) {
                xSpeed = -0.1;
            }

            if (Math.abs(zSpeed) < 0.1) {
                zSpeed = -0.1;
            }

            Vec3 newMovement = new Vec3(
                    xSpeed, // Invertir la velocidad horizontal
                    this.getDeltaMovement().y, // Mantener la velocidad vertical
                    zSpeed // Invertir la velocidad horizontal
            );
            this.setDeltaMovement(newMovement);
            this.move(MoverType.SELF, this.getDeltaMovement());
        }

        // Manejo del rebote vertical
        if (this.verticalCollision) {
            double verticalSpeed = -previousDeltaMovement.getY() * 0.3;

            // Asegura una velocidad mínima para que el rebote sea efectivo
            if (Math.abs(verticalSpeed) < 0.1) {
                verticalSpeed = -0.1;
            }

            Vec3 newMovement = new Vec3(
                    this.getDeltaMovement().x,   // Mantiene la velocidad horizontal
                    verticalSpeed,  // Aplica la velocidad vertical invertida
                    this.getDeltaMovement().z
            );
            this.setDeltaMovement(newMovement);
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else if (!wasVerticalCollision) {
            // Actualiza la velocidad anterior si no hubo colisión vertical
            previousDeltaMovement = new Vector(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
        }

        // Reducción de velocidad en el suelo
        if (this.onGround) {
            Vec3 reducedMovement = this.getDeltaMovement().multiply(0.9, 0.98, 0.9);
            this.setDeltaMovement(reducedMovement);
        }
    }
}