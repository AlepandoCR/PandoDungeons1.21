package pandodungeons.Game;


import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.PandoDungeons;
import pandodungeons.Utils.*;
import pandodungeons.bossfights.fights.randomFight.RandomFight;
import pandodungeons.DungeonBuilders.DungeonBuilder;
import pandodungeons.Elements.Room;
import pandodungeons.Game.Behaviours.NextRoomVillager;

import java.util.*;

import static pandodungeons.Game.PlayerStatsManager.getPlayerStatsManager;

public class RoomManager {
    private static final Map<UUID, RoomManager> activeRoomManagers = new HashMap<>();

    private final List<Room> rooms;
    private final Map<Location, Villager> roomVillagers = new HashMap<>();
    private final Player ownerPlayer;
    private PlayerParty party;
    private BukkitRunnable roomHandlingTask;
    private boolean shouldStopRoomHandling = false;
    private final Stats playerStats;
    private final boolean isPartyDungeon;
    private final PandoDungeons plugin;

    public RoomManager(World world, Player player, boolean isPartyDungeon, PandoDungeons plugin) {
        this.rooms = new ArrayList<>();
        this.ownerPlayer = player;
        this.isPartyDungeon = isPartyDungeon;
        this.plugin = plugin;
        playerStats = Stats.fromPlayer(player);

        processParty(isPartyDungeon, plugin);

        List<Location> roomLocations = LocationUtils.getAllDungeonRoomLocations("dungeon_" + player.getUniqueId());
        for (int i = 0; i < roomLocations.size(); i++) {
            rooms.add(new Room(roomLocations.get(i), i + 1, false)); // Inicialmente todas las habitaciones no están completadas
        }

        // Iniciar el bucle para manejar las habitaciones
        startRoomHandlingLoop();

        // Registrar este RoomManager como activo para el jugador
        activeRoomManagers.put(player.getUniqueId(), this);
    }

    private void processParty(boolean isPartyDungeon, PandoDungeons plugin) {
        if(isPartyDungeon){
            var aux = plugin.playerPartyList.getPartyByMember(ownerPlayer);
            if(aux != null){
                party = aux;
            }else{
                partyError();
            }
        }
    }

    private void partyError() {
        ownerPlayer.sendMessage(ChatColor.RED + "Ha ocurrido un error con la party");
        ownerPlayer.sendMessage(ChatColor.YELLOW + "Vuelve a utilizar el comando /dungeons play");
        ownerPlayer.performCommand("dungeons leave");
    }

