package pandoClass.gachaPon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.RPGPlayer;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GachaHolo {
    private final PandoDungeons plugin;
    // Mapa para llevar el seguimiento de los hologramas activos por jugador
    public static final Map<UUID, ArmorStand> activeHolograms = new HashMap<>();

    public GachaHolo(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    public void showHolo(Player player) {
        // Solo se genera si el jugador está online
        if (!player.isOnline()) {
            return;
        }
        // Obtener el mundo 'spawn'
        World world = Bukkit.getWorld("spawn");
        if (world == null) return;

        // Definir la ubicación del holograma (puede ser fija o relativa al jugador)
        Location loc = new Location(world, 294.5, 79, 441.5);

        UUID playerId = player.getUniqueId();
        // Si ya existe un holograma para este jugador, eliminarlo
        if (activeHolograms.containsKey(playerId)) {
            ArmorStand oldHolo = activeHolograms.get(playerId);
            if(oldHolo != null && !oldHolo.isDead()){
                oldHolo.remove();
            }
            activeHolograms.remove(playerId);
        }

        // Crear el ArmorStand para el holograma en el mundo 'spawn'
        ArmorStand holograma = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
        holograma.setVisible(false);
        holograma.setGravity(false);
        holograma.setCustomNameVisible(true);
        holograma.addScoreboardTag("gachaHolo");

        // Guardar el holograma activo para este jugador
        activeHolograms.put(playerId, holograma);

        // Configurar para que sea visible solo para el jugador correspondiente
        sendOnlyToOwner(player, holograma);

        // Tarea que actualiza el holograma cada segundo
        new BukkitRunnable() {
            @Override
            public void run() {
                // Si el jugador se desconecta o el holograma ya fue removido, cancelar la tarea y eliminar el holograma
                if (!player.isOnline() || holograma.isDead()) {
                    activeHolograms.remove(playerId);
                    if (!holograma.isDead()) {
                        holograma.remove();
                    }
                    cancel();
                    return;
                }
                // Actualizar el dato personalizado (ej. contador de gacha)
                int gachaOpens = new RPGPlayer(player).getGachaopen();
                holograma.setCustomName(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD +
                        "Acumulado: " + ChatColor.RESET + gachaOpens + "/" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "50");
            }
        }.runTaskTimer(plugin, 0L, 20L); // Se actualiza cada 20 ticks (1 segundo)
    }

    private void sendOnlyToOwner(Player player, ArmorStand holograma) {
        // Ocultar el holograma para todos los jugadores...
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hideEntity(plugin, holograma);
        }
        // ...y mostrarlo solo para el jugador correspondiente
        player.showEntity(plugin, holograma);
    }



    public static void removeAllGachaHolos() {
        // Iterar sobre todos los ArmorStands registrados en activeHolograms y removerlos
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
        }.runTaskLater(plugin, 40);
    }
}
