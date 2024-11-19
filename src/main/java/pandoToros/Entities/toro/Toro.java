package pandoToros.Entities.toro;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import pandoToros.Entities.toro.behaviours.ChargePlayerGoal;

public class Toro extends Ravager {

    public Toro(EntityType<? extends Ravager> type, Level world) {
        super(type, world);
        this.registerGoals();
    }

    @Override
    protected void registerGoals() {
        // Agrega comportamientos personalizados
        this.goalSelector.addGoal(0, new FloatGoal(this)); // Evitar ahogarse
        this.goalSelector.addGoal(1, new ChargePlayerGoal(this, 2)); // Comportamiento de embestida
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F)); // Mira al jugador
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.4)); // Camina aleatoriamente

        // Objetivos de ataque
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // Otros métodos relevantes para personalizar comportamiento
    @Override
    public void aiStep() {
        super.aiStep();
        // Aquí puedes personalizar la lógica del toro durante cada tick
    }

    public static boolean summonToro(Location location) {
        World bukkitWorld = location.getWorld();

        if (!(bukkitWorld instanceof CraftWorld)) {
            Bukkit.getLogger().warning("The world must be a CraftWorld to summon custom entities.");
            return false;
        }

        // Convert Bukkit world to NMS world
        ServerLevel nmsWorld = ((CraftWorld) bukkitWorld).getHandle();

        // Create and configure the Toro
        Toro toro = new Toro(EntityType.RAVAGER, nmsWorld);
        toro.setPos(location.getX(), location.getY(), location.getZ());
        toro.setCustomName(net.minecraft.network.chat.Component.literal("Toro"));
        toro.setCustomNameVisible(true);

        // Add the Toro to the world
        return nmsWorld.addFreshEntity(toro);
    }
}
