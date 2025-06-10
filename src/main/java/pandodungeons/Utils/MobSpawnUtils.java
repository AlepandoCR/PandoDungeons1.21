package pandodungeons.Utils;

import org.bukkit.*;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandoClass.RPGPlayer;
import pandoClass.util.EnemyTransformation;
import pandodungeons.Game.Stats;
import pandodungeons.PandoDungeons;

import java.util.*;

public class MobSpawnUtils {

    private static final Random random = new Random();


    public static void spawnMobs(Location location, Material blockType, World world, String subclassKey, PandoDungeons plugin) {
        String worldName = location.getWorld().getName().toLowerCase(Locale.ROOT); // Ensure this is at the top
        final Player[] foundPlayer = {null}; // Using array to be modifiable in lambda
        final int[] playerLvl = {0}; // Using array
        final int[] playerPrestige = {0}; // Using array
        final PlayerParty[] party = {null}; // Using array

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> worldName.contains(p.getName().toLowerCase(Locale.ROOT)))
                .findFirst()
                .ifPresent(player -> {
                    foundPlayer[0] = player;
                    playerLvl[0] = Stats.fromPlayer(player).dungeonLevel();
                    playerPrestige[0] = Stats.fromPlayer(player).prestige();
                    if (plugin.playerPartyList.isOwner(player)) {
                        party[0] = plugin.playerPartyList.getPartyByOwner(player);
                    }
                });

        int minMobs;
        int maxMobs;


        if (playerLvl[0] < 2) {
            minMobs = 1;
            maxMobs = 2;
        } else if (playerLvl[0] <= 4) {
            minMobs = 3;
            maxMobs = 5;
        } else {
            minMobs = 5;
            maxMobs = 7;
        }
        if(party[0] != null){
            maxMobs += 2 + party[0].getMembers().size();
            if(maxMobs > 15){
                maxMobs = 15;
            }
        }

        location.setY(location.getY() + 1);
        int numMobs = minMobs + random.nextInt(maxMobs - minMobs + 1);

