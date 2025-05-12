package pandodungeons.pandodungeons.bossfights.bossEntities.triton.entities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.pandodungeons.Game.Stats;
import pandodungeons.pandodungeons.Utils.LocationUtils;

public class Triton {
    private Drowned drowned;
    int targetPrestige;
    public Triton(JavaPlugin plugin, Location location) {
        Player target = LocationUtils.findNearestPlayer(location.getWorld(), location);
        Stats targetStats = Stats.fromPlayer(target);
        targetPrestige = targetStats.prestige();
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("La ubicación proporcionada no es válida");
        }
        double dmg = (1 + (double) targetPrestige / 2);
        int intDmg = (int) dmg;
        PotionEffect strengthEffect = new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, intDmg, false, false, false);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            drowned = world.spawn(location, Drowned.class, (drowned) -> {
                drowned.getEquipment().setItemInMainHand(createTridentWithImpaling());
                drowned.setTarget(target);
                drowned.addScoreboardTag("triton");
                drowned.addScoreboardTag("bossMob");
                if(targetPrestige < 1){
                    drowned.setCustomName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Triton");
                }else {
                    drowned.setCustomName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Triton" + ChatColor.RED + " Prestigio <" + targetPrestige + ">");
                }
                double baseHealth = 500d * (1 + ((double) targetPrestige / 2));
                drowned.addPotionEffect(strengthEffect);
                if(baseHealth > 2048D){
                    baseHealth = 2048;
                }
                drowned.setMaxHealth(baseHealth);
                drowned.setHealth(baseHealth);
                drowned.setAdult();
            });
        });
    }

    public Drowned getDrowned() {
        return drowned;
    }

    public static ItemStack createTridentWithImpaling() {
        // Crear el ItemStack del tridente
        ItemStack trident = new ItemStack(Material.TRIDENT);

        // Obtener el meta del item para agregar encantamientos
        ItemMeta meta = trident.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.IMPALING, 3, true);
            trident.setItemMeta(meta);
        }

        return trident;
    }

    public static ItemStack createIronSwordWithSharpness() {
        ItemStack ironSword = new ItemStack(Material.IRON_SWORD);

        ItemMeta meta = ironSword.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.SHARPNESS, 4, true);

            ironSword.setItemMeta(meta);
        }

        return ironSword;
    }

    public void setDrownedTarget(Location location) {
        if (drowned != null) {
            drowned.setTarget(LocationUtils.findNearestPlayer(location.getWorld(), location));
        }
    }
}
