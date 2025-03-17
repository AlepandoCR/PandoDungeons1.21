package pandoClass;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.floodgate.api.FloodgateApi;
import pandoClass.classes.archer.Archer;
import pandoClass.classes.assasin.Assasin;
import pandoClass.classes.farmer.ControlledEntityBehavior;
import pandoClass.classes.farmer.skils.ExtraHarvestSkill;
import pandoClass.classes.farmer.skils.TameSkill;
import pandoClass.files.RPGPlayerDataManager;
import pandoClass.classes.tank.Tank;
import pandoClass.upgrade.ItemUpgrade;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.Utils.ItemUtils;

import java.net.MalformedURLException;
import java.util.*;
import java.util.function.Predicate;

import static pandoClass.InitMenu.INNIT_MENU_NAME;
import static pandoClass.classes.archer.skills.ArrowExplotionSkill.explosiveAmmo;
import static pandoClass.classes.archer.skills.SaveAmmoSkill.playersSavingAmmo;
import static pandoClass.classes.assasin.skills.LifeStealSkill.lifeStealingPlayers;
import static pandoClass.classes.assasin.skills.SilentStepSkill.silencedPlayers;
import static pandoClass.gachaPon.GachaHolo.activeHolograms;
import static pandoClass.gachaPon.prizes.legendary.StormSwordPrize.isStormSword;
import static pandoClass.gachaPon.prizes.mithic.JetPackPrize.isJetPack;
import static pandoClass.gachaPon.prizes.mithic.MapachoBladePrize.isMapachoBlade;
import static pandodungeons.pandodungeons.Utils.ItemUtils.isGarabiThor;

public class RPGListener implements Listener {
    public static final List<Player> magicShieldPlayers = new ArrayList<>();

    private final PandoDungeons plugin;
    // Tiempo de cooldown en segundos
    private static final int PVP_COOLDOWN = 10;
    // Map que almacena las UUID de los jugadores y el cooldown restante (en segundos)
    private static final Map<UUID, Integer> pvpCooldowns = new HashMap<>();

    public RPGListener(PandoDungeons plugin) {
        this.plugin = plugin;
        // Iniciar un task que se ejecuta cada segundo para decrementar el cooldown
        Bukkit.getScheduler().runTaskTimer(plugin, this::decrementCooldowns, 1, 20); // 20 ticks = 1 segundo
    }

