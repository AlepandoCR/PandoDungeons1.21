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
        sniffer = new CompanionSnifferBehavior(EntityType.SNIFFER, ((CraftWorld) player.getWorld()).getHandle(), level);
        editSniffer(player);
        setLivingEntity(sniffer.getBukkitLivingEntity());
        startCompanionLoop(this::runSnifferLoop);
    }

    private void editSniffer(Player player) {
        double health = 20 * level;
        if(health > 2048D){
            health = 2048D;
        }
        craftSniffer = (Sniffer) sniffer.getBukkitLivingEntity();
        craftSniffer.setMaxHealth(health); // Ajusta la salud máxima del Sniffer
        craftSniffer.setHealth(health);
        craftSniffer.setRemoveWhenFarAway(false);
        craftSniffer.addScoreboardTag("companionMob");
        craftSniffer.setCustomName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Bahorrina " + ChatColor.GOLD + "lvl<" + level + ">");
        craftSniffer.setBaby();
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
