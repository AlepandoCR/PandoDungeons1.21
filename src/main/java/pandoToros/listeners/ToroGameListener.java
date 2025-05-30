package pandoToros.listeners;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pandoToros.game.ToroStatManager;
import pandodungeons.PandoDungeons;

import static pandoToros.game.ArenaMaker.extractUsername;
import static pandoToros.game.ArenaMaker.isRedondelWorld;
import static pandoToros.game.RandomBox.isProtectedPlayer;
import static pandoToros.game.RandomBox.unProtectPlayer;
import static pandoToros.game.RedondelGame.hasActiveRedondel;
import static pandoToros.game.modes.PlatformMode.platformPlayers;


public class ToroGameListener implements Listener {

    PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    @EventHandler
    private void buildBarricades(BlockPlaceEvent event){
        Player player = event.getPlayer();

        if(isRedondelWorld(player.getWorld().getName()) && !player.isOp()) {
            if (!event.getBlock().getType().equals(Material.BAMBOO_BLOCK) || event.getBlock().getLocation().getY() > -2) {
                event.setCancelled(true);

            }else{
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        event.getBlock().breakNaturally(false);
                    }
                }.runTaskLater(plugin,100);
            }
        }
    }

    @EventHandler
    public void dmgManagement(EntityDamageEvent event){
        if(isRedondelWorld(event.getEntity().getWorld().getName())){
            if(event.getEntity() instanceof Ravager){
                event.setCancelled(true);
            }
            if(event.getEntity() instanceof  Player player){
                if(isProtectedPlayer(player)){
                    event.setCancelled(true);

                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            if(isProtectedPlayer(player)) unProtectPlayer(player);
                            player.playSound(player, Sound.ITEM_MACE_SMASH_GROUND_HEAVY,1,1);
                        }
                    }.runTaskLater(plugin,60);
                }
            }
        }
    }

    @EventHandler
    public void totemOU(EntityResurrectEvent event){
        if(isRedondelWorld(event.getEntity().getWorld().getName())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void bucketUse(PlayerBucketEmptyEvent event){
        if(isRedondelWorld(event.getBlock().getWorld().getName())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionThrow(PlayerInteractEvent event) {

        if(isRedondelWorld(event.getPlayer().getWorld().getName())){
            // Verificar si el jugador tiene un ítem en la mano
            ItemStack item = event.getItem();
            if (item == null) return;

            // Verificar si es una poción arrojadiza o lingering
            Material type = item.getType();
            if (type == Material.SPLASH_POTION || type == Material.LINGERING_POTION) {
                // Cancelar la interacción y notificar al jugador
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "¡No puedes usar pociones arrojadizas o persistentes!");
            }
        }

    }

    @EventHandler
    public void leashUse(EntityMoveEvent event) {
        if (isRedondelWorld(event.getEntity().getWorld().getName())) {
            if (event.getEntity() instanceof Ravager ravager) {
                for (Entity entity : ravager.getNearbyEntities(1, 1, 1)) {
                    // Si la entidad es un FishingBobber
                    if (entity.getType().equals(EntityType.FISHING_BOBBER)) {
                        Location init = ravager.getLocation();
                        // Aplicar el efecto de Glow al Ravager
                        ravager.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 80, 1));

                        // Cancelar el movimiento de la entidad
                        event.setCancelled(true);

                        // Usar BukkitRunnable para hacer un seguimiento del tiempo
                        new BukkitRunnable() {
                            int count = 0;

                            @Override
                            public void run() {
                                // Incrementar el contador
                                count++;

                                // Si ha pasado 5 segundos (100 ticks), cancelar el task
                                if (count >= 100) {
                                    this.cancel();
                                    return;
                                }

                                // Mantener al Ravager en su posición inicial
                                ravager.teleport(init);
                            }
                        }.runTaskTimer(plugin, 1L, 1L);  // Ejecuta el task cada tick (1L)

                        return;  // Salir del bucle después de cancelar el movimiento
                    }
                }
            }
        }
    }



    @EventHandler
    public void onPlayerUseFishingRod(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Verificar si el jugador está en un mundo específico (por ejemplo, Redondel)
        if (isRedondelWorld(player.getWorld().getName())) {
            // Verifica si el jugador está usando una caña de pescar
            if (item != null && item.getType() == Material.FISHING_ROD) {
                ItemMeta meta = item.getItemMeta();

                // Verifica que el ItemMeta no sea nulo y tenga el CustomModelData 69
                if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 69) {
                    // Espera 5 segundos antes de romper la caña de pescar
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Eliminar todas las cañas de pescar con el CustomModelData 69
                            for (int i = 0; i < player.getInventory().getSize(); i++) {
                                ItemStack invItem = player.getInventory().getItem(i);
                                if (invItem != null && invItem.getType() == Material.FISHING_ROD) {
                                    ItemMeta invMeta = invItem.getItemMeta();
                                    if (invMeta != null && invMeta.hasCustomModelData() && invMeta.getCustomModelData() == 69) {
                                        player.getInventory().setItem(i, null);  // Eliminar la caña de pescar
                                    }
                                }
                            }
                            // Enviar un mensaje al jugador
                            player.sendMessage("¡Tu caña de pescar se ha roto!");
                            this.cancel();  // Cancelar la tarea después de ejecutar
                        }
                    }.runTaskLater(plugin, 100L);  // 100L es 5 segundos (5 * 20 ticks)
                } else {
                    // Si el CustomModelData no es 69, cancelamos el evento
                    event.setCancelled(true);
                }
            }
        }
    }



    @EventHandler
    public void onElytraGlide(EntityToggleGlideEvent event) {
        if(isRedondelWorld(event.getEntity().getWorld().getName())) {
            // Verificar si la entidad es un jugador
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();

                // Cancelar el planeo
                if (event.isGliding()) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "¡No puedes volar usando élytras!");
                }
            }
        }
    }

    @EventHandler
    private void breakBlocks(BlockBreakEvent event){
        Player player = event.getPlayer();

        if(isRedondelWorld(player.getWorld().getName()) && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void dieInGame(PlayerDeathEvent event){
        Player player = event.getPlayer();

        if(isRedondelWorld(player.getWorld().getName())){
            Entity entity = event.getDamageSource().getDirectEntity();
            if(entity instanceof Ravager ravager){
                if(ravager.getTarget() != null && ravager.getTarget() != player){
                    for(Player players : player.getWorld().getPlayers()){
                        players.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + ravager.getTarget().getName() + ChatColor.WHITE + " ha matado a " + ChatColor.AQUA + ChatColor.BOLD + player.getName());
                        ToroStatManager.getToroStatsManager((Player) ravager.getTarget()).addKill();
                    }
                }
            }
            event.setCancelled(true);
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    private void tpIntoRedondel(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();
        if (isRedondelWorld(worldName)) {
            if (!extractUsername(worldName).equalsIgnoreCase(player.getName())) {
                event.setCancelled(hasActiveRedondel(player));

            }
        }
    }

    @EventHandler
    private void eatOnRedondel(PlayerItemConsumeEvent event){
        if(isRedondelWorld(event.getPlayer().getWorld().getName())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void hitOnRedondel(PrePlayerAttackEntityEvent event) {
        if (isRedondelWorld(event.getPlayer().getWorld().getName())) {
            if (event.getAttacked() instanceof Player player) {
                if (platformPlayers.contains(event.getPlayer()) && platformPlayers.contains(player)) {
                    // Obtener la dirección del atacante al atacado
                    Player attacker = event.getPlayer();
                    Location attackerLoc = attacker.getLocation();
                    Location attackedLoc = player.getLocation();

                    // Calcular vector de dirección
                    Vector direction = attackedLoc.toVector().subtract(attackerLoc.toVector()).normalize();

                    // Añadir empuje horizontal (0.5 bloques)
                    direction.multiply(0.4);

                    // Añadir empuje vertical (0.2 bloques)
                    direction.setY(0.2);

                    // Aplicar la fuerza al jugador atacado
                    player.setVelocity(direction);
                }
            }
            event.setCancelled(true);
        }
    }


    @EventHandler
    private void arrowHitCancel(EntityShootBowEvent event){
        if(event.getEntity() instanceof Player player){
            if(isRedondelWorld(player.getWorld().getName())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void crossBowCancel(EntityLoadCrossbowEvent event){
        if(event.getEntity() instanceof Player player){
            if(isRedondelWorld(player.getWorld().getName())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            if (event.getEntity().getShooter() instanceof Player player) {
                World world = player.getWorld();

                if (isRedondelWorld(world.getName())) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.DARK_PURPLE + "No puedes usar enderpearls aqui");
                }
            }
        }
    }
}
