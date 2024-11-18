package pandodungeons.pandodungeons.Utils;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.pandodungeons.Game.Stats;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class MobSpawnUtils {

    private static final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    private static final Random random = new Random();

    public static void spawnMobs(Location location, Material blockType, World world) {
        int playerLvl = 0;
        String worldName = location.getWorld().getName().toLowerCase(Locale.ROOT);

        PlayerParty party = null;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (worldName.contains(player.getName().toLowerCase(Locale.ROOT))) {
                playerLvl = Stats.fromPlayer(player).getDungeonLevel();
                if(plugin.playerPartyList.isOwner(player)){
                    party = plugin.playerPartyList.getPartyByOwner(player);
                }
                break;
            }
        }

        int minMobs;
        int maxMobs;


        if (playerLvl < 2) {
            minMobs = 1;
            maxMobs = 2;
        } else if (playerLvl <= 4) {
            minMobs = 3;
            maxMobs = 5;
        } else {
            minMobs = 5;
            maxMobs = 7;
        }
        if(party != null){
            maxMobs += 2 + party.getMembers().size();
            if(maxMobs > 15){
                maxMobs = 15;
            }
        }

        location.setY(location.getY() + 1);
        int numMobs = minMobs + random.nextInt(maxMobs - minMobs + 1);

        switch (blockType) {
            case IRON_BLOCK -> spawnZombies(location, numMobs, world);
            case REDSTONE_BLOCK -> spawnSkeletons(location, numMobs, world);
            case COAL_BLOCK -> spawnWitherSkeletons(location, numMobs, world);
            case EMERALD_BLOCK -> spawnPillagersAndVindicators(location, numMobs, world);
            case GOLD_BLOCK -> spawnHuzk(location, numMobs, world);
            case DIAMOND_BLOCK -> spawnStray(location, numMobs, world);
            case MAGENTA_GLAZED_TERRACOTTA -> spawnBlaze(location, numMobs, world);
            case BLACK_GLAZED_TERRACOTTA -> spawnPiglinsAndBrutes(location, numMobs, world);
            case GRAY_GLAZED_TERRACOTTA -> spawnSpider(location, numMobs, world);
            case PURPLE_GLAZED_TERRACOTTA -> spawnBruja(location, numMobs, world);
            default -> {
            }
        }
    }


    private static void spawnPiglinsAndBrutes(Location location, int numMobs, World world) {
        for (int i = 0; i < numMobs; i++) {
            if (random.nextBoolean()) {
                Piglin piglin = (Piglin) world.spawnEntity(location, EntityType.PIGLIN);
                piglin.setImmuneToZombification(true);
                customizeMob(piglin);
            } else {
                PiglinBrute brute = (PiglinBrute) world.spawnEntity(location, EntityType.PIGLIN_BRUTE);
                brute.setImmuneToZombification(true);
                customizeMob(brute);
            }
        }
    }
    private static void spawnBruja(Location location, int numMobs, World world) {
        for (int i = 0; i < numMobs; i++) {
            Witch witch = (Witch) world.spawnEntity(location, EntityType.WITCH);
            customizeMob(witch);
        }
    }
    private static void spawnSpider(Location location, int numMobs, World world) {
        for (int i = 0; i < numMobs; i++) {
            Spider spider = (Spider) world.spawnEntity(location, EntityType.SPIDER);
            customizeMob(spider);
        }
    }
    private static void spawnBlaze(Location location, int numMobs, World world) {
        for (int i = 0; i < numMobs; i++) {
            Blaze blaze = (Blaze) world.spawnEntity(location, EntityType.BLAZE);
            customizeMob(blaze);
        }
    }
    private static void spawnStray(Location location, int numMobs, World world) {
        for (int i = 0; i < numMobs; i++) {
            Stray stray = (Stray) world.spawnEntity(location, EntityType.STRAY);
            customizeMob(stray);
        }
    }
    private static void spawnZombies(Location location, int numMobs, World world) {
        for (int i = 0; i < numMobs; i++) {
            Zombie zombie = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);
            customizeMob(zombie);
        }
    }
    private static void spawnHuzk(Location location, int numMobs, World world) {
        for (int i = 0; i < numMobs; i++) {
            Husk husk = (Husk) world.spawnEntity(location, EntityType.HUSK);
            customizeMob(husk);
        }
    }
    public static boolean areHostileMobsCleared(Location location) {
        World world = location.getWorld();
        if (world == null) {
            Bukkit.getLogger().warning("No se encontró mundo a la hora de verificar la existencia de mobs hostiles");
            return false;
        }

        int searchRadius = 50;

        for (Entity entity : world.getNearbyEntities(location, searchRadius, searchRadius, searchRadius)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player) && entity.getType() != EntityType.ARMOR_STAND) {
                if (isHostileMob((LivingEntity) entity)) {
                    Player nearestPlayer = LocationUtils.findNearestPlayer(location.getWorld(), location);
                    String playerName = nearestPlayer != null ? nearestPlayer.getName() : "unknown";
                    Bukkit.getLogger().info("Se encontró un mob hostil: " + entity.getType() + " en la dungeons de: " + playerName);
                    return false; // Si hay un mob hostil cerca, retornar falso
                }
            }
        }

        return true; // No se encontraron mobs hostiles cerca
    }

    private static boolean isHostileMob(LivingEntity entity) {
        EntityType type = entity.getType();

        if(entity.getScoreboardTags().contains("companionMob")){
            return false;
        }

        // Comprobación para encontrar una abeja con la etiqueta "bossMob" (cualquier mob de una boss fight)
        if (entity.getScoreboardTags().contains("bossMob")) {
            return true;
        }

        // Crear un conjunto de EntityTypes hostiles para una búsqueda más eficiente
        Set<EntityType> hostileTypes = Set.of(
                EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
                EntityType.SPIDER, EntityType.WITCH, EntityType.ENDERMAN,
                EntityType.WITHER_SKELETON, EntityType.PILLAGER, EntityType.VINDICATOR,
                EntityType.BLAZE, EntityType.HUSK, EntityType.STRAY,
                EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.EVOKER,
                EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN, EntityType.HOGLIN,
                EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOGLIN, EntityType.SHULKER,
                EntityType.WITHER, EntityType.DROWNED, EntityType.GHAST,
                EntityType.MAGMA_CUBE, EntityType.RAVAGER, EntityType.SLIME, EntityType.VEX
        );

        return hostileTypes.contains(type);
    }




    private static void spawnSkeletons(Location location, int numMobs, World world) {
        for (int i = 0; i < numMobs; i++) {
            Skeleton skeleton = (Skeleton) world.spawnEntity(location, EntityType.SKELETON);
            customizeMob(skeleton);
        }
    }

    private static void spawnWitherSkeletons(Location location, int numMobs, World world) {
        for (int i = 0; i < numMobs; i++) {
            WitherSkeleton witherSkeleton = (WitherSkeleton) world.spawnEntity(location, EntityType.WITHER_SKELETON);
            customizeMob(witherSkeleton);
        }
    }

    private static void spawnPillagersAndVindicators(Location location, int numMobs, World world) {
        for (int i = 0; i < numMobs; i++) {
            if (random.nextBoolean()) {
                Pillager pillager = (Pillager) world.spawnEntity(location, EntityType.PILLAGER);
                pillager.setPatrolLeader(false);
                customizeMob(pillager);
            } else {
                Vindicator vindicator = (Vindicator) world.spawnEntity(location, EntityType.VINDICATOR);
                vindicator.setPatrolLeader(false);
                customizeMob(vindicator);
            }
        }
    }

    private static void customizeMob(LivingEntity mob) {
        int playerLvl = 0;
        int playerPrestige = 0;
        Location location = mob.getLocation();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(location.getWorld().getName().contains(player.getName().toLowerCase(Locale.ROOT))){
                Stats playerStats = Stats.fromPlayer(player);
                playerPrestige = playerStats.getPrestige();
                playerLvl = playerStats.getDungeonLevel();
            }
        }
        if(playerLvl > 1){
            double health = (12.0 * playerLvl) + (playerPrestige * 2);
            if(health > 2048){
                int totalLevel = playerPrestige + playerLvl;
                health = 2048;
                mob.getEquipment().setHelmet(generateArmorPiece(Material.DIAMOND_HELMET, totalLevel));
                mob.getEquipment().setChestplate(generateArmorPiece(Material.DIAMOND_CHESTPLATE, totalLevel));
                mob.getEquipment().setLeggings(generateArmorPiece(Material.DIAMOND_LEGGINGS, totalLevel));
                mob.getEquipment().setBoots(generateArmorPiece(Material.DIAMOND_BOOTS, totalLevel));
                mob.getEquipment().setItemInMainHand(generateWeapon(totalLevel));
            }
            mob.setMaxHealth(health);
            mob.setHealth(health);
            mob.addScoreboardTag("dungeonMob");
            mob.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, (playerLvl/2)));
        }
        if(mob instanceof Skeleton || mob instanceof Stray){
            EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());
            equipment.setItemInMainHand(createPower1Bow(playerLvl));
        }
        if(playerPrestige < 1){
            mob.setCustomName(ChatColor.RED + "Dungeon Mob" + ChatColor.RESET + ChatColor.GOLD + "<LvL" + playerLvl + ">");
        }else {
            mob.setCustomName(ChatColor.GOLD + "Dungeon ✦ Mob " + ChatColor.RESET + ChatColor.RED + "<LvL" + playerLvl + ">");
        }
        mob.setCustomNameVisible(true);
        mob.setPersistent(true);
        mob.setRemoveWhenFarAway(false);
        mob.setCanPickupItems(false);
    }

    private static ItemStack generateArmorPiece(Material material, int qualityLevel) {
        ItemStack armorPiece = new ItemStack(material);
        ItemMeta meta = armorPiece.getItemMeta();

        if (meta != null) {
            for (Enchantment enchantment : Enchantment.values()) {
                if (random.nextDouble() < getEnchantmentProbability(qualityLevel)) {
                    int maxLevel = Math.min(enchantment.getMaxLevel(), qualityLevel);
                    int level = random.nextInt(maxLevel) + 1;
                    meta.addEnchant(enchantment, level, true);
                }
            }
            armorPiece.setItemMeta(meta);
        }

        return armorPiece;
    }

    private static ItemStack generateWeapon(int qualityLevel) {
        Material[] weapons = { Material.DIAMOND_SWORD, Material.DIAMOND_AXE };
        ItemStack weapon = new ItemStack(weapons[random.nextInt(weapons.length)]);
        ItemMeta meta = weapon.getItemMeta();

        if (meta != null) {
            for (Enchantment enchantment : Enchantment.values()) {
                if (random.nextDouble() < getEnchantmentProbability(qualityLevel)) {
                    int maxLevel = Math.min(enchantment.getMaxLevel(), qualityLevel);
                    int level = random.nextInt(maxLevel) + 1;
                    meta.addEnchant(enchantment, level, true);
                }
            }
            weapon.setItemMeta(meta);
        }

        return weapon;
    }

    private static double getEnchantmentProbability(int qualityLevel) {
        // Customize this probability function based on your requirements
        return Math.min(0.3 + 0.1 * qualityLevel, 1.0);
    }


    public static ItemStack createPower1Bow(int lvl) {
        // Crear un nuevo ItemStack de tipo arco
        ItemStack bow = new ItemStack(Material.BOW);

        // Obtener el ItemMeta del arco
        ItemMeta meta = bow.getItemMeta();

        // Agregar el encantamiento de Poder I
        meta.addEnchant(Enchantment.POWER, lvl, true);

        // Establecer el ItemMeta del arco
        bow.setItemMeta(meta);

        return bow;
    }
}