    private void startRoomHandlingLoop() {
        final Map<Player, Integer> listaKills = new HashMap<>();
        if(isPartyDungeon){
            for(Player player1 : party.getMembers()){
                listaKills.put(player1, getPlayerStatsManager(player1).getMobsKilled());
            }
        }else{
            listaKills.put(ownerPlayer,getPlayerStatsManager(ownerPlayer).getMobsKilled());
        }

        roomHandlingTask = new BukkitRunnable() {
            boolean bossSummoned = false;
            boolean bossDeath = false;
            boolean roomsRetry = false;
            final PlayerStatsManager statsManager = getPlayerStatsManager(ownerPlayer);
            @Override
            public void run() {
                if (shouldStopRoomHandling) {
                    this.cancel();
                    Bukkit.getLogger().warning("Se paró el bucle del juego de: " + ownerPlayer.getName());
                    return;
                }

                if(!ownerPlayer.isOnline()){
                    stopRoomHandling();
                    this.cancel();
                    return;
                }

                if(isPartyDungeon){

                    if(party == null){
                        partyError();
                    }

                    for(Player player1 : party.getMembers()){
                        player1.sendActionBar(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "💀" + ChatColor.BLACK + " : " + ChatColor.WHITE + ChatColor.BOLD + (getPlayerStatsManager(player1).getMobsKilled() - listaKills.get(player1)));
                    }

                    boolean alldeath = false;

                    if(party != null){
                        for (Player member : party.getMembers()) {
                            // Verifica si el miembro está vivo o es el jugador que acaba de respawnear
                            alldeath = member.getGameMode() == GameMode.SPECTATOR;  // Si encontramos a un miembro vivo o el mismo jugador que acaba de respawnear, alldeath se pone a false
                        }


                        if(alldeath){
                            ownerPlayer.performCommand("dungeons leave");
                            for(Player player1 : party.getMembers()){
                                player1.sendMessage(ChatColor.LIGHT_PURPLE + "Todo tu equipo ha muerto....");
                                PlayerStatsManager stats = getPlayerStatsManager(player1);
                                if (stats != null) {
                                    stats.resetLevelProgress();
                                }
                                player1.setGameMode(GameMode.SURVIVAL);  // Asegúrate de cambiar el modo de juego del jugador correcto
                            }
                        }
                    }
                } else {
                    ownerPlayer.sendActionBar(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "💀 " + ChatColor.BLACK + ChatColor.WHITE + ChatColor.BOLD + (statsManager.getMobsKilled() - listaKills.get(ownerPlayer)) + ChatColor.DARK_RED.toString() + ChatColor.BOLD +  " 💀");
                }

                if(rooms.isEmpty()){
                    if(!roomsRetry){
                        RoomsRetry();
                        roomsRetry = true;
                    }else{
                        shouldStopRoomHandling = true;
                        ownerPlayer.sendMessage(ChatColor.RED + "Ha ocurrido un error al iniciar la dungeon");
                        ownerPlayer.sendMessage(ChatColor.YELLOW + "Vuelve a utilizar el comando /dungeons play");
                        ownerPlayer.performCommand("dungeons leave");
                        this.cancel();
                    }
                    return;
                }

                if (rooms.get(0).getLocation().getWorld() == null) {
                    shouldStopRoomHandling = true;
                    ownerPlayer.sendMessage(ChatColor.RED + "El mundo dungeons ya no existe");
                    ownerPlayer.performCommand("dungeons leave");
                    this.cancel();
                    return;
                }


                boolean allRoomsCompleted = true;

                for (Room room : rooms) {
                    if (ownerPlayer.getLocation().distance(room.getLocation()) < 50) {
                        if (!room.isCleared()) {
                            room.setCleared(handleRoom(room));
                        }
                    }

                    if (!room.isCleared()) {
                        allRoomsCompleted = false;
                    }
                }

                if(bossSummoned){
                    if(BossUtils.isAnyBossAlive(ownerPlayer.getLocation(), 50)){
                        allRoomsCompleted = false;
                    }
                    if(!BossUtils.isAnyBossAlive(ownerPlayer.getLocation(), 50)){
                        allRoomsCompleted = true;
                        bossDeath = true;
                    }
                }

                if(allRoomsCompleted){
                    if (playerStats.levelProgress() < 2) {
                        this.cancel();
                        ownerPlayer.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "¡Has completado todas las habitaciones de la dungeon!");
                        if(isPartyDungeon){
                            for(Player player1 : party.getMembers()){
                               getPlayerStatsManager(player1).addDungeonCompletion();
                            }
                        }else{
                            statsManager.addDungeonCompletion();
                        }
                    }
                    if((playerStats.levelProgress() + 1) >= 3 && !bossSummoned){
                        killVillagers(ownerPlayer.getLocation(), 50);
                        if(isPartyDungeon){
                            int i = 1;
                            for(Player player1 : party.getMembers()){
                                if(i > 5){
                                    break;
                                }
                                RandomFight bossFight = new RandomFight(StructureUtils.findNetheriteBlock(ownerPlayer.getLocation(),50).add(0.5,1,0.5));
                                bossFight.startRandomFight();
                                i++;
                            }
                        }else{
                            RandomFight bossFight = new RandomFight(StructureUtils.findNetheriteBlock(ownerPlayer.getLocation(),50).add(0.5,1,0.5));
                            bossFight.startRandomFight();
                        }

                        bossSummoned = true;
                    }
                }

                if(bossDeath && bossSummoned){
                    Location villagerLocation = StructureUtils.findDriedKelpBlock(ownerPlayer.getLocation(),50);
                    spawnVillager(villagerLocation);
                    ownerPlayer.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "¡Has matado al jefe, ahora subes a nivel: " + (playerStats.dungeonLevel() + 1) + "!");
                    if(isPartyDungeon){
                        for(Player player1 : party.getMembers()){
                            if(!party.isOwner(player1)){
                                getPlayerStatsManager(player1).addDungeonCompletion();
                            }
                        }
                    }
                    statsManager.addDungeonCompletion();
                    this.cancel();
                }
            }
        };

        // Ejecutar cada 5 segundos (20 ticks = 1 segundo)
        if (!shouldStopRoomHandling) {
            roomHandlingTask.runTaskTimer(JavaPlugin.getPlugin(PandoDungeons.class), 20 * 2, 20 * 2);
        }
    }

    public void stopRoomHandling() {
        shouldStopRoomHandling = true;
        if (roomHandlingTask != null) {
            roomHandlingTask.cancel();
        }
    }

    public void RoomsRetry(){
        List<Location> roomLocations = LocationUtils.getAllDungeonRoomLocations("dungeon_" + ownerPlayer.getUniqueId());
        for (int i = 0; i < roomLocations.size(); i++) {
            rooms.add(new Room(roomLocations.get(i), i + 1, false)); // Inicialmente todas las habitaciones no están completadas
        }
    }

    public void handlePlayerQuit(PlayerQuitEvent event) {
        stopRoomHandling();
        if (event.getPlayer().equals(ownerPlayer)) {
            if(ownerPlayer.getBedSpawnLocation() != null){
                ownerPlayer.teleport(ownerPlayer.getBedSpawnLocation());
            }
            if(Bukkit.getWorld("spawn") != null){
                ownerPlayer.teleport(Bukkit.getWorld("spawn").getSpawnLocation());
            }
            Bukkit.getLogger().warning("Se paro el bucle del jugador: " + ownerPlayer.getName() + " por desconexión");
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
                DungeonBuilder.spawnMobsLater(location,"", plugin);
                ownerPlayer.sendMessage(ChatColor.RED + "No hay mobs, spawneando...");
                room.enter();
            }
            if (ownerPlayer.getLocation().distance(location) < 50) {
                ownerPlayer.sendMessage(ChatColor.GREEN + "Habitacion limpia!");
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

    public void spawnRandomShop(Location location){

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
            net.minecraft.world.entity.npc.Villager villager = new NextRoomVillager(location, ownerPlayer);
            Villager villagerB = (Villager) villager.getBukkitEntity();
            villagerB.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "Click for next room");
            villagerB.setCustomNameVisible(true);
            roomVillagers.put(location, villagerB);
            villagerB.addScoreboardTag("NextRoomVillager");
            villagerB.setInvulnerable(true);
            villagerB.setRemoveWhenFarAway(false);
            Bukkit.getLogger().info("Spawn custom villager: " + ((CraftWorld) ownerPlayer.getLocation().getWorld()).getHandle().addFreshEntity(((CraftEntity) villagerB).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM));
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
