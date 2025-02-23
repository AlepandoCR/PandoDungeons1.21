package pandoClass;

import org.bukkit.*;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.classes.archer.Archer;
import pandoClass.classes.assasin.Assasin;
import pandoClass.files.RPGPlayerDataManager;
import pandoClass.classes.tank.Tank;
import pandodungeons.pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.util.*;

import static pandoClass.InitMenu.INNIT_MENU_NAME;
import static pandoClass.InitMenu.createClassSelectionMenu;
import static pandoClass.classes.archer.skills.ArrowExplotionSkill.explosiveAmmo;
import static pandoClass.classes.archer.skills.SaveAmmoSkill.playersSavingAmmo;
import static pandoClass.classes.assasin.skills.LifeStealSkill.lifeStealingPlayers;
import static pandoClass.classes.assasin.skills.SilentStepSkill.silencedPlayers;
import static pandoClass.files.RPGPlayerDataManager.load;
import static pandoClass.files.RPGPlayerDataManager.save;

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
                cause == EntityDamageEvent.DamageCause.LIGHTNING;
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

        RPGPlayer rpgPlayer = new RPGPlayer(player);

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

            player.openInventory(createClassSelectionMenu(player, InitMenu.Reason.SKILL_MENU));
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
                    player.setWalkSpeed(0.2f);
                }
            }
        }
    }

    private void handleClassChange(RPGPlayer rpgPlayer, Player player, String classKey) throws MalformedURLException {
        String currentKey = rpgPlayer.getClassKey();
        if (currentKey == null) {
            rpgPlayer.setClassKey(classKey);
            player.closeInventory();
        } else if (!currentKey.equalsIgnoreCase(classKey)){
            if (rpgPlayer.getCoins() >= 500) {
                rpgPlayer.removeCoins(500);
                rpgPlayer.setClassKey(classKey);
                player.openInventory(createClassSelectionMenu(player, InitMenu.Reason.SHOP));
            } else {
                player.sendMessage("No tienes suficientes monedas para cambiar tu clase");
            }
        } else{
            player.sendMessage("Ya haz seleccionado esa clase");
        }
    }


    private static final double BASE_LIFESTEAL_PERCENT = 0.015; // 1.5% de robo de vida por nivel
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Verificar si el atacante es un jugador
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        RPGPlayer rpgPlayer = new RPGPlayer(player);


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
        RPGPlayer rpgPlayer = new RPGPlayer(player);

        if(event.getEntity() instanceof LivingEntity mob){
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
        RPGPlayer rpgPlayer = new RPGPlayer(player);
        String title = event.getView().getTitle();

        // Verificar si el inventario es el menú "Elección de clase"
        if (title.equals(INNIT_MENU_NAME) || title.contains("Cambiar clase")) {
            if(rpgPlayer.getClassKey() == null || rpgPlayer.getClassKey().isEmpty()){
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        try {
                            player.openInventory(createClassSelectionMenu(player, InitMenu.Reason.INNIT));
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.runTaskLater(plugin,2);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws MalformedURLException {
        Player player = event.getPlayer();
        player.setMaxHealth(20.0);
        player.setWalkSpeed(0.2f);

        RPGPlayer rpgPlayer = new RPGPlayer(player);

        if(rpgPlayer.getClassKey() == null || rpgPlayer.getClassKey().isEmpty()){
           player.openInventory(createClassSelectionMenu(player, InitMenu.Reason.INNIT));
           return;
        }

        if(!plugin.rpgPlayersList.containsKey(rpgPlayer)){
            String classKey = rpgPlayer.getClassKey();
            ClassRPG classRPG = new Assasin(rpgPlayer);
            classRPG = switch (classKey) {
                case "ArcherClass" -> new Archer(rpgPlayer);
                case "TankClass" -> new Tank(rpgPlayer);
                default -> classRPG;
            };
            plugin.rpgPlayersList.put(rpgPlayer.getPlayer(),classRPG);
        }

        save(rpgPlayer);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        // Verificar que el lanzador del proyectil sea un jugador.
        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }

        // Obtener el RPGPlayer para conocer su nivel.
        RPGPlayer rpgPlayer = new RPGPlayer(player);

        int level = rpgPlayer.getFirstSkilLvl();

        if(playersSavingAmmo.contains(player)){
            // Por ejemplo, cada nivel aporta 2% de probabilidad, hasta un máximo del 100%.
            double chance = 100 * (level * 0.02);

            // Realizar el roll aleatorio.
            if (random.nextDouble() < chance) {
                // Identificar el tipo de munición a reponer según el tipo de proyectil.
                Projectile projectile = event.getEntity();
                Material ammoMaterial = null;
                switch (projectile.getType()) {
                    case ARROW:
                        ammoMaterial = Material.ARROW;
                        break;
                    case SPECTRAL_ARROW:
                        ammoMaterial = Material.TIPPED_ARROW;
                        break;
                    default:
                        break;
                }

                if (ammoMaterial != null) {
                    // Reponer la munición añadiendo el item correspondiente al inventario.
                    player.getInventory().addItem(new ItemStack(ammoMaterial, 1));
                }
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
        RPGPlayer rpgPlayer = RPGPlayerDataManager.load(player);
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
}
