package pandoClass.campsListener;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import pandoClass.ExpandableClassMenu;
import pandoClass.InitMenu;
import pandoClass.RPGPlayer;
import pandodungeons.pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.util.*;

import static pandoClass.classes.archer.skills.DoubleJumSkill.doubleJumping;
import static pandoToros.game.ArenaMaker.isRedondelWorld;
import static pandodungeons.pandodungeons.Utils.LocationUtils.isDungeonWorld;

public class CampsListener implements Listener {

    private final PandoDungeons plugin;

    private final NamespacedKey expKey;

    private final NamespacedKey lvlKey;

    private final NamespacedKey coinsKey;

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
    public void onEnemySpawn(EntitySpawnEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof Enemy enemy){
            String worldName = event.getLocation().getWorld().getName();
            if(!isDungeonWorld(worldName) && !isRedondelWorld(worldName)){
                switch (entity.getEntitySpawnReason()){
                    case SPAWNER_EGG:
                    case COMMAND:
                    case CUSTOM:
                        transformNoName(enemy);
                        return;
                    case SPAWNER:
                        return;
                    default:
                        enemyTransformation(enemy);
                        break;
                }
            }
        }
    }

    private void transformNoName(Enemy enemy) {
        List<Entity> near = enemy.getNearbyEntities(100,100,100);
        int avrgLvl = getAvrgLevel(near);
        int exp = calculateExpFromLvl(avrgLvl, enemy);
        double finalHp = (int) calculateHpFromLvlAndApply(avrgLvl, enemy);
        addKey(expKey, PersistentDataType.INTEGER, enemy, exp);
        addKey(lvlKey,PersistentDataType.INTEGER,enemy,avrgLvl);
        addKey(coinsKey, PersistentDataType.INTEGER, enemy, Math.max(1, (int) (avrgLvl/2.5)));
    }

    @EventHandler
    public void onEnemyHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) {
            return;
        }
        // Solo nos interesa si la entidad dañada es un Enemy
        if (livingEntity instanceof Enemy enemy) {
            if(event.getDamager() instanceof Player source){
                updateEnemyExperience(enemy, event.getDamage(), source);
            }
        }
    }

    @EventHandler
    public void onProtalChange(EntityPortalEnterEvent event){
        if(event.getEntity() instanceof Player player){
            if(event.getPortalType().equals(PortalType.ENDER) && 50 > new RPGPlayer(player, plugin).getLevel()){
                if (player.hasPermission("pandodungeons.end")) return;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTp(PlayerTeleportEvent event){
        if(event.getTo().getWorld().getName().equals("world_the_end") && 50 > new RPGPlayer(event.getPlayer(), plugin).getLevel()){
            if (event.getPlayer().hasPermission("pandodungeons.end")) return;
            event.setCancelled(true);
        }
    }

    /**
     * Actualiza la experiencia del enemigo, restando la cantidad de daño del valor actual.
     *
     * @param enemy  El enemigo que recibió daño.
     * @param damage El daño recibido.
     */
    private void updateEnemyExperience(Enemy enemy, double damage, Player source) {
        Integer currentExp = getData(expKey, PersistentDataType.INTEGER, enemy);
        Integer currentLevel = getData(lvlKey, PersistentDataType.INTEGER, enemy);
        Integer coinsInEntity = getData(coinsKey, PersistentDataType.INTEGER, enemy);
        // Solo actualizamos si ambos valores existen
        if (currentExp == null || currentLevel == null) {
            return;
        }

        double health = enemy.getHealth();

        if(coinsInEntity > 0){
            addKey(coinsKey,PersistentDataType.INTEGER,enemy,coinsInEntity - 1);
            new RPGPlayer(source, plugin).addCoins(1);
        }

        // Calcula la nueva experiencia basada en la salud restante después del daño
        int newExp = (int) (health - damage);

        if(0 > newExp){
            newExp = 0;
        }

        if(damage > health){
            damage = health;
        }

        // Si la experiencia actual es mayor que 0, la actualizamos
        if (currentExp > 0) {
            addKey(expKey, PersistentDataType.INTEGER, enemy, newExp);
            new RPGPlayer(source, plugin).addExp((int) damage);
        }
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
        List<Entity> near = entity.getNearbyEntities(60,60,60);
        int avrgLvl = getAvrgLevel(near);
        double finalHp = (int) calculateHpFromLvlAndApply(avrgLvl, entity);
        int exp = calculateExpFromLvl(avrgLvl,entity);
        addKey(expKey, PersistentDataType.INTEGER, entity, exp);
        addKey(lvlKey,PersistentDataType.INTEGER,entity,avrgLvl);
        addKey(coinsKey, PersistentDataType.INTEGER, entity, Math.max(1, (int) (avrgLvl/2.5)));
        entity.setCustomName(getEmojiForLevel(avrgLvl) + ChatColor.WHITE + ChatColor.BOLD + " " + entity.getType());
    }

    private int calculateExpFromLvl(int lvl, LivingEntity entity){
        if(entity instanceof Enderman){
            return (int) entity.getHealth()/3;
        }
        return (int) entity.getHealth();
    }

    private int getAvrgLevel(List<Entity> entities) {
        int totalLevel = 0;
        int count = 0;
        for (Entity entity : entities) {
            if (entity instanceof Player player) {
                RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);
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



    @EventHandler
    public void doubleJump(PlayerInputEvent event) {
        Player player = event.getPlayer();

        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);

        // Verificar que el input sea de salto.
        if (!event.getInput().isJump()) {
            return;
        }

        if(!doubleJumping.contains(player)){
            return;
        }

        // Si el jugador está en el suelo, reiniciamos el contador y no realizamos doble salto.
        if (isOnGround(player)) {
            doubleJumpCount.put(player, 0);
            return;
        }

        // Si ya ha realizado el salto extra (doble salto) en el aire, no se permite otro.
        int jumps = doubleJumpCount.getOrDefault(player, 0);
        if (jumps >= 1) {
            return;
        }

        // Permitir el doble salto y aumentar el contador.
        doubleJumpCount.put(player, jumps + 1);

        // Dar un impulso vertical al jugador (simulando el doble salto).
        player.setVelocity(player.getVelocity().setY(0.7));



        // Usar el nivel del jugador para determinar la fuerza del empuje a las entidades cercanas.
        int level = rpgPlayer.getSecondSkilLvl();
        double force = Math.min(0.1 * level, 1.0);

        // Empujar las entidades cercanas lejos del jugador.
        for (Entity entity : player.getNearbyEntities(5, 3, 5)) {
            if (entity instanceof LivingEntity && entity != player) {
                @NotNull Vector direction = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                entity.setVelocity(direction.multiply(force));
            }
        }

        // Generar partículas de humo en la ubicación del jugador.
        player.getWorld().spawnParticle(Particle.WHITE_SMOKE, player.getLocation(), 20, 0.5, 0.5, 0.5, 0.0);
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
