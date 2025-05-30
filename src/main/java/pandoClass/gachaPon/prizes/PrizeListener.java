package pandoClass.gachaPon.prizes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.gachaPon.GachaHolo;
import pandoClass.gachaPon.Gachapon;
import pandoClass.gachaPon.prizes.legendary.BoomerangAxePrize;
import pandoClass.gachaPon.prizes.mithic.TeleShardPrize;
import pandoClass.gachaPon.prizes.mithic.TeleVillagerShardPrize;
import pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.util.*;

import static pandoClass.gachaPon.Gachapon.activeGachapon;
import static pandoClass.gachaPon.Gachapon.owedTokenPlayers;
import static pandoClass.gachaPon.prizes.epic.PuertoViejoPipePrize.applyRandomEffect;
import static pandoClass.gachaPon.prizes.epic.PuertoViejoPipePrize.isPuertoViejoPipe;
import static pandoClass.gachaPon.prizes.epic.ReparationShardPrize.isReparationShardItem;
import static pandoClass.gachaPon.prizes.legendary.EscudoReflectantePrize.isReflectShield;
import static pandoClass.gachaPon.prizes.legendary.StormSwordPrize.isStormSword;
import static pandoClass.gachaPon.prizes.epic.TeleportationHeartPrize.*;
import static pandoClass.gachaPon.prizes.mithic.InmortalityStar.chargeStar;
import static pandoClass.gachaPon.prizes.mithic.InmortalityStar.useStar;
import static pandoClass.gachaPon.prizes.mithic.MapachoBladePrize.isMapachoBlade;
import static pandoClass.gachaPon.prizes.legendary.SlingShotPrize.fireCustomCrossbow;
import static pandoClass.gachaPon.prizes.mithic.TeleShardPrize.*;
import static pandoClass.gachaPon.prizes.mithic.TeleVillagerShardPrize.*;

public class PrizeListener implements Listener {
    private final PandoDungeons plugin;
    private final NamespacedKey shieldEffect;
    private final NamespacedKey usosKey;
    private final NamespacedKey katanaKey;
    private final Map<UUID, Integer> comboMap = new HashMap<>();
    private final Map<UUID, Long> lastHitTime = new HashMap<>();
    private final Map<Player, BukkitRunnable> activeJetPackRunnables = new HashMap<>();
    private final Map<Player, BukkitRunnable> activeBootRunnables = new HashMap<>();


    private final Map<UUID, Integer> stacks = new HashMap<>();
    private final Map<UUID, Long> lastMoveTime = new HashMap<>();
    private final Map<UUID, Long> lastStackTime = new HashMap<>();
    private final NamespacedKey CHONETE_KEY;


    public PrizeListener(PandoDungeons plugin) {
        this.plugin = plugin;
        this.usosKey = new NamespacedKey(plugin, "puertoViejoPipeUsos") ;
        shieldEffect = new NamespacedKey(plugin,"shieldEffect");
        this.CHONETE_KEY = new NamespacedKey(plugin, "choneteViento");
        this.katanaKey = new NamespacedKey(plugin, "katana");

        katanaComboDecay();
    }

