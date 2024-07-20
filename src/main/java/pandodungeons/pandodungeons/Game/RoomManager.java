package pandodungeons.pandodungeons.Game;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.bossfights.fights.randomFight.RandomFight;
import pandodungeons.pandodungeons.DungeonBuilders.DungeonBuilder;
import pandodungeons.pandodungeons.Elements.Room;
import pandodungeons.pandodungeons.Game.Behaviours.NextRoomVillager;
import pandodungeons.pandodungeons.Utils.BossUtils;
import pandodungeons.pandodungeons.Utils.LocationUtils;
import pandodungeons.pandodungeons.Utils.MobSpawnUtils;
import pandodungeons.pandodungeons.Utils.StructureUtils;

import java.util.*;

import static pandodungeons.pandodungeons.Utils.CompanionUtils.unlockRandomCompanion;

public class RoomManager {
    private static final Map<UUID, RoomManager> activeRoomManagers = new HashMap<>();

    private final List<Room> rooms;
    private final Map<Location, Villager> roomVillagers = new HashMap<>();
    private final Player player;
    private BukkitRunnable roomHandlingTask;
    private boolean shouldStopRoomHandling = false;
    private final Stats playerStats;

    public RoomManager(World world, Player player) {
        this.rooms = new ArrayList<>();
        this.player = player;
        playerStats = Stats.fromPlayer(player);

        List<Location> roomLocations = LocationUtils.getAllDungeonRoomLocations("dungeon_" + player.getUniqueId());
        for (int i = 0; i < roomLocations.size(); i++) {
            rooms.add(new Room(roomLocations.get(i), i + 1, false)); // Inicialmente todas las habitaciones no están completadas
        }

        // Iniciar el bucle para manejar las habitaciones
        startRoomHandlingLoop();

        // Registrar este RoomManager como activo para el jugador
        activeRoomManagers.put(player.getUniqueId(), this);
    }

