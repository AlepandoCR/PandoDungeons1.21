package pandoClass.gachaPon.prizes;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import pandoClass.gachaPon.Gachapon;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static pandoClass.gachaPon.Gachapon.activeGachapon;
import static pandoClass.gachaPon.Gachapon.owedTokenPlayers;
import static pandoClass.gachaPon.prizes.mithic.MapachoBladePrize.isMapachoBlade;

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
        if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
            ItemStack item = event.getItem();
            if (item == null) return;
            if (isMapachoBlade(item, plugin)) {
                Player player = event.getPlayer();
                launchMapachoBlade(player);
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

                gachapon.trigger(new Location(player.getWorld(),290,82,437));
                player.getInventory().getItem(event.getHand()).setAmount(amount - 1);
            }
            else{
                player.sendMessage(ChatColor.RED + "No tienes Gacha Tokens");
            }
            event.setCancelled(true);
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
            @Override
            public void run() {
                // Si el jugador ya no presiona salto o ya no tiene el jetpack, detenemos la tarea.
                if (!player.getCurrentInput().isJump() || !isJetPack()) {
                    activeJetPackRunnables.remove(player);
                    cancel();
                    return;
                }


                // Obtenemos la dirección hacia donde mira el jugador y la normalizamos.
                Vector boostDirection = player.getLocation().getDirection().normalize();

                double boostPower = 0.1;
                // Aplicamos el impulso sumándolo a la velocidad actual del jugador.
                player.setVelocity(player.getVelocity().add(boostDirection.multiply(boostPower)));
            }

            // Verifica si el jugador tiene equipado el jetpack (por ejemplo, mediante un tag en el item del pecho)
            public boolean isJetPack() {
                NamespacedKey jetPackKey = new NamespacedKey(plugin, "jetPack");
                ItemStack chestplate = player.getInventory().getChestplate();
                if (chestplate == null || !chestplate.hasItemMeta()) return false;
                return chestplate.getItemMeta().getPersistentDataContainer().has(jetPackKey, PersistentDataType.BOOLEAN) && player.getPose().equals(Pose.FALL_FLYING);
            }
        };

        // Guardamos y ejecutamos el Runnable
        activeJetPackRunnables.put(player, jetPackRunnable);
        jetPackRunnable.runTaskTimer(plugin, 0L, 1L); // Ejecuta cada tick
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
}
