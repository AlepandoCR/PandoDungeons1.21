package pandodungeons.CustomEntities.CompanionLoops;

import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.PufferFish;
import pandodungeons.CustomEntities.Companions.Types.CompanionPufferfishBehavior;
import pandodungeons.Utils.CompanionUtils;
import pandodungeons.Utils.LocationUtils;

public class CompanionPufferfish extends Companion {
    private final CompanionPufferfishBehavior pufferfish;
    private PufferFish craftPufferfish;
    private int stateClock = 0;

    public CompanionPufferfish(Player player) {
        super(player, CompanionUtils.getCompanionLevel(player, "pufferfish"));
        pufferfish = new CompanionPufferfishBehavior(EntityType.PUFFERFISH, ((CraftWorld) player.getWorld()).getHandle(), level);
        editPufferfish(player);
        level = CompanionUtils.getCompanionLevel(player, "pufferfish");
        setLivingEntity(pufferfish.getBukkitLivingEntity());
        startCompanionLoop(this::runPufferfishLoop);
    }

    private void editPufferfish(Player player) {
        double health = 7 * level;
        if (health > 2048D) {
            health = 2048D;
        }
        craftPufferfish = (PufferFish) pufferfish.getBukkitEntity();
        craftPufferfish.setMaxHealth(health);
        craftPufferfish.setHealth(health);
        craftPufferfish.setRemoveWhenFarAway(false);
        craftPufferfish.setSilent(true);
        craftPufferfish.addScoreboardTag("companionMob");
        craftPufferfish.setCustomName(ChatColor.WHITE.toString() + ChatColor.BOLD + "Globerto García " + ChatColor.GREEN + "lvl<" + level + ">");
        Bukkit.getLogger().info("Spawn companion Pufferfish: " + ((CraftWorld) player.getLocation().getWorld()).getHandle().addFreshEntity(((CraftEntity) craftPufferfish).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM));
    }

    private void runPufferfishLoop() {

        craftPufferfish.setPuffState(1);

        craftPufferfish.setFallDistance(0);

        craftPufferfish.setRemainingAir(1000);

        stateClock++;

        if (craftPufferfish.isDead()) {
            craftPufferfish.remove();
            keepLooping = false;
            return;
        }

        if (craftPufferfish.getTarget() != null) {
            craftPufferfish.setCustomName(ChatColor.WHITE.toString() + ChatColor.BOLD + "Globerto García "  + ChatColor.GREEN + "lvl<" + level + ">" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + " ⚔");
        }else{
            craftPufferfish.setCustomName(ChatColor.WHITE.toString() + ChatColor.BOLD + "Globerto García "  + ChatColor.GREEN + "lvl<" + level + ">");
        }

        if (!LocationUtils.hasActiveDungeon(playerCompanion.getUniqueId().toString()) && !playerCompanion.isOp()) {
            craftPufferfish.remove();
            keepLooping = false;
            return;
        }

        if (checkDistanceAndTeleport(craftPufferfish)) {
            keepLooping = false;
        }
    }
}
