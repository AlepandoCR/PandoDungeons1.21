package pandoClass.gachaPon.prizes;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.gachaPon.Gachapon;
import pandoClass.gachaPon.prizes.mithic.TeleShardPrize;
import pandoClass.gachaPon.prizes.mithic.TeleVillagerShardPrize;
import pandodungeons.pandodungeons.PandoDungeons;

import javax.swing.plaf.SpinnerUI;
import java.net.MalformedURLException;
import java.util.*;

import static pandoClass.gachaPon.Gachapon.activeGachapon;
import static pandoClass.gachaPon.Gachapon.owedTokenPlayers;
import static pandoClass.gachaPon.prizes.epic.ReparationShardPrize.isReparationShardItem;
import static pandoClass.gachaPon.prizes.legendary.TeleportationHeartPrize.*;
import static pandoClass.gachaPon.prizes.mithic.MapachoBladePrize.isMapachoBlade;
import static pandoClass.gachaPon.prizes.mithic.TeleShardPrize.*;

public class PrizeListener implements Listener {
    private final PandoDungeons plugin;
    private final NamespacedKey shieldEffect;

    private final Map<Player, BukkitRunnable> activeJetPackRunnables = new HashMap<>();
    private final Map<Player, BukkitRunnable> activeBootRunnables = new HashMap<>();


    public PrizeListener(PandoDungeons plugin) {
        this.plugin = plugin;
        shieldEffect = new NamespacedKey(plugin,"shieldEffect");
    }

    @EventHandler
    public void onMapachoBlade(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (item == null) return;

        if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
            if (isMapachoBlade(item, plugin)) {
                launchMapachoBlade(player);
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
    public void whenPlayerOwed(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(owedTokenPlayers.contains(player)){
            owedTokenPlayers.remove(player);
            event.getPlayer().getInventory().addItem(plugin.prizeManager.gachaToken());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws MalformedURLException {
        Player player = (Player) event.getWhoClicked();
        RPGPlayer rpgPlayer = new RPGPlayer(player);
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

    public void launchMapachoBlade(Player player) {
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
            ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta meta = sword.getItemMeta();
            meta.setCustomModelData(345345545);
            sword.setItemMeta(meta);
            armorStand.getEquipment().setHelmet(sword);
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
        loc.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.0f, 1.0f);
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
        if (!TeleShardPrize.isTeleShardItem(plugin, stack) && !TeleVillagerShardPrize.isTeleVillagerShardItem(plugin, stack)) {
            return;
        }

        // Actualizamos el tiempo del último clic derecho
        lastRightClickTime.put(uuid, System.currentTimeMillis());

        Location eyeLoc = player.getEyeLocation();
        Vector dir = eyeLoc.getDirection().normalize();
        World world = player.getWorld();
        RayTraceResult result = null;

        // Dependiendo del tipo de shard, raytrace de Enemy o Villager
        if (TeleShardPrize.isTeleShardItem(plugin, stack)) {
            result = world.rayTraceEntities(eyeLoc, dir, 5, entity -> entity instanceof Enemy);
        } else if (TeleVillagerShardPrize.isTeleVillagerShardItem(plugin, stack)) {
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
                            if (TeleShardPrize.isTeleShardItem(plugin, currentStack)) {
                                TeleShardPrize.setTeleShardBatery(plugin, currentStack, battery - 1);
                            } else if (TeleVillagerShardPrize.isTeleVillagerShardItem(plugin, currentStack)) {
                                TeleVillagerShardPrize.setTeleVillagerShardBatery(plugin, currentStack, battery - 1);
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
                        return (TeleShardPrize.isTeleShardItem(plugin, currentStack) || TeleVillagerShardPrize.isTeleVillagerShardItem(plugin, currentStack))
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

        if (TeleVillagerShardPrize.isTeleVillagerShardItem(plugin, stack)) {
            event.setCancelled(true);
            // Si se abre alguna interfaz del villager, forzamos su cierre:
            player.closeInventory();
        }
    }
}
