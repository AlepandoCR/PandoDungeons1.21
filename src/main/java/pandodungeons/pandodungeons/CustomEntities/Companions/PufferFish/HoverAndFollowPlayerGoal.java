package pandodungeons.pandodungeons.CustomEntities.Companions.PufferFish;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Pufferfish;

import java.util.EnumSet;

public class HoverAndFollowPlayerGoal extends Goal {
    private final Pufferfish pufferfish;
    private ServerPlayer targetPlayer;
    private final double speedModifier;
    private final float stopDistance;
    private final float startDistance;
    private final double hoverHeight;
    private double orbitAngle;
    private final double maxHeightOffset;

    public HoverAndFollowPlayerGoal(Pufferfish pufferfish, double speedModifier, float startDistance, float stopDistance, double hoverHeight) {
        this.pufferfish = pufferfish;
        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.hoverHeight = hoverHeight;
        this.orbitAngle = 0;
        this.maxHeightOffset = 3.0; // Limitar la altura máxima a 3 bloques por encima del jugador
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.targetPlayer = (ServerPlayer) this.pufferfish.level().getNearestPlayer(this.pufferfish, startDistance);
        return this.targetPlayer != null && this.pufferfish.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPlayer != null && this.pufferfish.distanceTo(this.targetPlayer) > stopDistance;
    }

    @Override
    public void start() {
        this.pufferfish.setNoGravity(true); // Desactiva la gravedad para el vuelo
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.pufferfish.setNoGravity(false); // Restaura la gravedad al detener el movimiento
    }

    @Override
    public void tick() {
        if (this.targetPlayer != null && this.targetPlayer.isAlive()) {
            double distanceToPlayer = this.pufferfish.distanceTo(this.targetPlayer);

            if (distanceToPlayer > stopDistance) {
                // Incrementar el ángulo de órbita lentamente para un movimiento más suave
                orbitAngle += 0.03; // Reducción del incremento para que la órbita sea más lenta y natural

                // Calcular la posición de destino para orbitar alrededor del jugador
                double orbitRadius = startDistance; // Radio de la órbita
                double targetX = this.targetPlayer.getX() + orbitRadius * Math.cos(orbitAngle);
                double targetZ = this.targetPlayer.getZ() + orbitRadius * Math.sin(orbitAngle);

                // Limitar la altura del Pufferfish para que no suba indefinidamente
                double targetY = Math.min(this.targetPlayer.getY() + hoverHeight, this.targetPlayer.getY() + maxHeightOffset);

                // Moverse hacia la posición de destino
                double deltaX = targetX - this.pufferfish.getX();
                double deltaY = targetY - this.pufferfish.getY();
                double deltaZ = targetZ - this.pufferfish.getZ();
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                if (distance > 0.1) { // Evitar pequeños movimientos innecesarios
                    double moveX = deltaX / distance * speedModifier * 0.5; // Reducir la velocidad para movimientos más suaves
                    double moveY = deltaY / distance * speedModifier * 0.5;
                    double moveZ = deltaZ / distance * speedModifier * 0.5;
                    this.pufferfish.setDeltaMovement(moveX, moveY, moveZ);
                }
            } else {
                // Levitar en su lugar si está lo suficientemente cerca del jugador
                this.pufferfish.setDeltaMovement(0, 0, 0);
            }

            // Mirar al jugador
            this.pufferfish.getLookControl().setLookAt(this.targetPlayer, 10.0F, 10.0F);
        } else {
            stop(); // Detener si el jugador ya no está vivo
        }
    }
}
