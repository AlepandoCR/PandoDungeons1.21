package pandodungeons.CustomEntities.pandaMount;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CustomPanda extends Panda {

    private static final double JUMP_STRENGTH = 1; // Adjust this value to ensure the panda can jump over a full block
    private static final double GRAVITY = -1;
    private static net.minecraft.world.entity.player.Player owner;

    public CustomPanda(EntityType<? extends Panda> type, ServerLevel world, Player player) {
        super(type, world);
        this.setNoAi(false); // Enable AI so it can be controlled
        owner = ((CraftPlayer) player).getHandle();
        spawn(player);
    }

    public void spawn(Player player) {
        CraftEntity entity = this.getBukkitLivingEntity();
        entity.addScoreboardTag("pandaMount");
        Bukkit.getLogger().info("Spawn Panda: " + ((CraftWorld) player.getLocation().getWorld()).getHandle().addFreshEntity(entity.getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM));
        entity.teleport(player.getLocation());
    }

    // Method to mount the panda
    public void setRider(Player player) {
        // Convert Bukkit Player to NMS ServerPlayer
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.startRiding(this, true);
    }

    // Static method to mount the panda
    public static void setPandaRider(Player player, LivingEntity panda) {
        if(!isPandaMount(panda)){
            return;
        }
        // Convert Bukkit Player to NMS ServerPlayer
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.startRiding(((CraftEntity) panda).getHandle(), true);
    }

    public static boolean isPandaMount(LivingEntity entity){
        return entity.getScoreboardTags().contains("pandaMount");
    }

    public static Vec3 fromYaw(float yaw) {
        // Convert yaw to radians
        double yawRad = Math.toRadians(yaw);

        // Calculate vector components
        double x = -Math.sin(yawRad);
        double z = Math.cos(yawRad);

        return new Vec3(x, 0, z); // Only move horizontally, keep y (vertical) as 0
    }

    @Override
    public void tick() {
        super.tick();

        if(this.distanceTo(owner) > 15){
            this.getBukkitLivingEntity().remove();
        }

        if(owner.getBukkitEntity().getItemInHand().getType().equals(Material.BAMBOO)){
            return;
        }

        if (this.getFirstPassenger() != null && this.getFirstPassenger() instanceof net.minecraft.world.entity.player.Player human) {
            // Control panda movement based on player input
            float yaw = human.getBukkitYaw();
            Vec3 direction = fromYaw(yaw);

            // Update panda's look direction to face the movement direction
            this.setYRot(yaw);

            // Apply movement vector
            this.setDeltaMovement(direction.scale(0.25)); // Adjust movement speed

            // Handle jumping over obstacles
            if(this.isInWater() || this.isUnderWater()){
                this.setSwimming(true);
                this.playSwimSound(0.05F);
                this.setDeltaMovement(this.getDeltaMovement().add(0,0.04,0));
            }else{
                this.setSwimming(false);
            }
            if (this.horizontalCollision && this.onGround) {
                // Ensure the panda jumps with enough force to clear a block
                this.setDeltaMovement(this.getDeltaMovement().add(0, JUMP_STRENGTH, 0));
            } else if (!this.onGround && !this.isSwimming()) {
                // Apply gravity if the panda is in the air
                this.setDeltaMovement(this.getDeltaMovement().add(0, GRAVITY, 0));
            }
        }
    }
}
