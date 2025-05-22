package pandodungeons.CustomEntities.Companions.PolarBear;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.monster.Monster;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.PandoDungeons;

import java.util.EnumSet;
import java.util.List;

public class FreezeNearbyEntitiesGoal extends Goal {
    private final PolarBear polarBear;
    private Plugin plugin;
    private static final int FREEZE_DURATION = 100; // ticks, 5 segundos

    public FreezeNearbyEntitiesGoal(PolarBear polarBear, Plugin plugin) {
        this.polarBear = polarBear;
        this.plugin = plugin;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        // No additional cleanup required
    }

    @Override
    public void tick() {
        // Verifica las entidades cercanas y congélalas
        List<LivingEntity> nearbyEntities = polarBear.level().getEntitiesOfClass(LivingEntity.class, polarBear.getBoundingBox().inflate(3.0, 3.0, 3.0));
        for (LivingEntity entity : nearbyEntities) {
            if (entity instanceof Monster) {
                if (plugin == null) {
                    plugin = JavaPlugin.getPlugin(PandoDungeons.class);
                }
                if(!entity.getBukkitLivingEntity().getEquipment().getHelmet().getType().equals(Material.ICE)){
                    freezeTarget(entity);
                }
            }else{
            }
        }
    }

    private void freezeTarget(LivingEntity target) {
        if (target == null) {
            return;
        }

        CraftLivingEntity craftTarget = (CraftLivingEntity) target.getBukkitEntity();
        ItemStack originalHeadItem = craftTarget.getEquipment().getHelmet();

        // Añade el efecto de poción de lentitud
        craftTarget.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, FREEZE_DURATION, 4));

        // Coloca un bloque de hielo en la cabeza del objetivo
        craftTarget.getEquipment().setHelmet(new org.bukkit.inventory.ItemStack(org.bukkit.Material.ICE));

        // Programa una tarea para quitar el bloque de hielo y restaurar el item original
        new BukkitRunnable() {
            @Override
            public void run() {
                // Restaura el item original de la cabeza
                craftTarget.getEquipment().setHelmet(originalHeadItem);
            }
        }.runTaskLater(plugin, FREEZE_DURATION);
    }
}
