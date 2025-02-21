package pandoClass;

import org.bukkit.*;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.files.RPGPlayerDataManager;
import pandoClass.tank.Tank;
import pandodungeons.pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.util.*;

import static pandoClass.InitMenu.createClassSelectionMenu;
import static pandoClass.archer.skills.ArrowExplotionSkill.explosiveAmmo;
import static pandoClass.archer.skills.SaveAmmoSkill.playersSavingAmmo;
import static pandoClass.assasin.skills.LifeStealSkill.lifeStealingPlayers;
import static pandoClass.assasin.skills.SilentStepSkill.silencedPlayers;
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
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Iterator<Map.Entry<UUID, Integer>> iterator = pvpCooldowns.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, Integer> entry = iterator.next();
                int remaining = entry.getValue() - 1;
                if (remaining <= 0) {
                    iterator.remove();
                } else {
                    entry.setValue(remaining);
                }
            }
        }, 20L, 20L); // 20 ticks = 1 segundo
    }


    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        // Verificar que tanto la víctima como el atacante sean jugadores (PvP)
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player attacker) {
            if(magicShieldPlayers.contains(victim)){
                reduceEffectDamage(victim,event.getDamageSource().getDamageType(), event);
            }
            // Reiniciamos el cooldown del atacante al valor completo
            pvpCooldowns.put(attacker.getUniqueId(), PVP_COOLDOWN);
        }
    }

    public void reduceEffectDamage(Player player, DamageType damageType, EntityDamageByEntityEvent event) {
        RPGPlayer rpgPlayer = load(player);
        if (rpgPlayer != null) {
            int thirdSkillLvl = rpgPlayer.getThirdSkillLvl();  // Obtener el nivel de la habilidad

            double reductionPercentage = 0.0;

            // Lógica para calcular la reducción del daño
            if (isEffectDamage(damageType)) {
                // Para los efectos, aplicar mayor reducción (ejemplo: 7% por cada nivel)
                reductionPercentage = 0.07 * thirdSkillLvl;  // Reducción mayor para efectos
            } else if (isProjectileDamage(damageType)) {
                // Para los proyectiles, aplicar menor reducción (ejemplo: 4% por cada nivel)
                reductionPercentage = 0.04 * thirdSkillLvl;  // Reducción menor para proyectiles
            }

            // Limitar la reducción a un máximo de 50% (esto puede ajustarse)
            reductionPercentage = Math.min(reductionPercentage, 0.5);

            // Lógica para aplicar la reducción del daño
            double reducedDamage = calculateReducedDamage(event.getDamage(), reductionPercentage);
            event.setDamage(reducedDamage);
        }
    }

    private boolean isEffectDamage(DamageType damageType) {
        // Consideramos como "efectos" los tipos de daño relacionados con efectos como fuego, magia, etc.
        return damageType == DamageType.IN_FIRE ||
                damageType == DamageType.CAMPFIRE ||
                damageType == DamageType.LIGHTNING_BOLT ||
                damageType == DamageType.ON_FIRE ||
                damageType == DamageType.LAVA ||
                damageType == DamageType.HOT_FLOOR ||
                damageType == DamageType.DROWN ||
                damageType == DamageType.STARVE ||
                damageType == DamageType.FREEZE ||
                damageType == DamageType.MAGIC ||
                damageType == DamageType.WITHER ||
                damageType == DamageType.DRAGON_BREATH ||
                damageType == DamageType.WITHER_SKULL ||
                damageType == DamageType.EXPLOSION ||
                damageType == DamageType.SONIC_BOOM;
    }

    private boolean isProjectileDamage(DamageType damageType) {
        // Consideramos como "proyectiles" los tipos de daño relacionados con proyectiles como flechas, tridentes, etc.
        return damageType == DamageType.ARROW ||
                damageType == DamageType.TRIDENT ||
                damageType == DamageType.MOB_PROJECTILE ||
                damageType == DamageType.FIREWORKS ||
                damageType == DamageType.FIREBALL ||
                damageType == DamageType.THROWN ||
                damageType == DamageType.WIND_CHARGE ||
                damageType == DamageType.MACE_SMASH;
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
        return pvpCooldowns.containsKey(player.getUniqueId()) || getPvPCooldown(player) == 0;
    }

    private static final String MENU_NAME = ChatColor.DARK_GRAY + "Elección de clase" + "  " + ChatColor.BOLD + ChatColor.RED + "Solo podrás elegirlo una vez";

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        RPGPlayer rpgPlayer = new RPGPlayer(player);

        // Verificar si el inventario es el menú "Elección de clase"
        if (event.getView().getTitle().equals(MENU_NAME)) {
            // Evitar que los jugadores saquen o pongan objetos en el inventario
            event.setCancelled(true);

            // Detectar el clic del jugador y manejarlo si es necesario
            if (event.getCurrentItem() != null) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem.getType() == Material.PLAYER_HEAD) {
                    if(clickedItem.getItemMeta().hasCustomModelData()){
                        int customModelData = clickedItem.getItemMeta().getCustomModelData();
                        if(customModelData == 111){
                            rpgPlayer.setClassKey("ArcherClass");
                        }
                        else if(customModelData == 222){
                            rpgPlayer.setClassKey("TankClass");
                        }
                        else if(customModelData == 333){
                            rpgPlayer.setClassKey("AssassinClass");
                        }
                        player.closeInventory();
                        plugin.rpgPlayersList.get(rpgPlayer).toReset();
                        plugin.rpgPlayersList.get(rpgPlayer).triggerSkills();
                    }
                }
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

        LivingEntity mob = (LivingEntity) event.getEntity();

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
            double chanceToIgnore = Math.min(level * 2, 100); // Por ejemplo: nivel 10 -> 20%, nivel 50 -> 100%

            // Realiza un "roll" aleatorio y, si cae dentro de la probabilidad, cancela el targeting
            double roll = random.nextDouble() * 100;
            if (roll < chanceToIgnore) {
                event.setCancelled(true);
            }
        }
    }

    // Evitar que el jugador saque los objetos del inventario cuando lo cierra
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory closedInventory = event.getInventory();
        Player player = (Player) event.getPlayer();
        RPGPlayer rpgPlayer = new RPGPlayer(player);

        // Verificar si el inventario es el menú "Elección de clase"
        if (event.getView().getTitle().equals(MENU_NAME)) {
            if(rpgPlayer.getClassKey() == null || rpgPlayer.getClassKey().isEmpty()){
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        try {
                            player.openInventory(createClassSelectionMenu(player));
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

        RPGPlayer rpgPlayer = new RPGPlayer(player);

        if(rpgPlayer.getClassKey() == null || Objects.equals(rpgPlayer.getClassKey(), "")){
           player.openInventory(createClassSelectionMenu(player));
        }

        plugin.rpgPlayersList.get(rpgPlayer).toReset();
        plugin.rpgPlayersList.get(rpgPlayer).triggerSkills();
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
            // Por ejemplo, cada nivel aporta 3% de probabilidad, hasta un máximo del 100%.
            double chance = 5 * (level * 0.03);

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
                    case SNOWBALL:
                        ammoMaterial = Material.SNOWBALL;
                        break;
                    case FIREWORK_ROCKET:
                        ammoMaterial = Material.FIREWORK_ROCKET;
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
            float explosionRadius = 2.0f + level * 0.1f;

            // Calcular el daño de la explosión (puedes ajustar la fórmula según convenga)
            double explosionDamage = 4.0 + level * 0.2;

            // Obtener la ubicación de impacto de la flecha
            Location explosionLocation = arrow.getLocation();
            World world = explosionLocation.getWorld();

            // Crear efecto de explosión sin daño automático a entidades ni destrucción de bloques.
            // Se usa poder 0 para evitar el daño integrado, y luego se aplica manualmente.
            world.createExplosion(explosionLocation, explosionRadius, false, false, player);

            // Reproducir efectos visuales y de sonido de explosión
            world.spawnParticle(Particle.EXPLOSION, explosionLocation, 1);
            world.playSound(explosionLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

            // Remover la flecha, si aún existe
            arrow.remove();
        }
    }
}
