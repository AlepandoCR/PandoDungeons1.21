package pandodungeons.pandodungeons.CustomEntities.CompanionLoops;

import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pandodungeons.pandodungeons.CustomEntities.Companions.Types.CompanionAllayBehavior;
import pandodungeons.pandodungeons.Utils.CompanionUtils;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class CompanionAllay extends Companion {
    private final CompanionAllayBehavior allay;
    private Allay craftAllay;

    public CompanionAllay(Player player) {
        super(player, CompanionUtils.getCompanionLevel(player, "allay"));
        allay = new CompanionAllayBehavior(EntityType.ALLAY, (playerCompanion.getWorld()), ((CraftPlayer) playerCompanion).getHandle());
        editAllay(player);
        setLivingEntity(allay.getBukkitCreature());
        startCompanionLoop(this::runAllayLoop);
    }

    private void editAllay(Player player) {
        craftAllay = (Allay) allay.getBukkitCreature();
        craftAllay.setMaxHealth(5 * level);
        craftAllay.setHealth(5 * level);
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