    private void startRoomHandlingLoop() {
        roomHandlingTask = new BukkitRunnable() {
            boolean bossSummoned = false;
            boolean bossDeath = false;
            boolean roomsRetry = false;
            final PlayerStatsManager statsManager = PlayerStatsManager.getPlayerStatsManager(player);
            @Override
            public void run() {
                if (shouldStopRoomHandling) {
                    this.cancel();
                    Bukkit.getLogger().warning("Se paró el bucle del juego de: " + player.getName());
                    return;
                }

                if(!player.isOnline()){
                    stopRoomHandling();
                    this.cancel();
                    return;
                }

                if(rooms.isEmpty()){
                    if(!roomsRetry){
                        RoomsRetry();
                        roomsRetry = true;
                    }else{
                        shouldStopRoomHandling = true;
                        player.sendMessage(ChatColor.RED + "Ha ocurrido un error al iniciar la dungeon");
                        player.sendMessage(ChatColor.YELLOW + "Vuelve a utilizar el comando /dungeons play");
                        player.performCommand("dungeons leave");
                        this.cancel();
                    }
                    return;
                }

                if (rooms.get(0).getLocation().getWorld() == null) {
                    shouldStopRoomHandling = true;
                    player.sendMessage(ChatColor.RED + "El mundo dungeons ya no existe");
                    player.performCommand("dungeons leave");
                    this.cancel();
                    return;
                }


                boolean allRoomsCompleted = true;

                for (Room room : rooms) {
                    if (player.getLocation().distance(room.getLocation()) < 50) {
                        if (!room.isCleared()) {
                            room.setCleared(handleRoom(room));
                        }
                    }

                    if (!room.isCleared()) {
                        allRoomsCompleted = false;
                    }
                }

                if(bossSummoned){
                    if(BossUtils.isAnyBossAlive(player.getLocation(), 50)){
                        allRoomsCompleted = false;
                    }
                    if(!BossUtils.isAnyBossAlive(player.getLocation(), 50)){
                        allRoomsCompleted = true;
                        bossDeath = true;
                    }
                }

                if(allRoomsCompleted){
                    if (playerStats.getLevelProgress() < 2) {
                        this.cancel();
                        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "¡Has completado todas las habitaciones de la dungeon!");
                        statsManager.addDungeonCompletion();
                    }
                    if((playerStats.getLevelProgress() + 1) >= 3 && !bossSummoned){
                        killVillagers(player.getLocation(), 50);
                        RandomFight bossFight = new RandomFight(StructureUtils.findNetheriteBlock(player.getLocation(),50).add(0.5,1,0.5));
                        bossFight.startRandomFight();
                        bossSummoned = true;
                    }
                }

                if(bossDeath && bossSummoned){
                    Location villagerLocation = StructureUtils.findDriedKelpBlock(player.getLocation(),50);
                    spawnVillager(villagerLocation);
                    player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "¡Has matado al jefe, ahora subes a nivel: " + (playerStats.getDungeonLevel() + 1) + "!");
                    this.cancel();
                    statsManager.addDungeonCompletion();
                }
            }
        };

        // Ejecutar cada 5 segundos (20 ticks = 1 segundo)
        if (!shouldStopRoomHandling) {
            roomHandlingTask.runTaskTimer(JavaPlugin.getPlugin(PandoDungeons.class), 20 * 5, 20 * 5);
        }
    }

    public void stopRoomHandling() {
        shouldStopRoomHandling = true;
        if (roomHandlingTask != null) {
            roomHandlingTask.cancel();
        }
    }

    public void RoomsRetry(){
        List<Location> roomLocations = LocationUtils.getAllDungeonRoomLocations("dungeon_" + player.getUniqueId());
        for (int i = 0; i < roomLocations.size(); i++) {
            rooms.add(new Room(roomLocations.get(i), i + 1, false)); // Inicialmente todas las habitaciones no están completadas
        }
    }

    public void handlePlayerQuit(PlayerQuitEvent event) {
        stopRoomHandling();
        if (event.getPlayer().equals(player)) {
            if(player.getBedSpawnLocation() != null){
                player.teleport(player.getBedSpawnLocation());
            }
            if(Bukkit.getWorld("spawn") != null){
                player.teleport(Bukkit.getWorld("spawn").getSpawnLocation());
            }
            Bukkit.getLogger().warning("Se paro el bucle del jugador: " + player.getName() + " por desconexión");
            stopRoomHandling();
        }
    }

    public static void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (LocationUtils.hasActiveDungeon(player.getUniqueId().toString())) {
            player.performCommand("dungeons leave");
        }
    }

    public boolean handleRoom(Room room) {
        Location location = room.getLocation();
        if(isAllHostileMobsCleared(location)){
            if(!room.isEntered()){
                DungeonBuilder.spawnMobsLater(location);
                player.sendMessage(ChatColor.RED + "No hay mobs, spawneando...");
                room.enter();
            }
            if (player.getLocation().distance(location) < 50) {
                player.sendMessage(ChatColor.GREEN + "Habitacion limpia!");
                spawnVillager(StructureUtils.findNetheriteBlock(location, 50));
                // Habitación completada
                return true;
            }
        } else {
            room.enter();
            removeVillager(StructureUtils.findNetheriteBlock(location, 50));
            return false; // Habitación no completada
        }
        return false;
    }

    public void killVillagers(Location location, double radius) {
        for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (entity.getType() == EntityType.VILLAGER) {
                Villager villager = (Villager) entity;
                villager.remove();
            }
        }
    }

    private void killFurtherVillagers(Location location) {
        // Obtener todas las entidades vivas dentro de un radio de 50 bloques
        Collection<LivingEntity> nearbyEntities = location.getNearbyLivingEntities(50, 50, 50);

        for (LivingEntity entity : nearbyEntities) {
            // Filtrar solo los villager
            if (entity instanceof Villager) {
                Villager villager = (Villager) entity;
                boolean isNearPlayer = false;
                if (villager.getScoreboardTags().contains("NextRoomVillager")) {
                    break;
                }
                // Verificar si hay algún jugador dentro de 50 bloques del villager
                for (Player player : location.getWorld().getPlayers()) {
                    if (player.getLocation().distance(villager.getLocation()) <= 50) {
                        isNearPlayer = true;
                        break;
                    }
                }

                // Si no hay ningún jugador cerca, matar al villager
                if (!isNearPlayer && villager.getScoreboardTags().contains("NextRoomVillager")) {
                    villager.remove();
                }
            }
        }
    }

    private boolean isAllHostileMobsCleared(Location location) {
        // Implementa lógica para verificar si no hay mobs hostiles en la ubicación dada
        return MobSpawnUtils.areHostileMobsCleared(location);
    }

    private void spawnVillager(Location location) {
        location.add(0.5,1.1,0.5);

        if(!roomVillagers.containsKey(location)){
            net.minecraft.world.entity.npc.Villager villager = new NextRoomVillager(location, player);
            Villager villagerB = (Villager) villager.getBukkitEntity();
            villagerB.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "Click for next room");
            villagerB.setCustomNameVisible(true);
            roomVillagers.put(location, villagerB);
            villagerB.addScoreboardTag("NextRoomVillager");
            villagerB.setInvulnerable(true);
            villagerB.setRemoveWhenFarAway(false);
            Bukkit.getLogger().info("Spawn custom villager: " + ((CraftWorld) player.getLocation().getWorld()).getHandle().addFreshEntity(((CraftEntity) villagerB).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM));
        }else{
            Bukkit.getLogger().warning("Error aldeano");
        }
    }

    private void removeVillager(Location location) {
        Villager villager = roomVillagers.get(location);
        if (villager != null) {
            villager.remove();
            roomVillagers.remove(location);
        }
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public static RoomManager getActiveRoomManager(Player player) {
        return activeRoomManagers.get(player.getUniqueId());
    }
}
