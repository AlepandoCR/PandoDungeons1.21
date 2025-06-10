package pandoClass.campsListener;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import pandoClass.ExpandableClassMenu;
import pandoClass.InitMenu;
import pandoClass.RPGPlayer;
import pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.util.*;

import static pandoClass.classes.archer.skills.DoubleJumSkill.doubleJumping;
import static pandoToros.game.ArenaMaker.isRedondelWorld;
import static pandodungeons.Utils.LocationUtils.isDungeonWorld;

public class CampsListener implements Listener {

    private final PandoDungeons plugin;

    private final NamespacedKey expKey;

    private final NamespacedKey lvlKey;

    private final NamespacedKey coinsKey;

    private final Map<UUID, Long> lastUpdateTime = new HashMap<>();

    private final long UPDATE_COOLDOWN_MS = 100; // 100ms entre actualizaciones por mob (~10 veces por segundo)

    public CampsListener(PandoDungeons plugin) {
        this.plugin = plugin;
        expKey = new NamespacedKey(plugin,"exp");
        lvlKey = new NamespacedKey(plugin, "lvl");
        this.coinsKey = new NamespacedKey(plugin, "coins");;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){

    }

    @EventHandler
    public void onEnemySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Enemy enemy) {
            String worldName = event.getLocation().getWorld().getName();
            if (!isDungeonWorld(worldName) && !isRedondelWorld(worldName)) {
                switch (entity.getEntitySpawnReason()) {
                    case SPAWNER_EGG, COMMAND, CUSTOM -> {
                        // Ejecutar async si es posible
                        Bukkit.getScheduler().runTaskLater(plugin, () -> transformNoName(enemy), 1L);
                        return;
                    }
                    case SPAWNER -> {
                        return;
                    }
                    default -> {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> enemyTransformation(enemy), 1L);
                    }
                }
            }else if(isDungeonWorld(worldName)){
                Bukkit.getScheduler().runTaskLater(plugin, () -> transformDungeon(enemy), 1L);
            }
        }
    }


    private void transformNoName(Enemy enemy) {
        List<Entity> near = enemy.getNearbyEntities(40,40,40).stream().filter(entity -> entity instanceof Player).limit(3).toList();
        int avrgLvl = getAvrgLevel(near);
        int exp = calculateExpFromLvl(avrgLvl, enemy);
        double finalHp = (int) calculateHpFromLvlAndApply(avrgLvl, enemy);
        addKey(expKey, PersistentDataType.INTEGER, enemy, exp);
        addKey(lvlKey,PersistentDataType.INTEGER,enemy,avrgLvl);
        addKey(coinsKey, PersistentDataType.INTEGER, enemy, Math.max(1, (int) (avrgLvl/2.5)));
    }

    private void transformDungeon(Enemy enemy) {
        List<Entity> near = enemy.getNearbyEntities(40,40,40).stream().filter(entity -> entity instanceof Player).limit(3).toList();
        int avrgLvl = getAvrgLevel(near);
        if(avrgLvl < 50)return;
        int exp = calculateExpFromLvl(avrgLvl, enemy);
        double finalHp = (int) calculateHpFromLvlAndApply(avrgLvl, enemy);
        addKey(expKey, PersistentDataType.INTEGER, enemy, exp);
        addKey(lvlKey,PersistentDataType.INTEGER,enemy,avrgLvl);
        addKey(coinsKey, PersistentDataType.INTEGER, enemy, Math.max(1, (int) (avrgLvl/2.5)));
    }


    @EventHandler
    public void onEnemyHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Enemy enemy)) return;

        if (event.getDamager() instanceof Player player) {
            double damage = event.getFinalDamage();
            handleEnemyDamage(enemy, damage, player);
        }
    }

    @EventHandler
    public void onProtalChange(EntityPortalEnterEvent event){
        if(event.getEntity() instanceof Player player){
            if(event.getPortalType().equals(PortalType.ENDER) && 30 > plugin.rpgManager.getPlayer(player).getLevel()){
                if (player.hasPermission("pandodungeons.end") || FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) return;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTp(PlayerTeleportEvent event){
        if(event.getTo().getWorld().getName().equals("world_the_end") && 30 > plugin.rpgManager.getPlayer(event.getPlayer()).getLevel()){
            if (event.getPlayer().hasPermission("pandodungeons.end") || FloodgateApi.getInstance().isFloodgatePlayer(event.getPlayer().getUniqueId())) return;
            event.setCancelled(true);
        }
    }

    /**
     * Actualiza la experiencia del enemigo, restando la cantidad de daño del valor actual.
     *
     * @param enemy  El enemigo que recibió daño.
     * @param damage El daño recibido.
     */
    private void handleEnemyDamage(Enemy enemy, double damage, Player player) {
        UUID enemyId = enemy.getUniqueId();
        long now = System.currentTimeMillis();

        // Throttle por mob
        Long lastUpdate = lastUpdateTime.get(enemyId);
        if (lastUpdate != null && (now - lastUpdate) < UPDATE_COOLDOWN_MS) {
            return;
        }
        lastUpdateTime.put(enemyId, now);

        var data = enemy.getPersistentDataContainer();

        Integer currentExp = data.get(expKey, PersistentDataType.INTEGER);
        Integer currentLevel = data.get(lvlKey, PersistentDataType.INTEGER);
        Integer coins = data.get(coinsKey, PersistentDataType.INTEGER);

        if (currentExp == null || currentLevel == null || coins == null) return;


        double actualDamage = Math.min(damage, enemy.getHealth());

        if (coins > 0) {
            long coinReward = (int) Math.min(actualDamage, coins);
            plugin.rpgManager.getPlayer(player).addCoins(coinReward);

            data.set(coinsKey, PersistentDataType.INTEGER, coins - (int) coinReward);
        }

        if (currentExp > 0) {
            int expReward = (int) Math.min(actualDamage, currentExp);
            plugin.rpgManager.getPlayer(player).addExp(expReward);

            data.set(expKey, PersistentDataType.INTEGER, currentExp - expReward);
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event){
        plugin.getDisplayManager().removeAllDisplays();
    }


    public String getEmojiForLevel(int level) {
        return switch (level / 25) { // Divide el nivel entre 20 para agrupar en rangos
            case 0 -> "🔰 " + ChatColor.GREEN + ChatColor.BOLD + level;
            case 1 -> "💩 " + ChatColor.YELLOW + ChatColor.BOLD + level;
            case 2 -> "🔥 " + ChatColor.RED + ChatColor.BOLD + level;
            default -> "⚡ " + ChatColor.AQUA + ChatColor.BOLD + level;
        };
    }


    private void enemyTransformation(LivingEntity entity){
        List<Entity> near = entity.getNearbyEntities(40,40,40).stream().filter(e -> e instanceof Player).limit(10).toList();
        int avrgLvl = getAvrgLevel(near);
        double finalHp = (int) calculateHpFromLvlAndApply(avrgLvl, entity);
        int exp = calculateExpFromLvl((int) finalHp,entity);
        addKey(expKey, PersistentDataType.INTEGER, entity, exp);
        addKey(lvlKey,PersistentDataType.INTEGER,entity,avrgLvl);
        addKey(coinsKey, PersistentDataType.INTEGER, entity, Math.max(1, (int) (avrgLvl/2.5)));
        entity.setCustomName(getEmojiForLevel(avrgLvl) + ChatColor.WHITE + ChatColor.BOLD + " " + entity.getType());
    }

    private int calculateExpFromLvl(int finalHp, LivingEntity entity){
        if(entity instanceof Enderman){
            return finalHp /10;
        }
        return finalHp;
    }

    private int getAvrgLevel(List<Entity> entities) {
        int totalLevel = 0;
        int count = 0;
        for (Entity entity : entities) {
            if (entity instanceof Player player) {
                RPGPlayer rpgPlayer = plugin.rpgManager.getPlayer(player);
                totalLevel += rpgPlayer.getLevel();
                count++;
            }
        }
        return (count > 0) ? totalLevel / count : 0;
    }
    
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) throws MalformedURLException {
        Entity entity = event.getRightClicked();

        // Solo procesamos si el entity es un ArmorStand
        if (!(entity instanceof ArmorStand)) {
            return;
        }

        ArmorStand armorStand = (ArmorStand) entity;
        Set<String> tags = armorStand.getScoreboardTags();
        Player player = event.getPlayer();

        if (tags.contains("RPGshop")) {
            // Acción para la tienda
            player.openInventory(new ExpandableClassMenu(player,plugin).createExpandableClassMenu());
            // Aquí puedes agregar la lógica para abrir el inventario de la tienda u otra acción.
            event.setCancelled(true);
        } else if (tags.contains("RPGSkillMenu")) {
            // Acción para el menú de habilidades
            player.openInventory(plugin.initMenu.createClassSelectionMenu(player, InitMenu.Reason.SKILL_MENU));
            // Aquí puedes agregar la lógica para mostrar el menú de habilidades u otra acción.
            event.setCancelled(true);
        }
    }

    private final Map<Player, Integer> doubleJumpCount = new HashMap<>();

    // Método auxiliar para determinar si el jugador está en el suelo.
    private boolean isOnGround(Player player) {
        return player.isOnGround();
    }



    private final Set<UUID> justDoubleJumped = new HashSet<>();

    @EventHandler
    public void doubleJump(PlayerInputEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        RPGPlayer rpgPlayer = plugin.rpgManager.getPlayer(player);

        if (!event.getInput().isJump()) return;
        if (!doubleJumping.contains(player)) return;
        if (isOnGround(player)) {
            doubleJumpCount.put(player, 0);
            return;
        }

        int jumps = doubleJumpCount.getOrDefault(player, 0);
        if (jumps >= 1) return;

        doubleJumpCount.put(player, jumps + 1);
        player.setVelocity(player.getVelocity().setY(0.7));

        // MARCAR jugador como que hizo doble salto
        justDoubleJumped.add(uuid);

        Bukkit.getScheduler().runTaskLater(plugin, () -> justDoubleJumped.remove(uuid), 30L);

        int level = rpgPlayer.getSecondSkilLvl();
        double force = Math.min(0.1 * level, 1.0);
        for (Entity entity : player.getNearbyEntities(5, 3, 5)) {
            if (entity instanceof LivingEntity && entity != player) {
                Vector direction = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                entity.setVelocity(direction.multiply(force));
            }
        }
        player.getWorld().spawnParticle(Particle.WHITE_SMOKE, player.getLocation(), 20, 0.5, 0.5, 0.5, 0.0);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && justDoubleJumped.contains(uuid)) {
            event.setCancelled(true);
            justDoubleJumped.remove(uuid);
        }
    }



    private double calculateHpFromLvlAndApply(int lvl, LivingEntity entity){
        double baseHP = entity.getMaxHealth();

        double multiply = lvl * 0.025;

        double finalHp = baseHP + (baseHP * multiply);

        entity.setMaxHealth(finalHp);

        entity.heal(baseHP * multiply);

        return finalHp;
    }

    <P, C> void addKey(NamespacedKey key, PersistentDataType<P, C> type, LivingEntity entity, @NotNull C value){
        if(entity.getPersistentDataContainer().has(key)){
            entity.getPersistentDataContainer().remove(key);
        }
        entity.getPersistentDataContainer().set(key, type, value);
    }

    <P, C> C getData(NamespacedKey key, PersistentDataType<P, C> type, LivingEntity entity){
        C aux = null;
        if(entity.getPersistentDataContainer().has(key)){
            aux = entity.getPersistentDataContainer().get(key, type);
        }
        return aux;
    }
}
