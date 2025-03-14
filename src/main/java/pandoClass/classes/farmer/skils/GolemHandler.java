package pandoClass.classes.farmer.skils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.RPGPlayer;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.*;

import static pandoClass.classes.farmer.skils.GolemSkill.isGolemPlayer;

public class GolemHandler implements Listener {

    private final PandoDungeons plugin;

    private final Map<UUID, List<IronGolem>> playerGolems = new HashMap<>();
    private final Map<IronGolem, UUID> golemOwners = new HashMap<>();
    private final Map<UUID, Integer> golemCooldowns = new HashMap<>();

    public GolemHandler(PandoDungeons plugin) {
        this.plugin = plugin;
        runCooldownTimer();
    }

    @EventHandler
    public void onPlayerDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof Monster)) return;
        if (!isGolemPlayer(player)) return;

        UUID playerId = player.getUniqueId();
        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);

        int skillLevel = rpgPlayer.getThirdSkillLvl();
        int maxGolems = Math.max(1, skillLevel / 10);

        if (golemCooldowns.getOrDefault(playerId, 0) > 0) {
            player.sendMessage(ChatColor.RED + "Tus gólems están en cooldown y no pueden ser invocados.");
            return;
        }

        List<IronGolem> activeGolems = playerGolems.getOrDefault(playerId, new ArrayList<>());
        int availableSpots = maxGolems - activeGolems.size();

        if (availableSpots <= 0) return;

        for (int i = 0; i < availableSpots; i++) {
            IronGolem golem = spawnGolem(player, skillLevel);
            activeGolems.add(golem);
            golemOwners.put(golem, playerId);
        }

        playerGolems.put(playerId, activeGolems);
    }

    private IronGolem spawnGolem(Player player, int level) {
        Location spawnLocation = player.getLocation().add(1, 0, 1);
        IronGolem golem = (IronGolem) player.getWorld().spawnEntity(spawnLocation, EntityType.IRON_GOLEM);

        double health = 100 + (level * 5);
        double damage = 6 + (level * 0.2);

        golem.setCustomName(ChatColor.YELLOW + "Gólem Guardián" + ChatColor.AQUA + " [" + ChatColor.WHITE + getEmojiForLevel(level) + ChatColor.AQUA + "]");
        golem.setCustomNameVisible(true);
        golem.setMaxHealth(health);
        golem.setHealth(health);
        golem.setMetadata("golem_damage", new FixedMetadataValue(plugin, damage));

        setGolemTarget(golem);
        return golem;
    }

    public String getEmojiForLevel(int level) {
        return switch (level / 25) { // Divide el nivel entre 20 para agrupar en rangos
            case 0 -> "🔰 " + ChatColor.GREEN + ChatColor.BOLD + level;
            case 1 -> "💩 " + ChatColor.YELLOW + ChatColor.BOLD + level;
            case 2 -> "🔥 " + ChatColor.RED + ChatColor.BOLD + level;
            default -> "⚡ " + ChatColor.AQUA + ChatColor.BOLD + level;
        };
    }


    private void setGolemTarget(IronGolem golem) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!golem.isValid()) {
                    removeGolem(golem);
                    return;
                }

                List<Monster> nearbyMobs = golem.getLocation().getNearbyEntities(10, 5, 10).stream()
                        .filter(e -> e instanceof Monster)
                        .map(e -> (Monster) e)
                        .toList();

                if (!nearbyMobs.isEmpty()) {
                    golem.setTarget(nearbyMobs.get(0));
                } else {
                    UUID ownerID = golemOwners.get(golem);
                    if (ownerID != null) {
                        Player owner = Bukkit.getPlayer(ownerID);
                        if (owner != null) {
                            golem.teleport(owner.getLocation());
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    removeGolem(golem);
                                }
                            }.runTaskLater(plugin, 100);
                        }
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    @EventHandler
    public void onGolemKill(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof IronGolem golem)) return;
        UUID ownerId = golemOwners.get(golem);
        if (ownerId == null) return;

        Player player = Bukkit.getPlayer(ownerId);
        if (player != null) {
            golemCooldowns.put(ownerId, 120);
            player.sendMessage(ChatColor.RED + "Uno de tus gólems ha muerto. Tiempo de recarga: 120.");
        }

        removeGolem(golem);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof IronGolem golem) {
            UUID ownerId = golemOwners.get(golem);
            if (ownerId != null && event.getTarget() instanceof Player player && player.getUniqueId().equals(ownerId)) {
                event.setCancelled(true);
            }
        }
    }

    private void removeGolem(IronGolem golem) {
        UUID ownerId = golemOwners.remove(golem);
        if (ownerId != null) {
            List<IronGolem> golems = playerGolems.get(ownerId);
            if (golems != null) golems.remove(golem);
        }
        golem.remove();
    }

    public void runCooldownTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID playerId : new HashSet<>(golemCooldowns.keySet())) {
                    int timeLeft = golemCooldowns.get(playerId) - 1;
                    if (timeLeft <= 0) {
                        golemCooldowns.remove(playerId);
                        Player player = Bukkit.getPlayer(playerId);
                        if (player != null) {
                            player.sendMessage(ChatColor.GREEN + "Tus gólems pueden ser invocados nuevamente.");
                        }
                    } else {
                        golemCooldowns.put(playerId, timeLeft);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

}
