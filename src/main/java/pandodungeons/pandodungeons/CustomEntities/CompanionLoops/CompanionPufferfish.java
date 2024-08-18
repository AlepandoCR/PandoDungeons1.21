package pandodungeons.pandodungeons.CustomEntities.CompanionLoops;

import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.PufferFish;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.pandodungeons.CustomEntities.CompanionLoops.Companion;
import pandodungeons.pandodungeons.CustomEntities.Companions.Types.CompanionPufferfishBehavior;
import pandodungeons.pandodungeons.Utils.CompanionUtils;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class CompanionPufferfish extends Companion {
    private final CompanionPufferfishBehavior pufferfish;
    private PufferFish craftPufferfish;

    public CompanionPufferfish(Player player) {
        super(player, CompanionUtils.getCompanionLevel(player, "pufferfish"));
        pufferfish = new CompanionPufferfishBehavior(EntityType.PUFFERFISH, ((CraftWorld) player.getWorld()).getHandle(), level);
        editPufferfish(player);
        level = CompanionUtils.getCompanionLevel(player, "pufferfish");
        setLivingEntity(pufferfish.getBukkitLivingEntity());
        startCompanionLoop(this::runPufferfishLoop);
    }

    private void editPufferfish(Player player) {
        double health=6 * level;
        if (health > 2048D) {
            health = 2048D;
        }
        craftPufferfish = (PufferFish) pufferfish.getBukkitEntity();
        craftPufferfish.setMaxHealth(health);
        craftPufferfish.setHealth(health);
        craftPufferfish.setRemoveWhenFarAway(false);
        craftPufferfish.addScoreboardTag("companionMob");
        craftPufferfish.setCustomName(ChatColor.WHITE.toString() + ChatColor.BOLD + "Pufferfish " + ChatColor.GREEN + "lvl<" + level + ">");
        Bukkit.getLogger().info("Spawn companion Pufferfish: " + ((CraftWorld) player.getLocation().getWorld()).getHandle().addFreshEntity(((CraftEntity) craftPufferfish).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM));
    }

    private void runPufferfishLoop() {
        if (craftPufferfish.isDead()) {
            craftPufferfish.remove();
            keepLooping = false;
            return;
        }

        if (craftPufferfish.getPuffState() > 0) {
            craftPufferfish.setCustomName(ChatColor.WHITE.toString() + ChatColor.BOLD + "Pufferfish " + ChatColor.GREEN + "lvl<" + level + ">" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + " ⚔");
        } else if (!craftPufferfish.getCustomName().equals(ChatColor.WHITE.toString() + ChatColor.BOLD + "Pufferfish " + ChatColor.GREEN + "lvl<" + level + ">")) {
            craftPufferfish.setCustomName(ChatColor.WHITE.toString() + ChatColor.BOLD + "Pufferfish " + ChatColor.GREEN + "lvl<" + level + ">");
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
