package pandoToros.Entities.toro.behaviours;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Math;
import pandoToros.Entities.toro.Toro;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static pandoToros.utils.PlayerArmorChecker.hasArmor;

public class ChargePlayerGoal extends Goal {
    private final Ravager toro;
    private double speedModifier;
    private Player target;
    private int stareTicks; // Ticks for staring
    private boolean charging; // Indicates if the toro is currently charging
    private Vec3 chargeDirection; // Current charging direction
    private boolean preChargeNoise;
    private int cooldownTicks; // Cooldown period for the charge
    private int anger;
    private Vec3 calculatedPosition; // Strategic position during cooldown
    PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    public ChargePlayerGoal(Ravager toro, double speedModifier) {
        this.toro = toro;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.target = this.toro.level().getNearestPlayer(this.toro, 70.0);
        return this.target != null;
    }

    @Override
    public void start() {
        this.stareTicks = 40; // Stare for 2 seconds (40 ticks)
        this.charging = false;
        this.preChargeNoise = false;
        this.cooldownTicks = 0;
        this.anger = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target.isAlive();
    }

    @Override
    public void tick() {
        if(this.toro.level().getNearestPlayer(this.toro,3.0) != null){
            Objects.requireNonNull(this.toro.level().getNearestPlayer(this.toro, 3.0)).hurt(this.toro.damageSources().mobAttack(this.toro),7f);
        }
        if (this.target == null) return;

        this.toro.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        if (cooldownTicks > 0) {
            // Handle cooldown behavior
            cooldownTicks--;
            if (calculatedPosition == null) {
                calculatedPosition = calculateStrategicPosition();
            }
            if (calculatedPosition != null) {
                this.toro.getNavigation().moveTo(
                        calculatedPosition.x,
                        calculatedPosition.y,
                        calculatedPosition.z,
                        this.speedModifier - 0.5
                );
            }
            return;
        }

        if (this.stareTicks > 0) {
            // Staring phase
            if (!preChargeNoise) {
                for (org.bukkit.entity.Player player : this.toro.getBukkitEntity().getLocation().getNearbyPlayers(20)) {
                    player.playSound(this.toro.getBukkitEntity(), Sound.ENTITY_HORSE_ANGRY, 10, 0.4f);
                }
                preChargeNoise = true;
            }
            this.stareTicks--;
        } else if (!charging) {
            // Start charging after staring
            this.charging = true;
            this.target = this.toro.level().getNearestPlayer(this.toro, 70.0);
            if(this.target != null){
                this.chargeDirection = this.target.position().subtract(this.toro.position()).normalize();
                this.toro.getNavigation().moveTo(
                        this.toro.getX() + this.chargeDirection.x * 70, // Longer charge distance
                        this.toro.getY(),
                        this.toro.getZ() + this.chargeDirection.z * 70,
                        this.speedModifier
                );

                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if(target == null){
                            this.cancel();
                            return;
                        }
                        chargeDirection = target.position().subtract(toro.position()).normalize();
                        toro.getNavigation().moveTo(
                                toro.getX() + chargeDirection.x * 60, // Longer charge distance
                                toro.getY(),
                                toro.getZ() + chargeDirection.z * 60,
                                speedModifier
                        );
                    }
                }.runTaskLater(plugin,20);
            }
        } else {
            // Check for obstacles in a 3x3 area
            Vec3 nextPosition = this.toro.position().add(this.chargeDirection.scale(0.5)); // Step forward
            BlockPos centerPos = new BlockPos(
                    (int) Math.floor(nextPosition.x),
                    (int) Math.floor(nextPosition.y),
                    (int) Math.floor(nextPosition.z)
            );

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = 0; dy <= 2; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos blockPos = centerPos.offset(dx, dy, dz);
                        if (!this.toro.level().getBlockState(blockPos).isAir()) {
                            this.toro.level().destroyBlock(blockPos, false);
                        }
                    }
                }
            }
            // Impact phase or charge miss
            if (this.toro.distanceTo(this.target) <= 4.0) {
                for (org.bukkit.entity.Player player : this.toro.getBukkitEntity().getLocation().getNearbyPlayers(20)) {
                    player.playSound(this.toro.getBukkitEntity(), Sound.ENTITY_HORSE_BREATHE, 10, 0.7f);
                }
                Vec3 knockbackDirection = calculateKnockbackDirection(this.toro, this.target);
                if(hasArmor((org.bukkit.entity.Player) this.target.getBukkitEntity(),false)){
                    this.target.hurt(this.toro.damageSources().mobAttack(this.toro), 10.0F);
                } else {
                    this.target.hurt(this.toro.damageSources().mobAttack(this.toro), 7.0F);
                }
                this.target.push(knockbackDirection.x, 1.0, knockbackDirection.z);
                this.resetCharge();
                anger++;
            } else if (this.toro.getNavigation().isDone()) {
                this.resetCharge();
                this.dodgeMessage(this.target);
                anger++;
            }
        }
    }

    private void dodgeMessage(Player player){
        org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) player.getBukkitEntity();
        bukkitPlayer.playSound(bukkitPlayer, Sound.BLOCK_NOTE_BLOCK_BELL,1,1.2f);
    }

    @Override
    public void stop() {
        this.toro.getNavigation().stop();
        this.target = null;
        this.charging = false;
        this.cooldownTicks = 0;
        this.calculatedPosition = null;
    }

    private void resetCharge() {
        this.target = this.toro.level().getNearestPlayer(this.toro, 70.0);
        this.cooldownTicks = java.lang.Math.max((40 - (anger * 10)), 0);
       // 3 seconds cooldown (60 ticks)
        this.stareTicks = 40; // Restart staring phase
        this.charging = false;
        this.preChargeNoise = false;
        this.speedModifier += ((double) anger /100);
        this.calculatedPosition = null;
    }

    private Vec3 calculateStrategicPosition() {
        // Obtener jugadores en un radio de 40 bloques
        List<Player> players = this.toro.level().getEntitiesOfClass(Player.class, this.toro.getBoundingBox().inflate(20));

        // Calcular jugadores que estén a menos de 10 bloques
        List<Player> tooClosePlayers = players.stream()
                .filter(player -> this.toro.distanceTo(player) < 10)
                .toList();

        if (!tooClosePlayers.isEmpty()) {
            // Si hay jugadores demasiado cerca, encontrar una posición alejada
            Vec3 toroPosition = this.toro.position();
            Vec3 escapeDirection = tooClosePlayers.stream()
                    .map(player -> toroPosition.subtract(player.position()).normalize())
                    .reduce(Vec3::add)
                    .orElse(Vec3.ZERO) // En caso de que no haya direcciones válidas
                    .normalize();

            // Mover el toro 15 bloques en la dirección de escape
            return toroPosition.add(escapeDirection.scale(15));
        }

        // Filtrar jugadores que estén a una distancia válida (entre 10 y 30 bloques)
        players = players.stream()
                .filter(player -> {
                    double distance = this.toro.distanceTo(player);
                    return distance >= 10 && distance <= 30;
                })
                .toList();

        // Si no hay jugadores válidos, devolver null
        if (players.isEmpty()) return null;

        // Calcular posición promedio de los jugadores válidos
        List<Player> finalPlayers = players;
        Optional<Vec3> position = players.stream()
                .map(Player::position)
                .reduce(Vec3::add)
                .map(sum -> sum.scale(1.0 / finalPlayers.size()));

        return position.orElse(null);
    }



    private Vec3 calculateKnockbackDirection(Ravager toro, Player target) {
        Vec3 toroToPlayer = target.position().subtract(toro.position()).normalize();
        Vec3 perpendicular = new Vec3(-toroToPlayer.z, 0, toroToPlayer.x);
        double side = toroToPlayer.cross(new Vec3(0, 1, 0)).y;
        return toroToPlayer.add(perpendicular.scale(side < 0 ? -0.5 : 0.5)).normalize();
    }
}
