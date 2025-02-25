package pandodungeons.pandodungeons.Listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.CustomEntities.Ball.BallArmadillo;
import pandodungeons.pandodungeons.CustomEntities.CompanionLoops.Companion;
import pandodungeons.pandodungeons.Elements.CompanionMenu;
import pandodungeons.pandodungeons.Game.enchantments.souleater.SoulEaterEnchantment;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.Game.PlayerStatsManager;
import pandodungeons.pandodungeons.Game.RoomManager;
import pandodungeons.pandodungeons.Utils.CompanionUtils;
import pandodungeons.pandodungeons.Utils.LocationUtils;
import pandodungeons.pandodungeons.Utils.PlayerParty;
import pandodungeons.pandodungeons.Utils.StructureUtils;
import pandodungeons.pandodungeons.commands.Management.CommandQueue;
import pandodungeons.pandodungeons.commands.game.CompanionSelection;

import java.net.MalformedURLException;
import java.util.*;
import java.util.function.BiFunction;

import static pandodungeons.pandodungeons.CustomEntities.pandaMount.CustomPanda.setPandaRider;
import static pandodungeons.pandodungeons.Game.enchantments.garabiThor.garabiThor.handleGarabiThor;
import static pandodungeons.pandodungeons.Game.enchantments.souleater.SoulEaterEnchantment.*;
import static pandodungeons.pandodungeons.Utils.CompanionUtils.loadCompanions;
import static pandodungeons.pandodungeons.Utils.CompanionUtils.openUnlockCompanionMenu;
import static pandodungeons.pandodungeons.Utils.ItemUtils.*;
import static pandodungeons.pandodungeons.Utils.LocationUtils.hasActiveDungeon;
import static pandodungeons.pandodungeons.Utils.LocationUtils.isDungeonWorld;
import static pandodungeons.pandodungeons.Utils.ParticleUtils.spawnParticleCircle;
import static pandodungeons.pandodungeons.Utils.ParticleUtils.spawnSoulCircle;

