package pandodungeons.pandodungeons.Listeners;

import net.minecraft.network.chat.ChatDecorator;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.pandodungeons.CustomEntities.CompanionLoops.Companion;
import pandodungeons.pandodungeons.Elements.CompanionMenu;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.Game.PlayerStatsManager;
import pandodungeons.pandodungeons.Game.RoomManager;
import pandodungeons.pandodungeons.Utils.CompanionUtils;
import pandodungeons.pandodungeons.Utils.LocationUtils;
import pandodungeons.pandodungeons.Utils.StructureUtils;
import pandodungeons.pandodungeons.commands.game.CompanionSelection;

import java.net.MalformedURLException;
import java.util.*;

import static pandodungeons.pandodungeons.Utils.CompanionUtils.loadCompanions;
import static pandodungeons.pandodungeons.Utils.CompanionUtils.openUnlockCompanionMenu;
import static pandodungeons.pandodungeons.Utils.ItemUtils.*;
import static pandodungeons.pandodungeons.Utils.ParticleUtils.spawnParticleCircle;

public class PlayerEventListener implements Listener {
    JavaPlugin plugin = PandoDungeons.getPlugin(PandoDungeons.class);
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RoomManager roomManager = RoomManager.getActiveRoomManager(player);
        if (roomManager != null) {
            roomManager.handlePlayerQuit(event);
            StructureUtils.removeDungeon(player.getName().toLowerCase(Locale.ROOT), plugin);
        }
        PlayerStatsManager.getPlayerStatsManager(player).saveStats();
    }

    private final Map<Player, Long> lastClickTime = new HashMap<>();

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if(event.getRightClicked() instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) event.getRightClicked();
            double health = entity.getHealth();
            if (entity.getScoreboardTags().contains("companionMob")) {
                // Previene múltiples registros del evento
                long currentTime = System.currentTimeMillis();
                long lastTime = lastClickTime.getOrDefault(player, 0L);
                if (currentTime - lastTime < 200) { // 200 ms intervalo de tiempo para prevenir múltiples registros
                    return;
                }
                lastClickTime.put(player, currentTime);
                if (player.getInventory().getItemInMainHand().getType().equals(Material.GLOW_BERRIES)) {
                    if (health < entity.getMaxHealth()) {
                        entity.setHealth(Math.min(health + 2.0D, entity.getMaxHealth())); // Asegúrate de no superar la salud máxima
                        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                        spawnParticleCircle(entity.getLocation(), 1, 10);
                    }
                } else {
                    ChatColor chatColor = ChatColor.GREEN;
                    if (entity.getHealth() < 3) {
                        chatColor = ChatColor.RED;
                    } else if (entity.getHealth() < 5) {
                        chatColor = ChatColor.YELLOW;
                    }
                    player.sendMessage(chatColor + "Tu compañero tiene " + health + "/" + entity.getMaxHealth() + " de vida");
                }
            }
        }
    }

    private final Map<Entity, Long> polarShear = new HashMap<>();


    @EventHandler
    public void polarBearFurEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        World world = entity.getWorld();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (entity instanceof PolarBear && item.getType() == Material.SHEARS) {
            long currentTime = System.currentTimeMillis();
            long lastTime = polarShear.getOrDefault(entity, 0L);
            if (currentTime - lastTime < (60000 * 2)) {
                return;
            }
            polarShear.put(entity, currentTime);
            Random random = new Random();
            int quantity = random.nextInt(3) + 1;
            world.dropItem(entity.getLocation().add(0, 1, 0), polarBearFur(quantity));

            // Reduce la durabilidad de las tijeras en 1
            item.setDurability((short) (item.getDurability() + 1));
            if (item.getDurability() >= item.getType().getMaxDurability()) {
                player.getInventory().removeItem(item);
            }

            player.playSound(player.getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1, 1);
        }
    }


    @EventHandler
    public void companionUnlockMenu(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if(event.getRightClicked() instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) event.getRightClicked();
            if(entity instanceof Villager){
                if(entity.getScoreboardTags().contains("companionUnlockMenu")){
                    openUnlockCompanionMenu(player);
                }
            }
        }
    }

    @EventHandler
    public void noPlaceUnlockItems(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasCustomModelData() && meta.getCustomModelData() == 420 && item.getType() == Material.HEAVY_CORE) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void unlockCompanion(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if(item != null) {
            if (item.asOne().equals(armadilloUnlockItem(1))) {
                if (!CompanionUtils.hasUnlockedCompanion(player, "armadillo")) {
                    event.getItem().setAmount(event.getItem().getAmount() - 1);
                    CompanionUtils.unlockCompanion(player, "armadillo", 1);
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "Ya has desbloqueado el compañero Armadillo");
                }
            } else if (item.asOne().equals(breezeUnlockItem(1))) {
                if (!CompanionUtils.hasUnlockedCompanion(player, "breeze")) {
                    event.getItem().setAmount(event.getItem().getAmount() - 1);
                    CompanionUtils.unlockCompanion(player, "breeze", 1);
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "Ya has desbloqueado el compañero Breeze");
                }
            } else if (item.asOne().equals(allayUnlockItem(1))) {
                if (!CompanionUtils.hasUnlockedCompanion(player, "allay")) {
                    event.getItem().setAmount(event.getItem().getAmount() - 1);
                    CompanionUtils.unlockCompanion(player, "allay", 1);
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "Ya has desbloqueado el compañero Allay");
                }
            } else if (item.asOne().equals(osoUnlockItem(1))) {
                if (!CompanionUtils.hasUnlockedCompanion(player, "oso")) {
                    event.getItem().setAmount(event.getItem().getAmount() - 1);
                    CompanionUtils.unlockCompanion(player, "oso", 1);
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "Ya has desbloqueado el compañero Oso");
                }
            }
        }
    }

    @EventHandler
    public void stopPrestigeEating(PlayerItemConsumeEvent event){
        if(event.getItem().equals(physicalPrestigeNoAmount())){
            event.setCancelled(true);
        }
    }

    private final Map<Player, Long> lastPrestigeConsume = new HashMap<>();

    @EventHandler
    public void depositPrestige(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getItem() != null){
            if(event.getItem().asOne().equals(physicalPrestigeNoAmount())){
                long currentTime = System.currentTimeMillis();
                long lastTime = lastPrestigeConsume.getOrDefault(player, 0L);
                if (currentTime - lastTime < 200) { // 200 ms intervalo de tiempo para prevenir múltiples registros
                    return;
                }
                lastPrestigeConsume.put(player, currentTime);
                PlayerStatsManager statsManager = PlayerStatsManager.getPlayerStatsManager(player);
                statsManager.setPrestige(statsManager.getPrestige() + 1);
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Se añadió un prestigio a tu cuenta");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadCompanions(player);
        RoomManager.handlePlayerJoin(event);
        PlayerStatsManager.getPlayerStatsManager(player).loadStats();
        if(LocationUtils.hasActiveDungeon(player.getUniqueId().toString())){
            player.performCommand("dungeons leave");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws MalformedURLException {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && event.getView().getTitle().equals(ChatColor.BOLD + "Selecciona tu Compañero")) {
            event.setCancelled(true); // Cancelar el evento para evitar sacar o meter ítems manualmente

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasCustomModelData()) {
                String companionType = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                // Obtener el nombre del companion
                if (companionType != null && !companionType.isEmpty()) {
                    // Aquí puedes manejar lo que sucede al interactuar con un ítem específico en el inventario
                    int companionLevel = CompanionUtils.getCompanionLevel(player, companionType.toLowerCase());
                    CompanionMenu menuCompa = new CompanionMenu(player, companionType);
                    player.openInventory(menuCompa.menu());
                }
            }
        }
        if(clickedInventory != null && CompanionUtils.isCompanionType(ChatColor.stripColor(event.getView().getTitle()))){
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasCustomModelData()) {
                String item = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                String companionType;
                switch (item){
                    case "Atrás":
                        CompanionSelection.openMenu(player);
                        break;
                    case "Subir de nivel":
                        upgradeCompanion(player, event);
                        companionType = CompanionUtils.searchCompanionType(ChatColor.stripColor(event.getView().getTitle()));
                        if(companionType != null){
                            CompanionMenu menuCompa = new CompanionMenu(player, companionType.toUpperCase(Locale.ROOT));
                            player.openInventory(menuCompa.menu());
                        }
                        break;
                    case "Seleccionar":
                        companionType = CompanionUtils.searchCompanionType(ChatColor.stripColor(event.getView().getTitle()));
                        if(companionType != null){
                            CompanionUtils.selectCompanion(player, companionType);
                            player.sendMessage(ChatColor.BLUE + "Has seleccionado al compañero: " + companionType);
                        }
                    default:
                        break;
                }
            }
        }
    }

    private void upgradeCompanion(Player player, InventoryClickEvent event){
        PlayerStatsManager statsManager = new PlayerStatsManager(player);
        String companionType;
        companionType = CompanionUtils.searchCompanionType(ChatColor.stripColor(event.getView().getTitle()));
        if(companionType != null){
            int level = CompanionUtils.getCompanionLevel(player, companionType.toLowerCase(Locale.ROOT));

            int costo = (int) (level * 0.5) + 1;
            if(statsManager.getPrestige() >= costo){
                statsManager.setPrestige(statsManager.getPrestige() - costo);
                CompanionUtils.addCompanionLevel(player, companionType);
            }else{
                player.sendMessage(ChatColor.DARK_RED + "No tienes prestigio suficiente para esta mejora");
            }
        }
    }

    @EventHandler
    public void onPlayerKillMob(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            if(LocationUtils.hasActiveDungeon(player.getUniqueId().toString())) {
                if(Companion.hasActiveCompanion(player)){
                    if(!Companion.getCompanion(player).getEntityType().equals(EntityType.ALLAY)){
                        event.getDrops().clear();
                    }
                }else{
                    event.getDrops().clear();
                }

                PlayerStatsManager statsManager = PlayerStatsManager.getPlayerStatsManager(player);
                statsManager.addMobKill();
            }
        }
    }


    private void getPlayerOutOfDungeon(Player player){
        Location playerSpawnPoint = player.getBedSpawnLocation();
        if(playerSpawnPoint != null){
            player.teleport(playerSpawnPoint);
        }else{
            World spawn = Bukkit.getWorld("spawn");
            if(spawn != null){
                Location spawnSpawn = spawn.getSpawnLocation();
                player.teleport(spawnSpawn);
            }else{
                player.damage(1000);
            }
        }
    }

    @EventHandler
    public void onPlayerTp(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        World toWorld = event.getTo().getWorld();
        if(!player.isOp()){
            // Comprueba si el destino es un mundo de dungeon
            if (LocationUtils.isDungeonWorld(toWorld.getName())) {

                if(!LocationUtils.hasActiveDungeon(player.getUniqueId().toString())){
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "No puedes hacerte tp a otras dungeons");
                    return;
                }
                // Si el mundo de destino es diferente al mundo de dungeon del jugador, cancela el TP
                if (!toWorld.getName().contains(player.getName().toLowerCase(Locale.ROOT))) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "No puedes hacerte tp a otras dungeons");
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            if (event.getEntity().getShooter() instanceof Player) {
                Player player = (Player) event.getEntity().getShooter();
                World world = player.getWorld();

                if (LocationUtils.isDungeonWorld(world.getName())) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.DARK_PURPLE + "No puedes usar enderpearls aqui");
                }
            }
        }
    }

    @EventHandler
    public void onServerShutDown(PluginDisableEvent event) {

    }
    @EventHandler
    public void onBeeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Bee) {
            Bee bee = (Bee) event.getEntity();
            if (bee.getScoreboardTags().contains("beefight") && event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                event.setCancelled(true);
            }
        }
    }
}