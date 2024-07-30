package pandodungeons.pandodungeons.commands.game;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.Utils.CompanionUtils;
import pandodungeons.pandodungeons.commands.Management.CommandQueue;
import pandodungeons.pandodungeons.DungeonBuilders.DungeonBuilder;
import pandodungeons.pandodungeons.Elements.LootTableManager;
import pandodungeons.pandodungeons.Game.PlayerStatsManager;
import pandodungeons.pandodungeons.Utils.LocationUtils;
import pandodungeons.pandodungeons.Elements.Room;
import pandodungeons.pandodungeons.Game.RoomManager;
import pandodungeons.pandodungeons.Utils.StructureUtils;

import java.util.*;

import static pandodungeons.pandodungeons.commands.game.DungeonsLeaveCommand.removeAllBossBars;

public class DungeonsPlayCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 180000 * 2; // 6 minutos en milisegundos
    private static final long GLOBAL_COOLDOWN_TIME = 5000; // 50 segundos en milisegundos
    private static long lastCommandExecutionTime = 0;
    private boolean playerDced = false;

    public DungeonsPlayCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin); // Registrar este comando como listener
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por jugadores.");
            return true;
        }

        if(player.isOnline()){
            playerDced = false;
        }

        UUID playerUUID = player.getUniqueId();
        String playerName = playerUUID.toString();
        String playerNameLower = player.getName().toLowerCase(Locale.ROOT);

        // Verificar si el comando está en cooldown global
        if (!player.isOp() && (System.currentTimeMillis() - lastCommandExecutionTime) < GLOBAL_COOLDOWN_TIME) {
            long timeLeft = (GLOBAL_COOLDOWN_TIME - (System.currentTimeMillis() - lastCommandExecutionTime)) / 1000;
            player.sendMessage(ChatColor.RED + "El comando está en cooldown global. Por favor, espera " + timeLeft + " segundos.");
            return true;
        }

        // Verificar si el jugador está en cooldown
        if (cooldowns.containsKey(playerUUID) && !player.isOp()) {
            long timeLeft = (cooldowns.get(playerUUID) + COOLDOWN_TIME) - System.currentTimeMillis();
            if (timeLeft > 0) {
                player.sendMessage(ChatColor.RED + "Debes esperar " + (timeLeft / 1000) + " segundos antes de usar este comando nuevamente.");
                return true;
            }
        }

        // Verificar si el jugador ya tiene una dungeon activa
        if (LocationUtils.hasActiveDungeon(playerName)) {
            player.sendMessage("Ya tienes una Dungeon activa. Usa /dungeons leave");
            return true;
        }

        // Añadir el jugador a la cola de espera
        CommandQueue queue = CommandQueue.getInstance();
        queue.enqueue(playerName);
        if (queue.isEmpty() && !queue.peek().equals(playerName)) {
            player.sendMessage(ChatColor.DARK_RED + "Hay jugadores esperando para hacer su dungeon, intenta en unos segundos" + "\n"
                    + ChatColor.RESET + ChatColor.DARK_PURPLE + "Recomendación: Esperar 30 segundos ");
            //quitarlo pa que funque
            queue.dequeue();
            return true;
        }

        // Establecer el tiempo de uso del comando
        cooldowns.put(playerUUID, System.currentTimeMillis());
        lastCommandExecutionTime = System.currentTimeMillis(); // Actualizar el tiempo de la última ejecución del comando

        // Obtener temas disponibles
        List<String> availableThemes = StructureUtils.getAvailableThemes();

        // Verificar si hay temas disponibles
        if (availableThemes.isEmpty()) {
            player.sendMessage("No hay temas disponibles para crear una dungeon.");
            queue.dequeue(); // Remover al jugador de la cola si no hay temas disponibles
            return true;
        }

        for(World world : Bukkit.getWorlds()){
            if(world.getName().contains(playerNameLower)){
                player.sendMessage("No puedes crear mundos ya existentes");
                return true;
            }
        }

        // Selección aleatoria de un tema
        Random random = new Random();
        String theme = availableThemes.get(random.nextInt(availableThemes.size()));
        player.sendMessage(ChatColor.DARK_GREEN.toString() + "Generando mundo..." + "\n" + ChatColor.RESET + ChatColor.GOLD + "Esto puede tardar hasta 1 minuto");

        if(!player.isOnline()){
            StructureUtils.removeDungeon(playerName, plugin);
            playerDced = true;
            //quitarlo pa que funque
            queue.dequeue();
            return true;
        }

        // Crear una nueva ubicación para teleportar al jugador al mundo de la dungeon
        World dungeonWorld = StructureUtils.createDungeonWorld(playerNameLower);

        BukkitRunnable crearMundo = new BukkitRunnable() {

            @Override
            public void run() {
                if (dungeonWorld == null) {
                    player.sendMessage("No se pudo crear el mundo de la dungeon para el jugador " + playerName);
                    queue.dequeue(); // Remover al jugador de la cola si el comando no se ejecuta correctamente
                    return;
                }

                if(!player.isOnline() || playerDced){
                    playerDced = true;
                    StructureUtils.removeDungeon(playerName, plugin);
                    //quitarlo pa que funque
                    queue.dequeue();
                    this.cancel();
                    return;
                }

                dungeonWorld.setSpawnLocation(0, 100, 0);
                Location dungeonSpawnLocation = dungeonWorld.getSpawnLocation(); // Obtener la ubicación de spawn del mundo de la dungeon


                // Colocar la estructura de spawn
                StructureUtils.loadSpawnStructure(dungeonSpawnLocation, theme, playerName, dungeonWorld);


                if(!player.isOnline() || playerDced){
                    playerDced = true;
                    StructureUtils.removeDungeon(playerName, plugin);
                    //quitarlo pa que funque
                    queue.dequeue();
                    this.cancel();
                    return;
                }

                // Construir la dungeon dentro del mundo generado
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                player.sendMessage(ChatColor.AQUA + "Generando estructuras...");
                DungeonBuilder dungeonBuilder = new DungeonBuilder(plugin, dungeonWorld);
                dungeonBuilder.buildDungeon(dungeonSpawnLocation, theme, playerName);

                BukkitRunnable Warn1 = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!player.isOnline() || playerDced){
                            playerDced = true;
                            StructureUtils.removeDungeon(playerName, plugin);
                            //quitarlo pa que funque
                            queue.dequeue();
                            this.cancel();
                            return;
                        }
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.2f);
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Generando habitaciones...");
                    }
                };
                Warn1.runTaskLater(plugin, 200);
                BukkitRunnable Warn2 = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!player.isOnline() || playerDced){
                            playerDced = true;
                            StructureUtils.removeDungeon(playerName, plugin);
                            //quitarlo pa que funque
                            queue.dequeue();
                            this.cancel();
                            return;
                        }
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.4f);
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Spawneando mobs...");
                    }
                };
                Warn2.runTaskLater(plugin, 400);

                BukkitRunnable Warn3 = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!player.isOnline() || playerDced){
                            playerDced = true;
                            StructureUtils.removeDungeon(playerName, plugin);
                            //quitarlo pa que funque
                            queue.dequeue();
                            this.cancel();
                            return;
                        }
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.6f);
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Creando aldeanos...");
                    }
                };
                Warn3.runTaskLater(plugin, 600);

                BukkitRunnable Warn4 = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!player.isOnline() || playerDced){
                            playerDced = true;
                            StructureUtils.removeDungeon(playerName, plugin);
                            //quitarlo pa que funque
                            queue.dequeue();
                            this.cancel();
                            return;
                        }
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.8f);
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Cargando estadisticas...");
                    }
                };
                Warn4.runTaskLater(plugin, 800);

                BukkitRunnable afterGen = new BukkitRunnable() {
                    @Override
                    public void run() {

                        if(!player.isOnline() || playerDced){
                            playerDced = true;
                            StructureUtils.removeDungeon(playerName, plugin);
                            //quitarlo pa que funque
                            queue.dequeue();
                            this.cancel();
                            return;
                        }

                        BukkitRunnable lastWarn = new BukkitRunnable() {
                            @Override
                            public void run() {
                                if(!player.isOnline() || playerDced){
                                    playerDced = true;
                                    StructureUtils.removeDungeon(playerName, plugin);
                                    //quitarlo pa que funque
                                    queue.dequeue();
                                    this.cancel();
                                    return;
                                }
                                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
                                player.sendMessage(ChatColor.AQUA + "Falta poco...");
                            }
                        };
                        lastWarn.runTaskLater(plugin, 20);
                        // Buscar la ubicación del bloque de dried kelp dentro de la estructura de spawn
                        BukkitRunnable tpPlayer = new BukkitRunnable() {
                            @Override
                            public void run() {

                                if(!player.isOnline() || playerDced){
                                    playerDced = true;
                                    StructureUtils.removeDungeon(playerName, plugin);
                                    //quitarlo pa que funque
                                    queue.dequeue();
                                    this.cancel();
                                    return;
                                }

                                // Colocar el Villager en el spawn
                                Location spawnNetheriteBlockLocation = StructureUtils.findNetheriteBlock(dungeonSpawnLocation, 50);
                                if (spawnNetheriteBlockLocation != null) {
                                    spawnVillager(dungeonWorld, spawnNetheriteBlockLocation);
                                } else {
                                    player.sendMessage("No se pudo encontrar el bloque de netherite en la estructura de spawn.");
                                }
                                Location kelpBlockLocation = StructureUtils.findDriedKelpBlock(dungeonSpawnLocation, 50);
                                if (kelpBlockLocation != null) {
                                    // Guardar la ubicación inicial del jugador y la información de la dungeon
                                    String dungeonID = "dungeon_" + playerName;
                                    LocationUtils.savePlayerLocationData(playerName, player.getLocation(), dungeonID);

                                    // Guardar la ubicación de la dungeon para referencia futura
                                    LocationUtils.saveDungeonLocationData(dungeonID, dungeonSpawnLocation);

                                    // Iniciar el RoomManager para este jugador
                                    RoomManager roomManager = new RoomManager(dungeonWorld, player);

                                    player.teleport(kelpBlockLocation.add(0.5, 1, 0.5)); // Añadir 0.5 para centrar al jugador en el bloque y 1 para que esté encima
                                    player.setGameMode(GameMode.ADVENTURE);
                                } else {
                                    player.sendMessage("No se pudo encontrar el bloque de dried kelp en la estructura de spawn.");
                                }

                                if(CompanionUtils.hasSelectedCompanion(player)){
                                    CompanionUtils.summonSelectedCompanion(player);
                                }
                                // Informar al jugador
                                player.sendMessage(ChatColor.BOLD + "¡Bienvenido a tu dungeon personal! Usa /dungeons leave para salir.");
                            }
                        };
                        tpPlayer.runTaskLater(plugin, 80);
                    }
                };
                afterGen.runTaskLater(plugin, 1010);
            }
        };
        crearMundo.runTaskLater(plugin, 80);
        // Remover al jugador de la cola una vez que se haya creado la dungeon
        queue.dequeue();
        return true;
    }
    private void spawnVillager(World dungeonWorld, Location spawnNetheriteBlockLocation){
        Villager villager = (Villager) dungeonWorld.spawnEntity(spawnNetheriteBlockLocation.add(0.5, 1, 0.5), EntityType.VILLAGER);
        villager.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "Click to start");
        villager.setCustomNameVisible(true);
        villager.addScoreboardTag("startPoint");
        villager.setAdult(); // Configurar como adulto para evitar problemas con la invisibilidad
        villager.setAI(false);
        villager.setInvulnerable(true);
    }

    public void resetCooldown(UUID playerUUID) {
        cooldowns.remove(playerUUID);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getUniqueId().toString();
        if (LocationUtils.hasActiveDungeon(playerName) && !player.getWorld().getName().contains(player.getName().toLowerCase(Locale.ROOT))) {
            removeAllBossBars(player);
            player.performCommand("dungeons leave");
        }
        // Manejar si un jugador externo al mundo dungeon entra
        if(LocationUtils.isDungeonWorld(player.getWorld().getName()) && !player.getWorld().getName().contains(player.getName().toLowerCase(Locale.ROOT)) && !player.isOp()){
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
        if(!LocationUtils.hasActiveDungeon(playerName) && LocationUtils.isDungeonWorld(player.getWorld().getName()) && !player.isOp()){
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
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getUniqueId().toString();

        if (LocationUtils.hasActiveDungeon(playerName) && player.getWorld().getName().contains(player.getName().toLowerCase(Locale.ROOT))) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.performCommand("dungeons leave");
                player.sendMessage(ChatColor.RED + "Se eliminó tu dungeon por morir");

                PlayerStatsManager stats = PlayerStatsManager.getPlayerStatsManager(player);
                if (stats != null) {
                    stats.resetLevelProgress();
                }
            });
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Villager) {
            Villager villager = (Villager) event.getRightClicked();
            String customName = villager.getCustomName();
            if (customName != null) {
                if (customName.equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Click to start")) {
                    if (villager.getScoreboardTags().contains("startPoint")) {
                        handleStartVillagerInteraction(player, villager);
                    }
                } else if (customName.equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Click for next room")) {
                    handleNextRoomVillagerInteraction(player, villager);
                }
            }
        }
    }

    private void handleStartVillagerInteraction(Player player, Villager villager) {
        String dungeonID = "dungeon_" + player.getUniqueId();
        List<Location> roomLocations = LocationUtils.getAllDungeonRoomLocations(dungeonID);

        if (!roomLocations.isEmpty()) {
            Room firstRoom = new Room(roomLocations.get(0), 1, false);
            Location playerTpLocation = StructureUtils.findDriedKelpBlock(firstRoom.getLocation(), 50);
            if (playerTpLocation != null) {
                playerTpLocation.setY(playerTpLocation.getY() + 1);
                player.teleport(playerTpLocation);
                villager.remove();
                player.sendMessage("¡Teletransportado a la habitación #1!");
            } else {
                player.sendMessage("No se encontró la ubicación de la dungeon para teletransportarte.");
            }
        } else {
            player.sendMessage("No se encontraron habitaciones para teletransportarte.");
        }
    }

    private void handleNextRoomVillagerInteraction(Player player, Villager villager) {
        String roomTag = getRoomTag(villager);
        if (roomTag != null) {
            String dungeonID = "dungeon_" + player.getUniqueId();
            List<Location> roomLocations = LocationUtils.getAllDungeonRoomLocations(dungeonID);
            int currentRoomNumber = getCurrentRoomNumber(villager, roomLocations);
            int nextRoomNumber = currentRoomNumber + 1;

            if (currentRoomNumber == -1) {
                player.sendMessage("No se encontró la habitación actual. Reinicia la interacción con el aldeano.");
                return;
            }

            if (nextRoomNumber <= roomLocations.size()) { // Check if nextRoomNumber is within valid range
                Room nextRoom = new Room(roomLocations.get(nextRoomNumber - 1), nextRoomNumber, false);
                Location nextRoomLocation = nextRoom.getLocation();
                Location playerTpLocation = StructureUtils.findDriedKelpBlock(nextRoomLocation, 50);
                if (playerTpLocation != null) {
                    playerTpLocation.setY(playerTpLocation.getY() + 1);
                    player.teleport(playerTpLocation);
                    villager.remove();
                    player.sendMessage("¡Teletransportado a la habitación #" + nextRoomNumber + "!");
                } else {
                    player.sendMessage("No se encontró la ubicación de la dungeon para teletransportarte.");
                }
            } else {
                boolean emptyStack = false;
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item == null) {
                        emptyStack = true;
                        player.performCommand("dungeons leave");
                        LootTableManager.giveRandomLoot(player);
                        break;
                    }
                }
                if(!emptyStack){
                    player.sendMessage(ChatColor.RED + "Tu inventario esta lleno");
                }
                player.sendMessage("¡Has llegado al final de la dungeon!");
            }
        }
    }

    private String getRoomTag(Villager villager) {
        for (String tag : villager.getScoreboardTags()) {
            if (tag.contains("NextRoomVillager")) {
                return tag;
            }
        }
        return null;
    }

    private int getCurrentRoomNumber(Villager villager, List<Location> rooms) {
        Location currentLocation = villager.getLocation();
        double minDistanceSquared = Double.MAX_VALUE;
        int closestRoomNumber = -1;

        for (int i = 0; i < rooms.size(); i++) {
            Location roomLocation = rooms.get(i);
            double distanceSquared = roomLocation.distanceSquared(currentLocation);

            if (distanceSquared < 4) {
                return i + 1; // El número de habitación es el índice + 1
            }

            // Keep track of the closest room in case the exact match isn't found
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closestRoomNumber = i + 1;
            }
        }

        // If no exact match was found, return the closest room number found
        return closestRoomNumber;
    }
}
