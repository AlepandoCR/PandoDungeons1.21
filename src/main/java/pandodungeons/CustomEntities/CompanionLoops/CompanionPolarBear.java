package pandodungeons.CustomEntities.CompanionLoops;

import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.PolarBear;
import pandodungeons.CustomEntities.Companions.Types.CompanionPolarBearBehavior;
import pandodungeons.Utils.CompanionUtils;
import pandodungeons.Utils.LocationUtils;

public class CompanionPolarBear extends Companion {
    private final CompanionPolarBearBehavior polarBear;
    private PolarBear craftPolarBear;

    public CompanionPolarBear(Player player) {
        super(player, CompanionUtils.getCompanionLevel(player, "oso"));
        polarBear = new CompanionPolarBearBehavior(EntityType.POLAR_BEAR, ((CraftWorld) player.getWorld()).getHandle(), plugin,((CraftPlayer) player).getHandle());
        editPolarBear(player);
        this.level = CompanionUtils.getCompanionLevel(player, "oso");
        setLivingEntity(polarBear.getBukkitLivingEntity());
        startCompanionLoop(this::runPolarBearLoop);
    }

    private void editPolarBear(Player player) {
        double health = 10 * level;
        if(health > 2048D){
            health = 2048D;
        }
        craftPolarBear = (PolarBear) polarBear.getBukkitEntity();
        craftPolarBear.setMaxHealth(health);
        craftPolarBear.setHealth(health);
        craftPolarBear.setRemoveWhenFarAway(false);
        craftPolarBear.setBaby();
        craftPolarBear.addScoreboardTag("companionMob");
        craftPolarBear.setCustomName(ChatColor.WHITE.toString() + ChatColor.BOLD + "Gertrudis " + ChatColor.AQUA + "lvl<" + level + ">");
        Bukkit.getLogger().info("Spawn companion PolarBear: " + ((CraftWorld) player.getLocation().getWorld()).getHandle().addFreshEntity(((CraftEntity) craftPolarBear).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM));
    }

    private void runPolarBearLoop() {
        if (craftPolarBear.isDead()) {
            craftPolarBear.remove();
            keepLooping = false;
            return;
        }

        if (!LocationUtils.hasActiveDungeon(playerCompanion.getUniqueId().toString()) && !playerCompanion.isOp()) {
            craftPolarBear.remove();
            keepLooping = false;
            return;
        }

        if (checkDistanceAndTeleport(craftPolarBear)) {
            keepLooping = false;
        }
    }
}
