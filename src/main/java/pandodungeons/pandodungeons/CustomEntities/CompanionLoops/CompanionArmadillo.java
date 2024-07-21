package pandodungeons.pandodungeons.CustomEntities.CompanionLoops;

import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.Armadillo;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.pandodungeons.CustomEntities.Companions.Types.CompanionArmadilloBehavior;
import pandodungeons.pandodungeons.Utils.CompanionUtils;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class CompanionArmadillo extends Companion {
    private final CompanionArmadilloBehavior armadillo;
    private Armadillo craftArmadillo;
    private int resistance;

    public CompanionArmadillo(Player player) {
        super(player, CompanionUtils.getCompanionLevel(player, "armadillo"));
        armadillo = new CompanionArmadilloBehavior(EntityType.ARMADILLO, ((CraftWorld) player.getWorld()).getHandle(), level);
        editArmadillo(player);
        level  = CompanionUtils.getCompanionLevel(player, "armadillo");
        resistance = (int) (level * 0.5);
        if(resistance < 1){
            resistance = 1;
        }
        if(resistance > 4){
            resistance = 4;
        }
        setLivingEntity(armadillo.getBukkitLivingEntity());
        startCompanionLoop(this::runArmadilloLoop);
    }

    private void editArmadillo(Player player) {
        craftArmadillo = (Armadillo) armadillo.getBukkitEntity();
        craftArmadillo.setMaxHealth(10 * level);
        craftArmadillo.setHealth(10 * level);
        craftArmadillo.setRemoveWhenFarAway(false);
        craftArmadillo.addScoreboardTag("companionMob");
        craftArmadillo.setCustomName(ChatColor.WHITE.toString() + ChatColor.BOLD + "Armandillo " + ChatColor.GREEN + "lvl<" + level + ">");
        Bukkit.getLogger().info("Spawn companion Armadillo: " + ((CraftWorld) player.getLocation().getWorld()).getHandle().addFreshEntity(((CraftEntity) craftArmadillo).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM));
    }

    private void runArmadilloLoop() {
        if (craftArmadillo.isDead()) {
            craftArmadillo.remove();
            keepLooping = false;
            return;
        }
        playerCompanion.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, resistance, false, false));
        if(((net.minecraft.world.entity.animal.armadillo.Armadillo)((CraftEntity) craftArmadillo).getHandle()).isScared()){
            if(craftArmadillo.isVisualFire()){
                craftArmadillo.setVisualFire(false);
                craftArmadillo.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20, resistance, false, false));
            }
            craftArmadillo.setCustomName(ChatColor.WHITE.toString() + ChatColor.BOLD + "Armandillo " + ChatColor.GREEN + "lvl<" + level + ">" + ChatColor.RESET + ChatColor.DARK_GRAY + ChatColor.BOLD + " 🔒");
            craftArmadillo.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, resistance, false, false));
        }else if(!craftArmadillo.getCustomName().equals(ChatColor.WHITE.toString() + ChatColor.BOLD + "Armandillo " + ChatColor.GREEN + "lvl<" + level + ">")){
            craftArmadillo.setCustomName(ChatColor.WHITE.toString() + ChatColor.BOLD + "Armandillo " + ChatColor.GREEN + "lvl<" + level + ">");
        }

        if (!LocationUtils.hasActiveDungeon(playerCompanion.getUniqueId().toString()) && !playerCompanion.isOp()) {
            craftArmadillo.remove();
            keepLooping = false;
            return;
        }

        if (checkDistanceAndTeleport(craftArmadillo)) {
            keepLooping = false;
        }
    }
}
