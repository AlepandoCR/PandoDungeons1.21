package pandodungeons.CustomEntities.CompanionLoops;

import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pandodungeons.CustomEntities.Companions.Types.CompanionAllayBehavior;
import pandodungeons.Utils.CompanionUtils;
import pandodungeons.Utils.LocationUtils;

public class CompanionAllay extends Companion {
    private final CompanionAllayBehavior allay;
    private Allay craftAllay;

    public CompanionAllay(Player player) {
        super(player, CompanionUtils.getCompanionLevel(player, "allay"));
        allay = new CompanionAllayBehavior(EntityType.ALLAY, (playerCompanion.getWorld()), ((CraftPlayer) playerCompanion).getHandle());
        editAllay(player);
        setLivingEntity(allay.getBukkitLivingEntity());
        startCompanionLoop(this::runAllayLoop);
    }

    private void editAllay(Player player) {
        double health = 5 * level;
        if(health > 2048D){
            health = 2048D;
        }
        craftAllay = (Allay) allay.getBukkitLivingEntity();
        craftAllay.setMaxHealth(health);
        craftAllay.setHealth(health);
        craftAllay.setRemoveWhenFarAway(false);
        craftAllay.addScoreboardTag("companionMob");
        craftAllay.setCustomName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Trini " + ChatColor.GOLD + "lvl<" + level + ">");
        Bukkit.getLogger().info("Spawn companion Allay: " + ((CraftWorld) player.getLocation().getWorld()).getHandle().addFreshEntity(((CraftEntity) craftAllay).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM));
    }

    private void runAllayLoop() {
        if (craftAllay.isDead()) {
            craftAllay.remove();
            keepLooping = false;
            return;
        }

        if (!LocationUtils.hasActiveDungeon(playerCompanion.getUniqueId().toString()) && !playerCompanion.isOp()) {
            craftAllay.remove();
            keepLooping = false;
            return;
        }

        if (checkDistanceAndTeleport(craftAllay)) {
            keepLooping = false;
        }
    }
}