        if (subclassKey == null || subclassKey.isEmpty()) {
            switch (blockType) {
                case IRON_BLOCK -> spawnZombies(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                case REDSTONE_BLOCK -> spawnSkeletons(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                case COAL_BLOCK -> spawnWitherSkeletons(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                case EMERALD_BLOCK -> spawnPillagersAndVindicators(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                case GOLD_BLOCK -> spawnHuzk(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                case DIAMOND_BLOCK -> spawnStray(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                case MAGENTA_GLAZED_TERRACOTTA -> spawnBlaze(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                case BLACK_GLAZED_TERRACOTTA -> spawnPiglinsAndBrutes(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                case GRAY_GLAZED_TERRACOTTA -> spawnSpider(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                case PURPLE_GLAZED_TERRACOTTA -> spawnBruja(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                default -> {
                }
            }
            return;
        }

        // Subclass-specific mob spawning logic
        switch (subclassKey) {
            case "ArcherClass":
                // Archer: Prefers ranged, avoids too many weak melee
                if (blockType == Material.REDSTONE_BLOCK || blockType == Material.DIAMOND_BLOCK) { // Skeletons, Strays
                    spawnSkeletons(location, numMobs + 1, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]); // More skeletons/strays
                } else if (blockType == Material.EMERALD_BLOCK) { // Pillagers/Vindicators
                    spawnPillagersAndVindicators(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                } else if (blockType == Material.IRON_BLOCK) { // Zombies
                    spawnSkeletons(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]); // Change Zombies to Skeletons
                } else { // Default for other block types for Archer
                    spawnMobsBasedOnBlock(location, blockType, world, numMobs, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                }
                break;
            case "TankClass":
                // Tank: Prefers more melee, can handle groups
                if (blockType == Material.IRON_BLOCK || blockType == Material.GOLD_BLOCK) { // Zombies, Husks
                    spawnZombies(location, numMobs + 2, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]); // More zombies/husks
                } else if (blockType == Material.COAL_BLOCK) { // Wither Skeletons
                    spawnWitherSkeletons(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                } else if (blockType == Material.REDSTONE_BLOCK) { // Skeletons
                    spawnZombies(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]); // Change Skeletons to Zombies
                } else { // Default for other block types for Tank
                    spawnMobsBasedOnBlock(location, blockType, world, numMobs, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                }
                break;
            case "AssassinClass":
                // Assassin: Fewer but potentially more dangerous mobs, or mobs that fit stealth theme
                if (blockType == Material.COAL_BLOCK) { // Wither Skeletons
                    spawnWitherSkeletons(location, Math.max(1, numMobs -1), world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]); // Fewer, but still dangerous
                } else if (blockType == Material.EMERALD_BLOCK) { // Pillagers/Vindicators
                    spawnVindicators(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]); // Focus on Vindicators
                } else if (blockType == Material.GRAY_GLAZED_TERRACOTTA){ //Spiders
                    spawnCaveSpiders(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]); // Cave spiders are more assassin-like
                } else { // Default for other block types for Assassin
                    spawnMobsBasedOnBlock(location, blockType, world, Math.max(1, numMobs - 1), plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]); // Generally fewer mobs
                }
                break;
            case "MageClass":
                // Mage: Prefers magic-using or elemental mobs
                if (blockType == Material.MAGENTA_GLAZED_TERRACOTTA) { // Blaze
                    spawnBlaze(location, numMobs + 1, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                } else if (blockType == Material.PURPLE_GLAZED_TERRACOTTA) { // Witch
                    spawnBruja(location, numMobs + 1, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                } else if (blockType == Material.DIAMOND_BLOCK) { // Stray (ice magic)
                    spawnStray(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                } else if (blockType == Material.REDSTONE_BLOCK && random.nextDouble() < 0.3) { // 30% chance for Skeletons to become Witches
                    spawnBruja(location, numMobs, world, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                }
                else { // Default for other block types for Mage
                    spawnMobsBasedOnBlock(location, blockType, world, numMobs, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                }
                break;
            // Add cases for FarmerClass, etc.
            default:
                // Fallback to default logic if subclassKey is not recognized
                spawnMobsBasedOnBlock(location, blockType, world, numMobs, plugin, foundPlayer[0], playerLvl[0], playerPrestige[0]);
                break;
        }
    }

    // Helper method to avoid code duplication for default spawning logic
    private static void spawnMobsBasedOnBlock(Location location, Material blockType, World world, int numMobs, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        switch (blockType) {
            case IRON_BLOCK -> spawnZombies(location, numMobs, world, plugin, player, playerLvl, playerPrestige);
            case REDSTONE_BLOCK -> spawnSkeletons(location, numMobs, world, plugin, player, playerLvl, playerPrestige);
            case COAL_BLOCK -> spawnWitherSkeletons(location, numMobs, world, plugin, player, playerLvl, playerPrestige);
            case EMERALD_BLOCK -> spawnPillagersAndVindicators(location, numMobs, world, plugin, player, playerLvl, playerPrestige);
            case GOLD_BLOCK -> spawnHuzk(location, numMobs, world, plugin, player, playerLvl, playerPrestige);
            case DIAMOND_BLOCK -> spawnStray(location, numMobs, world, plugin, player, playerLvl, playerPrestige);
            case MAGENTA_GLAZED_TERRACOTTA -> spawnBlaze(location, numMobs, world, plugin, player, playerLvl, playerPrestige);
            case BLACK_GLAZED_TERRACOTTA -> spawnPiglinsAndBrutes(location, numMobs, world, plugin, player, playerLvl, playerPrestige);
            case GRAY_GLAZED_TERRACOTTA -> spawnSpider(location, numMobs, world, plugin, player, playerLvl, playerPrestige);
            case PURPLE_GLAZED_TERRACOTTA -> spawnBruja(location, numMobs, world, plugin, player, playerLvl, playerPrestige);
            default -> {
            }
        }
    }

    private static void spawnVindicators(Location location, int numMobs, World world, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        for (int i = 0; i < numMobs; i++) {
            Vindicator vindicator = (Vindicator) world.spawnEntity(location, EntityType.VINDICATOR);
            vindicator.setPatrolLeader(false);
            customizeMob(vindicator, plugin, player, playerLvl, playerPrestige);
        }
    }

    private static void spawnCaveSpiders(Location location, int numMobs, World world, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        for (int i = 0; i < numMobs; i++) {
            CaveSpider spider = (CaveSpider) world.spawnEntity(location, EntityType.CAVE_SPIDER);
            customizeMob(spider, plugin, player, playerLvl, playerPrestige);
        }
    }

    private static void spawnPiglinsAndBrutes(Location location, int numMobs, World world, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        for (int i = 0; i < numMobs; i++) {
            if (random.nextBoolean()) {
                Piglin piglin = (Piglin) world.spawnEntity(location, EntityType.PIGLIN);
                piglin.setImmuneToZombification(true);
                customizeMob(piglin, plugin, player, playerLvl, playerPrestige);
            } else {
                PiglinBrute brute = (PiglinBrute) world.spawnEntity(location, EntityType.PIGLIN_BRUTE);
                brute.setImmuneToZombification(true);
                customizeMob(brute, plugin, player, playerLvl, playerPrestige);
            }
        }
    }
    private static void spawnBruja(Location location, int numMobs, World world, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        for (int i = 0; i < numMobs; i++) {
            Witch witch = (Witch) world.spawnEntity(location, EntityType.WITCH);
            customizeMob(witch, plugin, player, playerLvl, playerPrestige);
        }
    }
    private static void spawnSpider(Location location, int numMobs, World world, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        for (int i = 0; i < numMobs; i++) {
            Spider spider = (Spider) world.spawnEntity(location, EntityType.SPIDER);
            customizeMob(spider, plugin, player, playerLvl, playerPrestige);
        }
    }
    private static void spawnBlaze(Location location, int numMobs, World world, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        for (int i = 0; i < numMobs; i++) {
            Blaze blaze = (Blaze) world.spawnEntity(location, EntityType.BLAZE);
            customizeMob(blaze, plugin, player, playerLvl, playerPrestige);
        }
    }
    private static void spawnStray(Location location, int numMobs, World world, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        for (int i = 0; i < numMobs; i++) {
            Stray stray = (Stray) world.spawnEntity(location, EntityType.STRAY);
            customizeMob(stray, plugin, player, playerLvl, playerPrestige);
        }
    }
    private static void spawnZombies(Location location, int numMobs, World world, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        for (int i = 0; i < numMobs; i++) {
            Zombie zombie = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);
            customizeMob(zombie, plugin, player, playerLvl, playerPrestige);
        }
    }
    private static void spawnHuzk(Location location, int numMobs, World world, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        for (int i = 0; i < numMobs; i++) {
            Husk husk = (Husk) world.spawnEntity(location, EntityType.HUSK);
            customizeMob(husk, plugin, player, playerLvl, playerPrestige);
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




    private static void spawnSkeletons(Location location, int numMobs, World world, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        for (int i = 0; i < numMobs; i++) {
            Skeleton skeleton = (Skeleton) world.spawnEntity(location, EntityType.SKELETON);
            customizeMob(skeleton, plugin, player, playerLvl, playerPrestige);
        }
    }

    private static void spawnWitherSkeletons(Location location, int numMobs, World world, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        for (int i = 0; i < numMobs; i++) {
            WitherSkeleton witherSkeleton = (WitherSkeleton) world.spawnEntity(location, EntityType.WITHER_SKELETON);
            customizeMob(witherSkeleton, plugin, player, playerLvl, playerPrestige);
        }
    }

    private static void spawnPillagersAndVindicators(Location location, int numMobs, World world, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        for (int i = 0; i < numMobs; i++) {
            if (random.nextBoolean()) {
                Pillager pillager = (Pillager) world.spawnEntity(location, EntityType.PILLAGER);
                pillager.setPatrolLeader(false);
                customizeMob(pillager, plugin, player, playerLvl, playerPrestige);
            } else {
                Vindicator vindicator = (Vindicator) world.spawnEntity(location, EntityType.VINDICATOR);
                vindicator.setPatrolLeader(false);
                customizeMob(vindicator, plugin, player, playerLvl, playerPrestige);
            }
        }
    }

    private static void customizeMob(LivingEntity mob, PandoDungeons plugin, Player player, int playerLvl, int playerPrestige) {
        if(playerLvl > 1){
            double health = (12.0 * playerLvl) + (playerPrestige * 2);
            if(health > 2048){
                int totalLevel = playerPrestige + playerLvl;
                health = 2048;
                EntityEquipment equipment = mob.getEquipment();

                if(equipment == null) return;

                equipment.setHelmet(generateArmorPiece(Material.DIAMOND_HELMET, totalLevel));
                equipment.setChestplate(generateArmorPiece(Material.DIAMOND_CHESTPLATE, totalLevel));
                equipment.setLeggings(generateArmorPiece(Material.DIAMOND_LEGGINGS, totalLevel));
                equipment.setBoots(generateArmorPiece(Material.DIAMOND_BOOTS, totalLevel));
                equipment.setItemInMainHand(generateWeapon(totalLevel));
            }
            mob.setMaxHealth(health);
            mob.setHealth(health);
            mob.addScoreboardTag("dungeonMob");
            mob.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, (playerLvl/2)));
        }
        if(mob instanceof Skeleton || mob instanceof Stray){
            EntityEquipment equipment = mob.getEquipment();
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

        if (player != null){
            RPGPlayer rpgPlayer = plugin.rpgManager.getPlayer(player);
            int lvl = rpgPlayer.getLevel();
            if(lvl > 50){
                EnemyTransformation.transformDungeon(mob,plugin,lvl);
            }
        }
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
