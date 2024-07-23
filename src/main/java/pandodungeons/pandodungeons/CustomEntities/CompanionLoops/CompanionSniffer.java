package pandodungeons.pandodungeons.CustomEntities.CompanionLoops;

import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sniffer;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pandodungeons.pandodungeons.CustomEntities.Companions.Types.CompanionSnifferBehavior;
import pandodungeons.pandodungeons.Utils.CompanionUtils;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class CompanionSniffer extends Companion {
    private final CompanionSnifferBehavior sniffer;
    private Sniffer craftSniffer;

    public CompanionSniffer(Player player) {
        super(player, CompanionUtils.getCompanionLevel(player, "sniffer"));
        sniffer = new CompanionSnifferBehavior(EntityType.SNIFFER, ((CraftWorld) player.getWorld()).getHandle());
        editSniffer(player);
        setLivingEntity(sniffer.getBukkitLivingEntity());
        startCompanionLoop(this::runSnifferLoop);
    }

    private void editSniffer(Player player) {
        craftSniffer = (Sniffer) sniffer.getBukkitLivingEntity();
        craftSniffer.setMaxHealth(20 * level); // Ajusta la salud máxima del Sniffer
        craftSniffer.setHealth(20 * level);
        craftSniffer.setRemoveWhenFarAway(false);
        craftSniffer.addScoreboardTag("companionMob");
        craftSniffer.setCustomName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Sifilis " + ChatColor.GOLD + "lvl<" + level + ">");
        Bukkit.getLogger().info("Spawn companion Sniffer: " + ((CraftWorld) player.getWorld()).getHandle().addFreshEntity(((CraftEntity) craftSniffer).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM));
    }

    private void runSnifferLoop() {
        if (craftSniffer.isDead()) {
            craftSniffer.remove();
            keepLooping = false;
            return;
        }

        if (!LocationUtils.hasActiveDungeon(playerCompanion.getUniqueId().toString()) && !playerCompanion.isOp()) {
            craftSniffer.remove();
            keepLooping = false;
            return;
        }

        if (checkDistanceAndTeleport(craftSniffer)) {
            keepLooping = false;
        }
    }
}