    private boolean isWearingChonete(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null) return false;
        ItemMeta meta = helmet.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(CHONETE_KEY);
    }

    // Katana [--------------------------------------------------------------------------------------------------------------]


    public void katanaComboDecay(){
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            for (UUID uuid : new HashSet<>(lastHitTime.keySet())) {
                if (now - lastHitTime.get(uuid) >= 5000) {
                    comboMap.put(uuid, 0);
                    lastHitTime.remove(uuid);
                }
            }
        }, 20L, 20L);
    }

    @EventHandler
    public void onKatanaDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isKatana(item)) return;

        UUID uuid = player.getUniqueId();
        int combo = comboMap.getOrDefault(uuid, 0);
        combo = Math.min(combo + 1, 100); // max combo x5
        comboMap.put(uuid, combo);
        lastHitTime.put(uuid, System.currentTimeMillis());

        double bonusMultiplier = 1.0 + (combo * 0.1); // 10% por combo
        event.setDamage(event.getDamage() * bonusMultiplier);

        player.sendActionBar(
                ChatColor.DARK_RED + "⚔ Combo " + ChatColor.RED + "x" + combo +
                        ChatColor.GRAY + " | " + ChatColor.GOLD + "+" + (int)((bonusMultiplier - 1) * 100) + "%" + ChatColor.YELLOW + " Daño"
        );

    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        BoomerangAxePrize.handleRightClick(event,plugin); // Machete Volador [-----------------------------------------------------------------------------------------------------------------]

        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isKatana(item)) return;

        UUID uuid = player.getUniqueId();
        int combo = comboMap.getOrDefault(uuid, 0);

        if (!player.isSneaking()) return;
        if (combo < 5) return;

        event.setCancelled(true);
        comboMap.put(uuid, 0);
        lastHitTime.remove(uuid);

        launchSpectralSlash(player, combo);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1f, 1.2f);
    }

    private void launchSpectralSlash(Player player, int combo) {
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection().normalize();

        for (int i = 1; i <= 10; i++) {
            Location checkLoc = loc.clone().add(direction.clone().multiply(i));
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, checkLoc, 1);

            for (Entity entity : player.getWorld().getNearbyEntities(checkLoc, 5, 5, 5)) {
                if (entity instanceof Enemy target && !entity.equals(player)) {

                    target.damage(combo * 10, player);
                }
            }
        }
    }

    private boolean isKatana(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(katanaKey, PersistentDataType.BYTE);
    }


    // Katana [--------------------------------------------------------------------------------------------------------------]


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isWearingChonete(player)) return;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        boolean moved = event.getFrom().distance(event.getTo()) > 0.05; // filtramos micro-movimientos
        if (!moved) return;

        lastMoveTime.put(uuid, now);

        // Carga stack si pasó suficiente tiempo desde el último
        long lastStack = lastStackTime.getOrDefault(uuid, 0L);
        if (now - lastStack >= 2000) {
            int current = stacks.getOrDefault(uuid, 0);
            if (current < 10) {
                stacks.put(uuid, current + 1);
                lastStackTime.put(uuid, now);
            }
        }

        updatePlayerSpeedAndUI(player);
    }

    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!isWearingChonete(player)) return;

        stacks.put(player.getUniqueId(), 0);
        updatePlayerSpeedAndUI(player);
    }

    public void tickChoneteStacks() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!isWearingChonete(player)) continue;
                    UUID uuid = player.getUniqueId();

                    long lastMoved = lastMoveTime.getOrDefault(uuid, 0L);
                    int current = stacks.getOrDefault(uuid, 0);

                    if (now - lastMoved > 3000 && current > 0) {
                        stacks.put(uuid, current - 1);
                        updatePlayerSpeedAndUI(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    private void updatePlayerSpeedAndUI(Player player) {
        UUID uuid = player.getUniqueId();
        int stack = stacks.getOrDefault(uuid, 0);

        float baseSpeed = 0.2f;
        float bonusSpeed = 0.02f * stack;
        player.setWalkSpeed(baseSpeed + bonusSpeed);

        String bar = "\uD83D\uDCA8".repeat(stack);
        player.sendActionBar(Component.text("Viento acumulado: ")
                .append(Component.text(bar).color(TextColor.color(0x00CFFF))));

        if (stack == 10) {
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0, 1, 0), 1);
        }
    }


    @EventHandler
    public void onPlayerUsePipe(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();


        if (isPuertoViejoPipe(item, plugin)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.GREEN + "Usaste la Pipa de Puerto Viejo... sientes algo extraño.");
                applyRandomEffect(player);
                reduceUse(player, item);
        }
    }

    @EventHandler
    public void onPlayerShoot(EntityShootBowEvent event) {
        // Verificar si el tirador es un jugador
        if (event.getEntity() instanceof Player player) {
            // Verificar si el artículo utilizado es una ballesta
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.CROSSBOW) {
                fireCustomCrossbow(player,item,plugin);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.NETHERITE_SWORD) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey swordKey = new NamespacedKey(plugin, "tankSword");

        if (meta.getPersistentDataContainer().has(swordKey, PersistentDataType.BYTE)) {
            double currentHealth = player.getHealth();
            double originalDamage = event.getDamage();

            // Multiplicador: Por ejemplo, vida 20 → ×2, vida 10 → ×1.5, etc.
            double multiplier = 1.0 + (currentHealth / 20.0);

            double newDamage = originalDamage * multiplier;
            event.setDamage(newDamage);
        }
    }




    private int getRemaningUses(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.getOrDefault(usosKey, PersistentDataType.INTEGER, 0);
    }

    @EventHandler
    private void inmStar(PlayerDeathEvent event){
        useStar(plugin,event);
    }

    @EventHandler
    private void entityKilled(EntityDeathEvent event){
        chargeStar(plugin,event);
    }

    private void reduceUse(Player player, ItemStack item) {
        int usosRestantes = getRemaningUses(item);
        if (usosRestantes <= 1) {
            player.getInventory().removeItem(item);
            player.sendMessage(ChatColor.RED + "Tu pipa se ha acabado...");
            return;
        }

        // Reducir usos y actualizar el item
        usosRestantes--;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(usosKey, PersistentDataType.INTEGER, usosRestantes);

        // Actualizar lore con los usos restantes
        List<String> lore = meta.getLore();
        int i = 0;
        for(String s : lore){
            if(s.contains("Jalones")){
                lore.set(i, ChatColor.AQUA + "Jalones: " + usosRestantes);
            }
            i++;
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        Entity entity = event.getHitEntity();
        if (!(entity instanceof Player)) return;

        Player player = (Player) entity;
        ItemStack shield = player.getInventory().getItemInOffHand();

        if (!isReflectShield(shield, plugin)) return;
        if (!player.isBlocking()) return;

        if (event.getEntity() instanceof Arrow arrow) {
            LivingEntity shooter = (arrow.getShooter() instanceof LivingEntity) ? (LivingEntity) arrow.getShooter() : null;

            // Crear nueva flecha reflejada
            Arrow returnedArrow = player.launchProjectile(Arrow.class);
            returnedArrow.setShooter(player);
            returnedArrow.setCritical(arrow.isCritical());
            returnedArrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);

            if (shooter != null) {
                // Si hay un atacante, dirigir la flecha hacia él
                returnedArrow.setVelocity(shooter.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(arrow.getVelocity().length()));
            } else {
                // Si no hay atacante, simplemente invertir la dirección
                returnedArrow.setVelocity(arrow.getVelocity().multiply(-1));
            }

            // Reducir la durabilidad del escudo
            reduceDurability(shield);
        }
    }

    private void reduceDurability(ItemStack item) {
        if (!(item.getItemMeta() instanceof Damageable)) return;

        Damageable meta = (Damageable) item.getItemMeta();
        int currentDamage = meta.getDamage();
        meta.setDamage(currentDamage + 1); // Reduce la durabilidad (ajustable)
        item.setItemMeta(meta);
    }


    @EventHandler
    public void onMapachoBlade(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (item == null) return;

        if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
            if (isMapachoBlade(item, plugin)) {
                launchMapachoBlade(player, item);
            }
        }
    }

    @EventHandler
    public void onHealShard(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (item == null) return;

        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if (isReparationShardItem(plugin, item)) {
                healItems(player);
                item.setAmount(item.getAmount() - 1);
            }
        }
    }

    private void healItems(Player player) {
        for (ItemStack stack : player.getInventory()) {
            if (stack != null && stack.hasItemMeta()) {
                ItemMeta meta = stack.getItemMeta();
                if (meta instanceof Damageable damageable) {
                    damageable.resetDamage();
                    stack.setItemMeta(meta);
                }
            }
        }
    }

    @EventHandler
    public void onStorSwordHit(EntityDamageByEntityEvent event) {
        // Verifica si el atacante es un jugador
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();

            // Verifica si el ítem del jugador es la "Espada de las Tormentas"
            if (isStormSword(item, plugin) && event.getEntity() instanceof Enemy enemy) {
                // Genera una probabilidad de 30% para invocar un rayo
                if (new Random().nextDouble() < 0.3) { // 30% de probabilidad
                    // Llama a un rayo en la ubicación de la entidad afectada
                    enemy.getWorld().strikeLightning(enemy.getLocation());
                }
            }
        }
    }

    @EventHandler
    public void rechargeShards(PlayerFishEvent event){
        if(event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)){
            Player player = event.getPlayer();
            for(ItemStack stack : player.getInventory()){
                if(stack != null){
                    if(isTeleShardItem(plugin,stack)){
                        setTeleShardBatery(plugin,stack,getTeleShardBatery(plugin,stack) + 10);
                    }
                    if(isTeleVillagerShardItem(plugin,stack)){
                        setTeleVillagerShardBatery(plugin,stack,getTeleVillagerShardBatery(plugin,stack) + 10);
                    }
                }
            }
        }
    }

    @EventHandler
    public void whenPlayerOwed(PlayerJoinEvent event){
        Player player = event.getPlayer();
        new GachaHolo(plugin).showHolo(player);
        if(owedTokenPlayers.contains(player)){
            owedTokenPlayers.remove(player);
            event.getPlayer().getInventory().addItem(plugin.prizeManager.gachaToken());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws MalformedURLException {
        Player player = (Player) event.getWhoClicked();
        RPGPlayer rpgPlayer = plugin.rpgManager.getPlayer(player);
        ClassRPG classRPG = rpgPlayer.getClassRpg();
        String title = event.getView().getTitle();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) {
            return;
        }

        if(title.contains("Premios Gachapon")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();

        // Solo procesamos si el entity es un ArmorStand
        if (!(entity instanceof ArmorStand armorStand)) {
            return;
        }

        Set<String> tags = armorStand.getScoreboardTags();
        Player player = event.getPlayer();


        if(tags.contains("Gachapon")){
            ItemStack stack = event.getPlayer().getInventory().getItem(event.getHand());
            int amount = event.getPlayer().getInventory().getItem(event.getHand()).getAmount();
            if(plugin.prizeManager.hasGatchaToken(stack)){
                Gachapon gachapon = new Gachapon(plugin, player);

                if(!Objects.equals(activeGachapon, gachapon)){
                    player.sendMessage(ChatColor.RED + "Hay otro gachapon activo");
                    return;
                }

                gachapon.trigger(new Location(player.getWorld(),290.5,81,437.5));
                player.getInventory().getItem(event.getHand()).setAmount(amount - 1);
            }
            else{
                player.sendMessage(ChatColor.RED + "No tienes Gacha Tokens");
                player.openInventory(plugin.prizeManager.createPrizeInventory());
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        // Verificamos que la animación sea el swing (left click)
        if (event.getAnimationType() != PlayerAnimationType.ARM_SWING) {
            return;
        }

        // Realizamos un ray trace desde la vista del jugador
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        // Definimos un rango (por ejemplo, 5 bloques)
        RayTraceResult result = player.getWorld().rayTraceEntities(eyeLocation, direction, 5, entity ->
                entity instanceof ArmorStand && entity.getScoreboardTags().contains("Gachapon")
        );

        if (result != null && result.getHitEntity() instanceof ArmorStand) {
            // Se encontró un ArmorStand con la etiqueta "Gachapon": se abre el inventario de premios
            player.openInventory(plugin.prizeManager.createPrizeInventory());
        }
    }

    public void launchMapachoBlade(Player player, ItemStack blade) {
        World world = player.getWorld();
        Location startLoc = player.getEyeLocation().add(0,-1.4,0);
        Vector direction = startLoc.getDirection().normalize();

        // Crear el ArmorStand que mostrará la espada (blade)
        ArmorStand displayBlade = world.spawn(startLoc, ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setMarker(true);
            armorStand.setGravity(false);
            armorStand.setGlowing(true);
            armorStand.setCustomNameVisible(false);
            // Colocar la espada en la cabeza
            armorStand.getEquipment().setHelmet(blade);
        });

        // Runnable para mover la espada (blade)
        new BukkitRunnable() {
            int distanceTravelled = 0;
            final double speed = 1.0; // Bloques por tick (ajusta según necesites)

            @Override
            public void run() {
                // Si se ha recorrido el máximo o el ArmorStand ya no es válido, removemos
                if (!displayBlade.isValid() || distanceTravelled >= 40) {
                    displayBlade.remove();
                    cancel();
                    return;
                }

                // Actualizar la posición
                Location current = displayBlade.getLocation();
                current.add(direction.clone().multiply(speed));
                displayBlade.teleport(current);
                distanceTravelled += speed;

                // Calcular el yaw basado en el vector de dirección
                float yaw = (float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
                // Convertir el yaw a radianes para el head pose
                double yawRad = Math.toRadians(yaw);
                // Asumimos que no queremos rotación en X (pitch) ni en Z (roll), solo en Y (yaw)
                displayBlade.setHeadPose(new EulerAngle(0, yawRad, 0));

                // Revisar colisiones con bloques
                Block blockInFront = current.clone().add(0, 1, 0).getBlock();
                if (blockInFront.getType().isSolid()) {
                    explodeEffect(current);
                    displayBlade.remove();
                    cancel();
                    return;
                }

                // Revisar colisiones con entidades (excluyendo el propio ArmorStand)
                for (Entity entity : displayBlade.getNearbyEntities(0.5, 0.5, 0.5)) {
                    if (!(entity instanceof ArmorStand) && entity instanceof LivingEntity) {
                        if (entity instanceof Enemy enemy) {
                            enemy.damage(10, player); // Hacer daño al enemigo
                            explodeEffect(current);
                            displayBlade.remove();
                            cancel();
                            return;
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private void explodeEffect(Location loc) {
        loc.getWorld().spawnParticle(Particle.CRIT, loc, 20, 0.5, 0.5, 0.5, 0.1);
    }

    @EventHandler
    public void onTeleporter(PlayerInteractEvent event){
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        if(hand != null){
            ItemStack stack = player.getInventory().getItem(event.getHand());
            if(isTeleporterItem(plugin,stack)){
                switch (event.getAction()){
                    case LEFT_CLICK_AIR:
                    case LEFT_CLICK_BLOCK:
                        teleportWithAnimation(plugin,player,stack);
                        break;
                    case RIGHT_CLICK_AIR:
                    case RIGHT_CLICK_BLOCK:
                        setStoredLocation(plugin,stack,player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Se ha guardado la localización correctamente");
                        break;
                }
            }
        }

    }


    @EventHandler
    public void onShieldHit(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player player){
            ItemStack activeItem = player.getActiveItem();
            String effect = activeItem.getPersistentDataContainer().getOrDefault(shieldEffect, PersistentDataType.STRING, "");
            if(event.getDamager() instanceof Enemy enemy){
                switch (effect){
                    case "fire":
                        enemy.setFireTicks(60);
                        break;
                    case "ice":
                        enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 255, false, false, false));
                }
            }
        }
    }



    @EventHandler
    public void jetPackBoost(PlayerInputEvent event) {
        Player player = event.getPlayer();
        // Si ya hay un Runnable activo para este jugador, no lo volvemos a crear
        if (activeJetPackRunnables.containsKey(player)) return;

        BukkitRunnable jetPackRunnable = new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                // Si el jugador ya no presiona salto o ya no tiene el jetpack, detenemos la tarea.
                if (!player.getCurrentInput().isJump() || !isJetPack() || !hasCoal()) {
                    if(!hasCoal() && player.getCurrentInput().isJump() && isJetPack()){
                        player.sendMessage(ChatColor.RED + "No tienes carbón");
                    }
                    activeJetPackRunnables.remove(player);
                    cancel();
                    return;
                }

                if(ticks == 0){
                    playSound(player);
                }

                if(ticks % 40 == 0){
                    removeCoal();
                }

                // Obtenemos la dirección hacia donde mira el jugador y la normalizamos.
                Vector boostDirection = player.getLocation().getDirection().normalize();

                double boostPower = 0.1;
                // Aplicamos el impulso sumándolo a la velocidad actual del jugador.
                player.setVelocity(player.getVelocity().add(boostDirection.multiply(boostPower)));

                spawnJetpackParticles(player);

                ticks++;
            }

            // Verifica si el jugador tiene equipado el jetpack (por ejemplo, mediante un tag en el item del pecho)
            public boolean isJetPack() {
                NamespacedKey jetPackKey = new NamespacedKey(plugin, "jetPack");
                ItemStack chestplate = player.getInventory().getChestplate();
                if (chestplate == null || !chestplate.hasItemMeta()) return false;
                return chestplate.getItemMeta().getPersistentDataContainer().has(jetPackKey, PersistentDataType.BOOLEAN) && player.getPose().equals(Pose.FALL_FLYING);
            }

            public boolean hasCoal(){
                for(ItemStack stack : player.getInventory()){
                    if(stack != null){
                        if(stack.getType().equals(Material.COAL)){
                            return true;
                        }
                    }
                }
                return false;
            }

            public void removeCoal(){
                for(ItemStack stack : player.getInventory()){
                    if(stack != null){
                        if(stack.getType().equals(Material.COAL)){
                            stack.setAmount(stack.getAmount() - 1);
                            return;
                        }
                    }
                }
            }
        };

        // Guardamos y ejecutamos el Runnable
        activeJetPackRunnables.put(player, jetPackRunnable);
        jetPackRunnable.runTaskTimer(plugin, 0L, 1L); // Ejecuta cada tick
    }

    public void playSound(Player player){
        player.getWorld().playSound(player.getLocation(),Sound.ENTITY_BLAZE_SHOOT,0.4f,1);
    }

    private void spawnJetpackParticles(Player player) {
        // Usamos la ubicación del ojo para aproximar la posición de las elytras.
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().normalize();
        // Calculamos el vector derecho (cross product con el vector up)
        Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        // Ajustamos las posiciones: 0.5 bloques a cada lado y bajamos un poco para que queden en el área de las alas
        Location rightWing = eyeLoc.clone().add(right.multiply(1)).subtract(0, 0.3, 0);
        Location leftWing = eyeLoc.clone().subtract(right.multiply(1)).subtract(0, 0.3, 0);

        // Lanza partículas tipo FLAME (puedes usar otro tipo) con ligeros offsets para efecto
        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, rightWing, 1, 0.1, 0.1, 0.1, 0.01);
        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, leftWing, 1, 0.1, 0.1, 0.1, 0.01);
        player.getWorld().spawnParticle(Particle.FLAME, leftWing, 3, 0.1, 0.1, 0.1, 0.01);
        player.getWorld().spawnParticle(Particle.FLAME, rightWing, 3, 0.1, 0.1, 0.1, 0.01);
        player.getWorld().spawnParticle(Particle.CLOUD, leftWing, 1, 0.01, 0.01, 0.01, 0.001);
        player.getWorld().spawnParticle(Particle.CLOUD, rightWing, 1, 0.01, 0.01, 0.01, 0.001);
    }


    @EventHandler
    public void rocketBoots(PlayerInputEvent event) {
        Player player = event.getPlayer();
        // Evitamos duplicar runnables para el mismo jugador
        if (activeBootRunnables.containsKey(player)) return;

        if(player.isOnGround()) return;

        if(player.getLocation().subtract(0,2,0).getBlock().getType().equals(Material.AIR)){
            return;
        }

        BukkitRunnable rocketRunnable = new BukkitRunnable() {
            int ticksHeld = 0; // Cuenta los ticks durante los cuales se ha mantenido el boost

            @Override
            public void run() {
                // Si el jugador deja de presionar espacio o ya no tiene las botas cohete, cancelamos la tarea.
                if (!player.getCurrentInput().isJump() || !hasRocketBoots(player) || player.isFlying()) {
                    activeBootRunnables.remove(player);
                    cancel();
                    return;
                }
                // Si se supera el máximo de 2 segundos (40 ticks), cancelamos el boost.
                if (ticksHeld >= 40) {
                    activeBootRunnables.remove(player);
                    cancel();
                    return;
                }
                ticksHeld++;

                // Aplica el boost vertical (ignora cualquier componente horizontal)
                double boostPower = 0.3; // Ajusta este valor para modificar la fuerza del impulso


                player.setVelocity(player.getVelocity().setY(boostPower));

                    // Genera partículas para el efecto (por ejemplo, nubes)
                player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getLocation(), 1);

            }

            // Método para verificar si el jugador tiene equipadas las botas cohete y está en el aire.
            public boolean hasRocketBoots(Player player) {
                NamespacedKey rocketBootKey = new NamespacedKey(plugin, "rocketBoots");
                ItemStack boots = player.getInventory().getBoots();
                if (boots == null || !boots.hasItemMeta()) return false;
                // Verifica que el item tenga el tag "rocketBoots" y que el jugador no esté en tierra
                return boots.getItemMeta().getPersistentDataContainer().has(rocketBootKey, PersistentDataType.BOOLEAN) && !player.isOnGround();
            }
        };

        activeBootRunnables.put(player, rocketRunnable);
        rocketRunnable.runTaskTimer(plugin, 0L, 1L); // Ejecuta cada tick
    }

    private final Map<UUID, Long> lastRightClickTime = new HashMap<>();
    // Para cada jugador se guarda el runnable activo que mueve la entidad.
    private final Map<UUID, BukkitRunnable> activeTeleShards = new HashMap<>();

    // Tiempo límite (en milisegundos) sin actualizar el clic derecho para considerar que se soltó.
    private static final long RELEASE_THRESHOLD = 200;
    // Distancia máxima de raytrace (10 bloques)
    private static final double MAX_DISTANCE = 10.0;

    @EventHandler
    public void onPlayerTeleShard(PlayerInteractEvent event) {
        // Solo procesamos clic derecho (en aire o en bloque)
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        EquipmentSlot slot = event.getHand();
        if (slot == null) return;

        ItemStack stack = player.getInventory().getItem(slot);
        // Si el item NO es ni TeleShard ni TeleVillagerShard, salimos
        if (!isTeleShardItem(plugin, stack) && !isTeleVillagerShardItem(plugin, stack)) {
            return;
        }

        // Actualizamos el tiempo del último clic derecho
        lastRightClickTime.put(uuid, System.currentTimeMillis());

        Location eyeLoc = player.getEyeLocation();
        Vector dir = eyeLoc.getDirection().normalize();
        World world = player.getWorld();
        RayTraceResult result = null;

        // Dependiendo del tipo de shard, raytrace de Enemy o Villager
        if (isTeleShardItem(plugin, stack)) {
            result = world.rayTraceEntities(eyeLoc, dir, 5, entity -> entity instanceof Enemy);
        } else if (isTeleVillagerShardItem(plugin, stack)) {
            result = world.rayTraceEntities(eyeLoc, dir, 5, entity -> entity instanceof Villager);
        }

        if (result != null && result.getHitEntity() instanceof LivingEntity livingTarget) {
            // Resaltamos la entidad
            livingTarget.setGlowing(true);

            // Si no hay ya un runnable activo para este jugador, lo creamos
            if (!activeTeleShards.containsKey(uuid)) {
                // Guardamos la distancia inicial entre la entidad y el ojo del jugador
                double initialDistance = livingTarget.getLocation().distance(player.getEyeLocation());

                BukkitRunnable moverRunnable = new BukkitRunnable() {
                    int ticks = 0;
                    ItemStack currentStack;

                    @Override
                    public void run() {
                        currentStack = player.getInventory().getItem(slot);
                        if (currentStack == null || currentStack.getType() == Material.AIR) {
                            livingTarget.setGlowing(false);
                            activeTeleShards.remove(uuid);
                            cancel();
                            return;
                        }
                        int battery = 0;
                        if (livingTarget instanceof Enemy) {
                            battery = TeleShardPrize.getTeleShardBatery(plugin, currentStack);
                        } else if (livingTarget instanceof Villager) {
                            battery = TeleVillagerShardPrize.getTeleVillagerShardBatery(plugin, currentStack);
                        }

                        // Si ha pasado demasiado tiempo desde el último clic derecho o la batería se agotó, cancelar
                        long lastClick = lastRightClickTime.getOrDefault(uuid, 0L);
                        if (System.currentTimeMillis() - lastClick > RELEASE_THRESHOLD || !isValid(battery)) {
                            livingTarget.setGlowing(false);
                            activeTeleShards.remove(uuid);
                            cancel();
                            return;
                        }

                        // Cancelamos si la entidad ya no es válida
                        if (!livingTarget.isValid() || livingTarget.isDead()) {
                            activeTeleShards.remove(uuid);
                            cancel();
                            return;
                        }

                        ticks++;
                        // Cada 20 ticks (1 segundo) se reduce la batería en 1
                        if (ticks % 20 == 0) {
                            if (isTeleShardItem(plugin, currentStack)) {
                                setTeleShardBatery(plugin, currentStack, battery - 1);
                            } else if (isTeleVillagerShardItem(plugin, currentStack)) {
                                setTeleVillagerShardBatery(plugin, currentStack, battery - 1);
                            }
                        }

                        // Calculamos la ubicación deseada: el jugador mantiene la distancia inicial en la dirección de su vista (sin tomar en cuenta la altura)
                        Location playerEye = player.getEyeLocation();
                        Vector desiredDirection = playerEye.getDirection().normalize();
                        Location desiredLocation = playerEye.clone().add(desiredDirection.multiply(initialDistance));

                        // Verificar si hay obstáculos entre el jugador y la posición deseada
                        double distanceBetween = livingTarget.getLocation().distance(playerEye);
                        if (world.rayTraceBlocks(playerEye, desiredDirection, distanceBetween) != null) {
                            // Si hay obstáculo, no mover la entidad
                            return;
                        }

                        // Calcular la diferencia entre la posición actual de la entidad y la posición deseada
                        Location currentTargetLoc = livingTarget.getLocation();
                        Vector toDesired = desiredLocation.toVector().subtract(currentTargetLoc.toVector());

                        // Aplicar una velocidad proporcional a esa diferencia (factor ajustable)
                        double factor = 1.5;
                        Vector velocity = toDesired.multiply(factor);

                        livingTarget.setVelocity(velocity);
                    }

                    private boolean isValid(int battery) {
                        return (isTeleShardItem(plugin, currentStack) || isTeleVillagerShardItem(plugin, currentStack))
                                && battery > 0;
                    }
                };

                activeTeleShards.put(uuid, moverRunnable);
                moverRunnable.runTaskTimer(plugin, 0L, 1L); // Ejecuta cada tick
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVillagerClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager)) return;
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItem(event.getHand());
        if (stack == null) return;

        if (isTeleVillagerShardItem(plugin, stack)) {
            event.setCancelled(true);
            // Si se abre alguna interfaz del villager, forzamos su cierre:
            player.closeInventory();
        }
    }
}