public class PlayerEventListener implements Listener {
    PandoDungeons plugin = PandoDungeons.getPlugin(PandoDungeons.class);
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RoomManager roomManager = RoomManager.getActiveRoomManager(player);
        for(World world : Bukkit.getWorlds()){
            if(world.getName().contains(player.getName().toLowerCase(Locale.ROOT)) && isDungeonWorld(world.getName())){
                StructureUtils.removeDungeon(player.getName().toLowerCase(Locale.ROOT), plugin);
                if(CommandQueue.getInstance().isEmpty()){
                    CommandQueue.getInstance().dequeue();
                }
            }
        }
        if (roomManager != null) {
            roomManager.handlePlayerQuit(event);
            StructureUtils.removeDungeon(player.getName().toLowerCase(Locale.ROOT), plugin);
            if(CommandQueue.getInstance().isEmpty()) {
                CommandQueue.getInstance().dequeue();
            }
        }
        PlayerStatsManager.getPlayerStatsManager(player).saveStats();
    }

    private final Map<Player, Long> lastClickTime = new HashMap<>();

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if(event.getRightClicked() instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) event.getRightClicked();
            setPandaRider(player, entity);
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

    private final Map<Player, Long> ballCooldown = new HashMap<>();

    @EventHandler
    public void interactBlock(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if(block != null){
                Player player = event.getPlayer();
                if(player.getItemInHand().asOne().equals(soccerBall(1))){
                    long currentTime = System.currentTimeMillis();
                    long lastTime = ballCooldown.getOrDefault(player, 0L);
                    if (currentTime - lastTime < (200)) {
                        return;
                    }
                    ballCooldown.put(player, currentTime);
                    Location location = block.getLocation();
                    BallArmadillo ball = new BallArmadillo(net.minecraft.world.entity.EntityType.ARMADILLO, ((CraftWorld)location.getWorld()).getHandle());
                    ball.spawnLocation(location.add(0,1,0));
                    player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
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
            } else if (item.asOne().equals(snifferUnlockItem(1))) {
                if (!CompanionUtils.hasUnlockedCompanion(player, "sniffer")) {
                    event.getItem().setAmount(event.getItem().getAmount() - 1);
                    CompanionUtils.unlockCompanion(player, "sniffer", 1);
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "Ya has desbloqueado el compañero Sniffer");
                }
            } else if(item.asOne().equals(pufferFishUnlockItem(1))){
                if (!CompanionUtils.hasUnlockedCompanion(player, "pufferfish")) {
                    event.getItem().setAmount(event.getItem().getAmount() - 1);
                    CompanionUtils.unlockCompanion(player, "pufferfish", 1);
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "Ya has desbloqueado el compañero Puffer Fish");
                }
            }
        }
    }

    @EventHandler
    public void stopConsumes(PlayerItemConsumeEvent event){
        ItemStack item = event.getItem();
        if(item.asOne().equals(physicalPrestigeNoAmount())){
            event.setCancelled(true);
        }
        if(item.asOne().getType().equals(Material.CHORUS_FRUIT) && hasActiveDungeon(event.getPlayer().getUniqueId().toString())){
            event.setCancelled(true);
        }
    }

    private final Map<Player, Long> lastPrestigeConsume = new HashMap<>();
    private final Map<Player, Long> copperGum = new HashMap<>();
    private final Map<Player, Long> garabiThor = new HashMap<>();
    private final Map<Player, Long> soulAbility = new HashMap<>();
    private final Map<Player, BukkitRunnable> actionBarRunnables = new HashMap<>();

    @EventHandler
    public void changeItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());

        // Cancel any existing action bar update tasks
        if (actionBarRunnables.containsKey(player)) {
            actionBarRunnables.get(player).cancel();
            player.sendActionBar(" ");
        }
        handleSoulHabilities(newItem, player);
        handleGarabiThorBar(newItem, player);
    }

    private void handleGarabiThorBar(ItemStack newItem, Player player) {
        if (isGarabiThor(newItem)) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    long lastTime = garabiThor.getOrDefault(player, 0L);
                    long cooldown = 3000; // 3t seconds cooldown
                    long timeLeft = cooldown - (currentTime - lastTime);
                    int batery = (int) getBateria(newItem);
                    String bateryString = String.valueOf(batery) + "W";
                    if(batery == 1000){
                        bateryString = "1Kw";
                    }

                    if(!newItem.equals(player.getItemInHand())){
                        this.cancel();
                        return;
                    }

                    if(batery < 75){
                        player.sendActionBar(ChatColor.RED.toString() + "\uD83E\uDEAB" + ChatColor.DARK_RED + ChatColor.BOLD + batery + ChatColor.RED + "\uD83E\uDEAB");
                    }else{
                        if (timeLeft <= 0) {
                            player.sendActionBar((ChatColor.GOLD.toString() + ChatColor.BOLD + "⚡ " + ChatColor.AQUA + "[" + ChatColor.GREEN + "▊▊▊" + ChatColor.AQUA + bateryString + ChatColor.GREEN + "▊▊▊" + ChatColor.AQUA + "]" + ChatColor.GOLD + ChatColor.BOLD + " ⚡"));
                        }else{
                            int progressBars = (int) ((timeLeft / (double) cooldown) * 10);
                            progressBars = Math.max(0, Math.min(progressBars, 10));

                            String progressBar = (ChatColor.GREEN + "▊").repeat(10 - progressBars) + (ChatColor.RED.toString() + "▬").repeat(progressBars);
                            player.sendActionBar((ChatColor.GOLD.toString() + ChatColor.BOLD + "⚡ " + ChatColor.RESET + ChatColor.AQUA + "[" + ChatColor.RESET + progressBar + ChatColor.AQUA + "]" + ChatColor.GOLD + ChatColor.BOLD + " ⚡"));
                        }
                    }
                }
            };
            runnable.runTaskTimer(plugin, 0L, 10L);
            actionBarRunnables.put(player, runnable);
        }
    }

    private void handleSoulHabilities(ItemStack newItem, Player player) {
        if (hasSoulEater(newItem)) {
            BukkitRunnable runnable = new BukkitRunnable() {
                private static String line = ChatColor.WHITE.toString() + ChatColor.BOLD + " - ";
                @Override
                public void run() {
                    String progressBar = "";
                    if(!newItem.equals(player.getItemInHand())){
                        this.cancel();
                        return;
                    }
                    if(getSoulCount(newItem) >= 100){
                        progressBar = progressBar + ChatColor.DARK_AQUA.toString() + "[" + ChatColor.GOLD.toString() + "DDD" + ChatColor.DARK_AQUA.toString() + "]" + ChatColor.AQUA.toString() + "Curación" + ChatColor.DARK_AQUA + ChatColor.BOLD + " 50" + ChatColor.DARK_AQUA + " \uD83D\uDC7B";
                    }
                    if(getSoulCount(newItem) >= 200){
                        progressBar = progressBar + line + ChatColor.DARK_AQUA.toString() + "[" + ChatColor.GOLD.toString() + "DID" + ChatColor.DARK_AQUA.toString() + "]" + ChatColor.AQUA.toString() + "Inmovilizar" + ChatColor.DARK_AQUA + ChatColor.BOLD + " 100" + ChatColor.DARK_AQUA + " \uD83D\uDC7B";
                    }
                    if(getSoulCount(newItem) >= 300){
                        progressBar = progressBar + line + ChatColor.DARK_AQUA.toString() + "[" + ChatColor.GOLD.toString() + "DDI" + ChatColor.DARK_AQUA.toString() + "]" + ChatColor.AQUA.toString() + "Berserk" + ChatColor.DARK_AQUA + ChatColor.BOLD + " 200" + ChatColor.DARK_AQUA + " \uD83D\uDC7B";
                    }
                    if((getSoulCount(newItem) >= 500)){
                        progressBar = progressBar + line + ChatColor.DARK_AQUA.toString() + "[" + ChatColor.GOLD.toString() + "DII" + ChatColor.DARK_AQUA.toString() + "]" + ChatColor.AQUA.toString() + "Soul Army" + ChatColor.DARK_AQUA + ChatColor.BOLD + " 500" + ChatColor.DARK_AQUA + " \uD83D\uDC7B";
                    }
                    player.sendActionBar(progressBar);
                }
            };
            runnable.runTaskTimer(plugin, 0L, 10L); // Update every 10 ticks (0.5 seconds)
            actionBarRunnables.put(player, runnable);
        }
    }

    private final Map<Player, String> combinations = new HashMap<>();

    @EventHandler
    public void consumables(PlayerInteractEvent event) throws MalformedURLException {
        long currentTime = System.currentTimeMillis();
        Player player = event.getPlayer();
        if(event.getItem() != null){
            ItemStack item = event.getItem();
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if(isGarabiThor(item)){
                    long lastTime = garabiThor.getOrDefault(player, 0L);
                    if (currentTime - lastTime < 3000 || getBateria(item) < 75) { // 200 ms intervalo de tiempo para prevenir múltiples registros
                        return;
                    }
                    removeBatery(item, 75D);
                    handleGarabiThorBar(item, player);
                    garabiThor.put(player, currentTime);
                    handleGarabiThor(player, item);
                }   
            }
            if (hasSoulEater(item)) {
                long lastTime = soulAbility.getOrDefault(player, 0L);

                // Prevenir múltiples registros en un corto período de tiempo
                if (currentTime - lastTime < 200) {
                    return;
                }

                // Restablecer combinaciones si han pasado menos de 2 segundos desde la última acción
                if (currentTime - lastTime >= 2000) {
                    combinations.remove(player);
                }

                soulAbility.put(player, currentTime);
                String combination = combinations.getOrDefault(player, "");

                // Actualizar combinaciones basadas en las acciones del jugador
                if(combination.startsWith("D")){
                    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        combination += "I";
                    }
                }
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    combination += "D";
                }

                // Almacenar la nueva combinación
                combinations.put(player, combination);

                if(combination.toCharArray().length > 3){
                    combination = "";
                    combinations.remove(player);
                }
                // Enviar la combinación actual al jugador
                if(combination.startsWith("D")){
                    player.sendTitle((ChatColor.AQUA + "[" + ChatColor.GOLD + ChatColor.BOLD + combination + ChatColor.AQUA + "]"), "", 0, 20, 10);
                }

                // Verificar si la combinación es "III" para activar la habilidad de curación
                if (combination.equals("DDD")) {
                    healAbility(player, item);
                    combinations.remove(player); // Restablecer la combinación después de activar la habilidad
                }

                if(combination.equals("DID")){
                    freezeAbility(player, item);
                    combinations.remove(player);
                }

                if(combination.equals("DDI")){
                    berserkAttack(player, item);
                    combinations.remove(player);
                }
                if(combination.equals("DII")){
                    soulArmyAbility(player,item);
                    combinations.remove(player);
                }
            }
            if(item.asOne().equals(physicalPrestigeNoAmount())){
                long lastTime =     lastPrestigeConsume.getOrDefault(player, 0L);
                if (currentTime - lastTime < 200) { // 200 ms intervalo de tiempo para prevenir múltiples registros
                    return;
                }
                lastPrestigeConsume.put(player, currentTime);
                PlayerStatsManager statsManager = PlayerStatsManager.getPlayerStatsManager(player);
                statsManager.setPrestige(statsManager.getPrestige() + 1);
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Se añadió un prestigio a tu cuenta");
            } else if (item.asOne().equals(comperGum(1))) {
                long lastTime = copperGum.getOrDefault(player, 0L);
                if (currentTime - lastTime < 2000) { // 2000 ms intervalo de tiempo para prevenir múltiples registros
                    return;
                }
                copperGum.put(player, currentTime);
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200,0));
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadCompanions(player);
        RoomManager.handlePlayerJoin(event);
        PlayerStatsManager.getPlayerStatsManager(player).loadStats();
        if(hasActiveDungeon(player.getUniqueId().toString())){
            player.performCommand("dungeons leave");
        }
    }

    @EventHandler
    public void onPlayerQuitParty(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Obtener la party del jugador si está en una
        PlayerParty playerParty = plugin.playerPartyList.getPartyByMember(player);
        if (playerParty != null) {
            if (playerParty.isOwner(player)) {
                // Si el owner se desconecta, transferir el ownership o disolver la party
                if (playerParty.getMembers().size() > 1) {
                    Player newOwner = playerParty.getMembers().stream()
                            .filter(p -> !p.equals(player))
                            .findFirst().orElse(null);
                    if (newOwner != null) {
                        playerParty.setOwner(newOwner);
                        newOwner.sendMessage("Has sido promovido a owner de la party ya que " + player.getName() + " se ha desconectado.");
                        playerParty.removeMember(player);
                    }
                } else {
                    // Disolver la party si no hay más miembros
                    playerParty.disbandParty();
                    plugin.playerPartyList.removeParty(playerParty);

                }
            } else {
                // Remover al jugador desconectado de la party
                playerParty.removeMember(player);
                for (Player member : playerParty.getMembers()) {
                    member.sendMessage(player.getName() + " se ha desconectado y ha sido removido de la party.");
                }
            }
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
            LivingEntity target = event.getEntity();

            if(target.getScoreboardTags().contains("bolaFut")){
                event.getDrops().clear();
                target.getWorld().dropItemNaturally(target.getLocation(), soccerBall(1));
            }

            if(isGarabiThor(player.getItemInHand())){
                if(target instanceof Creeper){
                    addBateria(player.getItemInHand());
                }
            }


            try {
                handleSoulEaterEffect(player, target);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }



            if(hasActiveDungeon(player.getUniqueId().toString()) || (plugin.playerPartyList.isMember(player) && hasActiveDungeon(plugin.playerPartyList.getPartyByMember(player).getOwner().getUniqueId().toString()))) {
                if(Companion.hasActiveCompanion(player)){
                    if(!Companion.getCompanion(player).getEntityType().equals(EntityType.ALLAY)){
                        event.getDrops().clear();
                    }
                }else{
                    event.getDrops().clear();
                }

                PlayerStatsManager statsManager = PlayerStatsManager.getPlayerStatsManager(player);
                spawnSoulCircle(event.getEntity().getLocation(),2,10);
                statsManager.addMobKill();
            }
        }
    }


    /**
     * Maneja el evento PrepareAnvilEvent para aplicar el encantamiento Soul Eater.
     * @param event El evento de preparación del yunque.
     */
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();
        ItemStack firstItem = anvil.getItem(0);
        ItemStack secondItem = anvil.getItem(1);

        if (firstItem == null || secondItem == null) {
            return;
        }

        // Verificar si el segundo ítem es el libro de encantamiento Soul Eater
        if (secondItem.equals(createSoulEaterEnchantedBook())) {
            // Crear una copia del primer ítem para aplicar el encantamiento
            ItemStack resultItem = firstItem.clone();
            applySoulEater(resultItem);

            // Establecer el resultado en el yunque
            event.setResult(resultItem);
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
            if (isDungeonWorld(toWorld.getName())) {

                boolean isPartOfDungeon = hasActiveDungeon(player.getUniqueId().toString());

                if(plugin.playerPartyList.isMember(player) && hasActiveDungeon(plugin.playerPartyList.getPartyByMember(player).getOwner().getUniqueId().toString())){
                    isPartOfDungeon = true;
                }

                if(!isPartOfDungeon){
                    if(!hasActiveDungeon(player.getUniqueId().toString())){
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "No puedes hacerte tp a otras dungeons");
                        return;
                    }
                    // Si el mundo de destino es diferente al mundo de dungeon del jugador, cancela el TP
                    if (!toWorld.getName().contains(player.getName().toLowerCase(Locale.ROOT))) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "No puedes hacerte tp a otras dungeons");
                    }
                }else{
                    if(!plugin.playerPartyList.isOwner(player)){
                        if(plugin.playerPartyList.getPartyByMember(player) == null){
                            return;
                        }
                        if(!toWorld.getName().contains(plugin.playerPartyList.getPartyByMember(player).getOwner().getName().toLowerCase(Locale.ROOT))){
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.RED + "No puedes salir de la dungeon, si deseas salir debes decirle al owner");
                        }
                    }
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

                if (isDungeonWorld(world.getName())) {
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
        if (event.getEntity() instanceof Bee bee) {
            if (bee.getScoreboardTags().contains("beefight") && event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                event.setCancelled(true);
            }
        }
    }
}