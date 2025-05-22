package pandoClass.gachaPon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GachaHolo {
    private final PandoDungeons plugin;
    public static final Map<UUID, ArmorStand> activeHolograms = new HashMap<>();

    public GachaHolo(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    public void showHolo(Player player) {
        if (!player.isOnline()) return;

        World world = Bukkit.getWorld("spawn");
        if (world == null) return;

        Location loc = new Location(world, 294.5, 79, 441.5);
        UUID playerId = player.getUniqueId();

        removeHolo(player); // Asegura que no haya duplicados

        ArmorStand holograma = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
        holograma.setVisible(false);
        holograma.setGravity(false);
        holograma.setCustomNameVisible(true);
        holograma.addScoreboardTag("gachaHolo");
        holograma.addScoreboardTag(player.getName());

        activeHolograms.put(playerId, holograma);
        hideFromEveryoneExceptOwner(player, holograma);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || holograma.isDead()) {
                    removeHolo(player);
                    cancel();
                    return;
                }

                keepHidingHolos(plugin);

                int gachaOpens = plugin.rpgManager.getPlayer(player).getGachaopen();
                holograma.setCustomName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD +
                        "Acumulado: " + ChatColor.RESET + gachaOpens + "/" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "50");
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void hideFromEveryoneExceptOwner(Player owner, ArmorStand holograma) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hideEntity(plugin, holograma);
        }
        owner.showEntity(plugin, holograma);
    }

    public static void removeHolo(Player player) {
        UUID playerId = player.getUniqueId();
        ArmorStand holograma = activeHolograms.remove(playerId);
        if (holograma != null && !holograma.isDead()) {
            holograma.remove();
        }
    }

    public static void removeAllGachaHolos() {
        for (ArmorStand holograma : activeHolograms.values()) {
            if (holograma != null && !holograma.isDead()) {
                holograma.remove();
            }
        }
        activeHolograms.clear();

        World spawn = Bukkit.getWorld("spawn");
        if (spawn != null) {
            for (Entity entity : spawn.getEntities()) {
                if (entity.getScoreboardTags().contains("gachaHolo")) {
                    entity.remove();
                }
            }
        }
    }

    public static void removeAllGachaHolosOnStart(PandoDungeons plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                removeAllGachaHolos();
            }
        }.runTaskLater(plugin, 40);
    }

    public static void keepHidingHolos(PandoDungeons plugin){
        for (ArmorStand holograma : activeHolograms.values()) {
            if (holograma != null && !holograma.isDead()) {
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(holograma.getScoreboardTags().contains("gachaHolo")){
                        if(!holograma.getScoreboardTags().contains(player.getName())){
                            player.hideEntity(plugin, player);
                        }else{
                            player.showEntity(plugin, player);
                        }
                    }
                }
            }
        }
    }
}
