package pandodungeons.pandodungeons.CustomEntities.CompanionLoops;

import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.pandodungeons.CustomEntities.Companions.Types.CompanionBreezeBehavior;
import pandodungeons.pandodungeons.Utils.CompanionUtils;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class CompanionBreeze extends Companion {
    private final CompanionBreezeBehavior breeze;
    private Mob craftBreeze;

    public CompanionBreeze(Player player) {
        super(player, CompanionUtils.getCompanionLevel(player, "breeze"));
        breeze = new CompanionBreezeBehavior(EntityType.BREEZE, ((CraftWorld) playerCompanion.getWorld()).getHandle());
        breeze.setOwnerUUID(player.getUniqueId());
        editBreeze(player);
        setLivingEntity((LivingEntity) breeze.getBukkitEntity());
        startCompanionLoop(this::runBreezeLoop);
    }

    private void editBreeze(Player player) {
        craftBreeze = (Mob) breeze.getBukkitEntity();
        craftBreeze.setMaxHealth(5 * level);
        craftBreeze.setHealth(5 * level);
        craftBreeze.setRemoveWhenFarAway(false);
        craftBreeze.addScoreboardTag("companionMob");
        craftBreeze.setCustomName(ChatColor.translateAlternateColorCodes('&', "&x&6&b&4&2&2&6&lJepeto "  + ChatColor.GOLD + "lvl<" + level + ">"));
        Bukkit.getLogger().info("Spawn companion Breeze: " + ((CraftWorld) player.getLocation().getWorld()).getHandle().addFreshEntity(((CraftEntity) craftBreeze).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM));
    }

    private void runBreezeLoop() {
        if (craftBreeze.isDead()) {
            craftBreeze.remove();
            keepLooping = false;
            return;
        }
        int speed = (int) (CompanionUtils.getCompanionLevel(playerCompanion, "breeze") * 0.5);
        playerCompanion.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, speed, false, false));

        if (!LocationUtils.hasActiveDungeon(playerCompanion.getUniqueId().toString()) && !playerCompanion.isOp()) {
            craftBreeze.remove();
            keepLooping = false;
            return;
        }

        if (checkDistanceAndTeleport(craftBreeze)) {
            keepLooping = false;
        }
    }
}