    private void decrementCooldowns() {
        pvpCooldowns.entrySet().removeIf(entry -> {
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                return true; // Eliminar la entrada
            } else {
                entry.setValue(remaining);
                return false; // No eliminar
            }
        });
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        // Verificar que tanto la víctima como el atacante sean jugadores (PvP)
        if(event.getEntity() instanceof Player victim){
            if (magicShieldPlayers.contains(victim)) {
                reduceEffectDamage(victim, event);
            }
            if (event.getDamager() instanceof Player attacker) {
                // Reiniciamos el cooldown del atacante al valor completo
                pvpCooldowns.put(attacker.getUniqueId(), PVP_COOLDOWN);
            }
        }

    }


    @EventHandler(ignoreCancelled = true)
    public void onCropMultiplyBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // Verifica que el jugador tenga activada la habilidad ExtraProducción
        if (!ExtraHarvestSkill.isFarmingPlayer(player)) return;

        Block block = event.getBlock();
        Material type = block.getType();
        // Lista de cultivos: ajusta según necesidades
        if (type == Material.WHEAT || type == Material.CARROTS || type == Material.POTATOES || type == Material.BEETROOTS || type == Material.MELON || type == Material.PUMPKIN || type == Material.COCOA) {
            // Evitar drops dobles por la acción normal

            event.setDropItems(false);
            Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
            double multiplier = new RPGPlayer(player, plugin).getFirstSkilLvl() / 5.0;
            for (ItemStack drop : drops) {
                drop.setAmount(Math.max(1,(int)(drop.getAmount() * multiplier)));
                block.getWorld().dropItemNaturally(block.getLocation(), drop);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCropBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!ExtraHarvestSkill.isFarmingPlayer(player)) return;

        Block block = event.getBlock();
        Material type = block.getType();
        if (type == Material.WHEAT || type == Material.CARROTS || type == Material.POTATOES || type == Material.BEETROOTS) {
            // Romper bloques adyacentes en un radio 1 (puedes ajustar)
            int num = 0;
            for (int x = -1; x <= 1; x++) {
                if(15 <= num){
                    break;
                }
                num++;
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) continue;
                    Block adjacent = block.getRelative(x, 0, z);
                    if (adjacent.getType() == type) {
                        adjacent.breakNaturally();
                    }
                }
            }
            player.sendMessage("¡Combo de cosecha activado!");
        }
    }


    @EventHandler
    public void onAnimalInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);

        // Verifica si el jugador tiene la habilidad Animal Wrangler
        if (!TameSkill.isTamingPlayer(player)) return;

        // Considera animales neutrales o amistosos
        if (entity instanceof Animals || entity instanceof Golem || entity instanceof Piglin) {
            LivingEntity neutralEntity = (LivingEntity) entity;
            double ogHealth = neutralEntity.getMaxHealth();
            calculateHpFromLvlAndApply(rpgPlayer.getSecondSkilLvl(),neutralEntity);
            LivingEntity nearestHostile = findNearestHostile(neutralEntity, 40);
            if (nearestHostile != null) {
                new ControlledEntityBehavior(neutralEntity,nearestHostile,ogHealth).runTaskTimer(plugin,0,1);
                player.sendMessage("¡" + neutralEntity.getType() + " ahora atacará a " + nearestHostile.getType() + "!");
            } else {
                player.sendMessage("No hay entidades hostiles cercanas.");
            }
        }
    }


    private void calculateHpFromLvlAndApply(int lvl, LivingEntity entity){
        double baseHP = entity.getMaxHealth();

        double multiply = lvl * 0.25;

        double finalHp = baseHP + (baseHP * multiply);

        entity.setMaxHealth(finalHp);

        entity.heal(baseHP * multiply);
    }


    private LivingEntity findNearestHostile(LivingEntity entity, double radius) {
        Location location = entity.getLocation();
        double closestDistance = radius * radius;
        LivingEntity closestEntity = null;
        for (Entity nearbyEntity : entity.getNearbyEntities(radius, radius, radius)) {
            if (nearbyEntity instanceof Monster) {
                double distance = location.distanceSquared(nearbyEntity.getLocation());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = (LivingEntity) nearbyEntity;
                }
            }
        }
        return closestEntity;
    }

    // Mapa para rastrear las entidades afectadas y sus objetivos




    public void reduceEffectDamage(Player player, EntityDamageByEntityEvent event) {
        RPGPlayer rpgPlayer = load(player);
        if (rpgPlayer != null) {
            int thirdSkillLvl = rpgPlayer.getThirdSkillLvl();  // Obtener el nivel de la habilidad
            double reductionPercentage = 0.0;
            EntityDamageEvent.DamageCause cause = event.getCause();

            // Lógica para calcular la reducción del daño
            if (isEffectDamage(cause)) {
                // Para daños "efecto", aplicar mayor reducción (ejemplo: 20% por nivel)
                reductionPercentage = 0.2 * thirdSkillLvl;
            } else if (isProjectileDamage(cause) || event.getDamager() instanceof Projectile) {
                // Para daños por proyectiles, aplicar una reducción menor (ejemplo: 10% por nivel)
                reductionPercentage = 0.1 * thirdSkillLvl;
            }

            // Limitar la reducción a un máximo de 50%
            reductionPercentage = Math.min(reductionPercentage, 0.5);

            // Aplicar la reducción del daño
            double reducedDamage = calculateReducedDamage(event.getDamage(), reductionPercentage);
            event.setDamage(reducedDamage);
        }
    }

    private boolean isEffectDamage(EntityDamageEvent.DamageCause cause) {
        // Se consideran "efecto" los daños por fuego, lava, magia, etc.
        return cause == EntityDamageEvent.DamageCause.FIRE ||
                cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
                cause == EntityDamageEvent.DamageCause.LAVA ||
                cause == EntityDamageEvent.DamageCause.DROWNING ||
                cause == EntityDamageEvent.DamageCause.STARVATION ||
                cause == EntityDamageEvent.DamageCause.FREEZE ||
                cause == EntityDamageEvent.DamageCause.MAGIC ||
                cause == EntityDamageEvent.DamageCause.WITHER ||
                cause == EntityDamageEvent.DamageCause.DRAGON_BREATH ||
                cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
                cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                cause == EntityDamageEvent.DamageCause.LIGHTNING ||
                cause == EntityDamageEvent.DamageCause.SONIC_BOOM;
    }

    private boolean isProjectileDamage(EntityDamageEvent.DamageCause cause) {
        // Se consideran "proyectiles" los que tienen la causa PROJECTILE
        return cause == EntityDamageEvent.DamageCause.PROJECTILE;
    }

    private double calculateReducedDamage(double originalDamage, double reductionPercentage) {
        return originalDamage * (1 - reductionPercentage);
    }

    // Método para saber si un jugador está en cooldown
    public static boolean isPlayerOnPvPCooldown(Player player) {
        return pvpCooldowns.containsKey(player.getUniqueId());
    }

    // Método para obtener el cooldown restante (en segundos) para un jugador
    public static int getPvPCooldown(Player player) {
        return pvpCooldowns.getOrDefault(player.getUniqueId(), 0);
    }

    public static boolean isPlayerOnPvP(Player player) {
        return pvpCooldowns.containsKey(player.getUniqueId());
    }

    public void upGradeSkill(Skill skill, int skillNum) throws MalformedURLException {
        Player player = skill.getPlayer();

        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);

        if(4 <= rpgPlayer.getOrbs()){
            switch (skillNum){
                case 1:
                    rpgPlayer.setFirstSkilLvl(skill.getLvl() + 1);
                    break;
                case 2:
                    rpgPlayer.setSecondSkilLvl(skill.getLvl() + 1);
                    break;
                case 3:
                    rpgPlayer.setThirdSkillLvl(skill.getLvl() + 1);
                    break;
            }

            rpgPlayer.setOrbs(rpgPlayer.getOrbs() - 4);

            player.openInventory(plugin.initMenu.createClassSelectionMenu(player, InitMenu.Reason.SKILL_MENU));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws MalformedURLException {
        Player player = (Player) event.getWhoClicked();
        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);
        ClassRPG classRPG = rpgPlayer.getClassRpg();
        String title = event.getView().getTitle();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) {
            return;
        }

        if (title.contains("Menu Skills")) {
            event.setCancelled(true);

            if (clickedItem.getType() == Material.PLAYER_HEAD) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 420) {
                    String displayName = meta.getDisplayName();

                    if (displayName.contains(classRPG.getFirstSkill().getName())) {
                        upGradeSkill(classRPG.getFirstSkill(), 1);
                    } else if (displayName.contains(classRPG.getSecondSkill().getName())) {
                        upGradeSkill(classRPG.getSecondSkill(), 2);
                    } else if (displayName.contains(classRPG.getThirdSkill().getName())) {
                        upGradeSkill(classRPG.getThirdSkill(), 3);
                    }
                }
            }
        } else if (title.equals(INNIT_MENU_NAME) || title.contains("Cambiar clase")) {
            event.setCancelled(true);

            if (clickedItem.getType() == Material.PLAYER_HEAD) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta != null && meta.hasCustomModelData()) {
                    int customModelData = meta.getCustomModelData();
                    switch (customModelData) {
                        case 111:
                            handleClassChange(rpgPlayer, player, "ArcherClass");
                            break;
                        case 222:
                            handleClassChange(rpgPlayer, player,"TankClass");
                            break;
                        case 333:
                            handleClassChange(rpgPlayer, player,"AssassinClass");
                            break;
                        default:
                            break;
                    }
                    rpgPlayer.handleTexturePack(player);
                    player.setWalkSpeed(0.2f);
                }
            }
        }else if(title.contains(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "¿Deseas el texturepack?")){
            event.setCancelled(true);

            if(clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()){
                if(clickedItem.getItemMeta().getDisplayName().contains("SI")){
                    rpgPlayer.setTexturePack(true);
                    player.performCommand("texturas mantener");
                }else if(clickedItem.getItemMeta().getDisplayName().contains("NO")){
                    rpgPlayer.setTexturePack(false);
                }
            }
            rpgPlayer.setHasChosenTextures(true);
            player.closeInventory();
        }
    }

    private void handleClassChange(RPGPlayer rpgPlayer, Player player, String classKey) throws MalformedURLException {
        String currentKey = rpgPlayer.getClassKey();
        if(currentKey == null){
            rpgPlayer.setClassKey(classKey);
            player.closeInventory();
        }else{
            if (currentKey.isEmpty()) {
                rpgPlayer.setClassKey(classKey);
                player.closeInventory();
            } else if (!currentKey.equalsIgnoreCase(classKey)){
                if (rpgPlayer.getCoins() >= 500) {
                    rpgPlayer.removeCoins(500);
                    rpgPlayer.setClassKey(classKey);
                    player.openInventory(plugin.initMenu.createClassSelectionMenu(player, InitMenu.Reason.SHOP));
                } else {
                    player.sendMessage("No tienes suficientes monedas para cambiar tu clase");
                }
            } else{
                player.sendMessage("Ya haz seleccionado esa clase");
            }
        }
    }



    private static final double BASE_LIFESTEAL_PERCENT = 0.015; // 1.5% de robo de vida por nivel
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Verificar si el atacante es un jugador
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);


        int level = rpgPlayer.getFirstSkilLvl(); // Método para obtener el nivel del jugador


        if(lifeStealingPlayers.contains(player)){
            // Verificar si la entidad dañada es un ser vivo (mobs o jugadores)
            if (event.getEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getEntity();

                double damage = event.getFinalDamage();
                double lifeStealAmount = damage * (BASE_LIFESTEAL_PERCENT * level); // Robo de vida escalado por nivel

                player.heal(lifeStealAmount);
            }
        }
    }

    private Random random = new Random();

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        // Si el target no es un jugador, no interesa
        if (!(event.getTarget() instanceof Player player)) {
            return;
        }

        // Obtén el RPGPlayer para saber su nivel
        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);

        if(event.getEntity() instanceof LivingEntity mob){

            if(!player.getWorld().equals(mob.getWorld()))return;
            // Calcula la distancia entre el mob y el jugador
            double distance = mob.getLocation().distance(player.getLocation());

            int level = rpgPlayer.getSecondSkilLvl(); // Se asume que este nivel determina la "efectividad del sigilo"

            if(silencedPlayers.contains(player)){

                // Si el jugador se encuentra muy cerca, la acción "dispara" (p. ej. por haber golpeado a corta distancia)
                if (distance <= 5) {
                    return; // El mob debe targetearlo sin aplicar sigilo
                }

                // Si el jugador está sprintando y se encuentra a 8 bloques o menos, el mob también debe targetearlo
                if (player.isSprinting() && distance <= 8) {
                    return;
                }

                // Calcula la probabilidad de que el mob ignore al jugador
                double chanceToIgnore = Math.min(10 + (level * 3), 100); // Por ejemplo: nivel 10 -> 20%, nivel 50 -> 100%

                // Realiza un "roll" aleatorio y, si cae dentro de la probabilidad, cancela el targeting
                double roll = random.nextDouble() * 100;
                if (roll < chanceToIgnore) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // Evitar que el jugador saque los objetos del inventario cuando lo cierra
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory closedInventory = event.getInventory();
        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();
        if(FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())){
            return;
        }

        // Verificar si el inventario es el menú "Elección de clase"
        if (title.equals(INNIT_MENU_NAME) || title.contains("Cambiar clase")) {
            RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);
            if(rpgPlayer.getClassKey() == null || rpgPlayer.getClassKey().isEmpty()){
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        try {
                            player.openInventory(plugin.initMenu.createClassSelectionMenu(player, InitMenu.Reason.INNIT));
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.runTaskLater(plugin,2);
            }
        }
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity.getType() == EntityType.VILLAGER) {
            Villager villager = (Villager) entity;
            NamespacedKey key = new NamespacedKey(plugin, "ItemUpgrade");
            PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
            if (dataContainer.has(key, PersistentDataType.BOOLEAN)) {
                Boolean customTag = dataContainer.get(key, PersistentDataType.BOOLEAN);
                if(Boolean.TRUE.equals(customTag)){
                    Player player = event.getPlayer();
                    new ItemUpgrade(plugin).upgradeItem(player.getInventory().getItem(EquipmentSlot.HAND),player,villager.getLocation(),villager);
                }

            }
        }
    }

    public void applyPack(Player player, String url){
        // Opcional: crea un mensaje usando Component (si usas la API Adventure)
        Component prompt = Component.text("Aplica el texturepack que indicaste con /texturas mantener");

        // Solicita el texture pack al cliente, forzando su aplicación
        player.setResourcePack(url, null, prompt, true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws MalformedURLException {
        Player player = event.getPlayer();
        player.setMaxHealth(20.0);
        player.setWalkSpeed(0.2f);

        applyMissingTextures(player);

        if(!player.hasPlayedBefore()){
            player.getInventory().addItem(plugin.prizeManager.gachaToken());
        }

        if(FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())){
            return;
        }

        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);

        if(rpgPlayer.isTexturePack()){
            String texturePackUrl = "https://raw.githubusercontent.com/AlepandoCR/MapachoTextura/main/mapachos.zip";
            applyPack(player,texturePackUrl);
        }

        if(rpgPlayer.getClassKey() == null || rpgPlayer.getClassKey().isEmpty()){
            player.openInventory(plugin.initMenu.createClassSelectionMenu(player, InitMenu.Reason.INNIT));
           return;
        }

        if(!plugin.rpgPlayersList.containsKey(rpgPlayer)){
            String classKey = rpgPlayer.getClassKey();
            ClassRPG classRPG = new Assasin(rpgPlayer,plugin);
            classRPG = switch (classKey) {
                case "ArcherClass" -> new Archer(rpgPlayer,plugin);
                case "TankClass" -> new Tank(rpgPlayer,plugin);
                default -> classRPG;
            };
            plugin.rpgPlayersList.put(rpgPlayer.getPlayer(),classRPG);
        }

        save(rpgPlayer);
    }

    private void applyMissingTextures(Player player) {
        Map<String, Predicate<ItemStack>> itemChecks = Map.of(
                "mapachoblade", stack -> isMapachoBlade(stack, plugin),
                "garabithor", ItemUtils::isGarabiThor,
                "thunderSword", stack -> isStormSword(stack, plugin),
                "jetpack", stack -> isJetPack(stack, plugin)
        );

        for (ItemStack stack : player.getInventory()) {
            if (stack == null || !stack.hasItemMeta()) continue;

            ItemMeta meta = stack.getItemMeta();
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            List<String> existingTags = component.getStrings();

            itemChecks.forEach((tag, check) -> {
                if (check.test(stack) && !existingTags.contains(tag)) {
                    applyCustomModelData(meta, component, tag);
                    stack.setItemMeta(meta);
                }
            });
        }
    }

    private void applyCustomModelData(ItemMeta meta, CustomModelDataComponent component, String tag) {
        List<String> newTags = new ArrayList<>(component.getStrings());
        newTags.add(tag);
        component.setStrings(newTags);
        meta.setCustomModelDataComponent(component);
    }


    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }

        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);
        int level = rpgPlayer.getFirstSkilLvl();

        if (playersSavingAmmo.contains(player)) {
            // La probabilidad de ahorro de flecha es 2% por nivel (máx. 100%).
            double chance = Math.min(level * 0.02, 1.0); // Se asegura que no pase de 100%.

            // Si el azar está a favor, evitamos el consumo de la flecha.
            if (random.nextDouble() < chance) {
                // Evita que la flecha se consuma al disparar.
                event.setCancelled(true);
                player.updateInventory(); // Refresca el inventario para evitar desincronización visual.
            }
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        // Verificar que el proyectil sea una flecha
        if (!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }

        // Verificar que el tirador sea un jugador
        if (!(arrow.getShooter() instanceof Player player)) {
            return;
        }

        // Obtener el RPGPlayer para conocer el nivel
        RPGPlayer rpgPlayer = plugin.rpgPlayerDataManager.load(player);
        if (rpgPlayer == null) {
            return;
        }
        int level = rpgPlayer.getThirdSkillLvl();

        if(explosiveAmmo.contains(player)){

            // Calcular el radio de la explosión según el nivel.
            // Ejemplo: radio base de 2 bloques + 0.1 por cada nivel
            float explosionRadius = 1.0f + level * 0.1f;

            // Obtener la ubicación de impacto de la flecha
            Location explosionLocation = arrow.getLocation();
            World world = explosionLocation.getWorld();

            // Crear efecto de explosión sin daño automático a entidades ni destrucción de bloques.
            world.createExplosion(explosionLocation, explosionRadius, false, false, player);

            // Reproducir efectos visuales y de sonido de explosión
            world.spawnParticle(Particle.EXPLOSION, explosionLocation, 1);
            world.playSound(explosionLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

            // Remover la flecha, si aún existe
            arrow.remove();
        }
    }

    // Listener para eliminar el holograma inmediatamente cuando el jugador se desconecta
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (activeHolograms.containsKey(playerId)) {
            ArmorStand holograma = activeHolograms.get(playerId);
            if (holograma != null && !holograma.isDead()) {
                holograma.remove();
            }
            activeHolograms.remove(playerId);
        }
    }


    private void save(RPGPlayer rpgPlayer){
        plugin.rpgPlayerDataManager.save(rpgPlayer);
    }

    private RPGPlayer load(Player rpgPlayer){
        return plugin.rpgPlayerDataManager.load(rpgPlayer);
    }
}
