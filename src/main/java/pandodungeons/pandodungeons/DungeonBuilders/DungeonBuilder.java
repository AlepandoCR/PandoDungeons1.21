package pandodungeons.pandodungeons.DungeonBuilders;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.Elements.Room;
import pandodungeons.pandodungeons.Utils.LocationUtils;
import pandodungeons.pandodungeons.Utils.MobSpawnUtils;
import pandodungeons.pandodungeons.Utils.StructureUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static pandodungeons.pandodungeons.Utils.StructureUtils.isWorldLoaded;

public class DungeonBuilder {
    private final JavaPlugin plugin;
    private final World world;
    private final Random random;
    private static List<Room> rooms;
    List<Location> roomLocations;

    public DungeonBuilder(JavaPlugin plugin, World world) {
        this.plugin = plugin;
        this.world = world;
        this.random = new Random();
        rooms = new ArrayList<>();
        roomLocations = new ArrayList<>();
    }
    public void buildDungeon(Location baseLocation, String theme, String playerName) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            int numRooms = 7 + random.nextInt(4);  // 7 to 10 rooms

            List<Location> roomLocations = new ArrayList<>();
            List<Room> rooms = new ArrayList<>();
            Set<Location> placedLocations = new HashSet<>();
            AtomicInteger roomIndex = new AtomicInteger(0);

            Runnable placeRoomTask = new Runnable() {
                @Override
                public void run() {
                    if(world == null || !isWorldLoaded(world)){
                        return;
                    }
                    if (roomIndex.get() >= numRooms) {
                        // Guardar ubicaciones de habitaciones en LocationUtils
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            LocationUtils.saveDungeonRoomLocations(playerName, roomLocations);

                            // Llamar al método para spawnear los mobs y números de habitación
                            spawnRoomsAndMobs(rooms, 0);
                        });
                        return;
                    }

                    Location roomLocation;
                    int attempts = 0;
                    do {
                        roomLocation = getRandomLocation(baseLocation);
                        attempts++;
                    } while (isOverlapping(roomLocation, placedLocations) && attempts < 20);
                    if (attempts < 20) {
                        Clipboard clipboard = StructureUtils.loadStructure(roomLocation, theme, playerName, world);
                        if (clipboard != null) {
                            placedLocations.add(roomLocation);
                            Room room = new Room(roomLocation, rooms.size() + 1, false);
                            room.setClipboard(clipboard);
                            rooms.add(room); // Agregar la habitación a la lista
                            roomLocations.add(roomLocation); // Agregar la ubicación de la habitación al listado local

                            plugin.getLogger().warning("Se creó la habitación #" + room.getRoomNumber() + " en: " + roomLocation.getX() + "," + roomLocation.getY() + "," + roomLocation.getZ() + " En el mundo: " + roomLocation.getWorld().getName());
                        }
                    } else {
                        plugin.getLogger().warning("No se pudo colocar una habitación después de 20 intentos");
                    }

                    roomIndex.incrementAndGet();
                    plugin.getServer().getScheduler().runTaskLater(plugin, this, 80L); // Esperar 4 segundos (80 ticks)
                }
            };

            // Iniciar la primera tarea de colocar habitaciones
            plugin.getServer().getScheduler().runTask(plugin, placeRoomTask);
        });
    }

    private void spawnRoomsAndMobs(List<Room> rooms, int index) {
        if (index >= rooms.size()) {
            return;
        }

        if(!isWorldLoaded(rooms.getFirst().getLocation().getWorld())){
            return;
        }

        Room room = rooms.get(index);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            spawnRoomNumber(room.getRoomNumber(), findDriedKelpBlock(room.getLocation(), 50));
            spawnMobsInRoom(room.getLocation(), room.getClipBoard());
        });

        // Schedule the next room and mobs to be spawned after a short delay
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            spawnRoomsAndMobs(rooms, index + 1);
        }, 80L); // 4 sg delay
    }

    public void spawnRoomNumbers() {
        JavaPlugin plugin = PandoDungeons.getPlugin(PandoDungeons.class);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (Room room : rooms) {
                Location numberLocation = findDriedKelpBlock(room.getLocation(), 50);
                if (numberLocation != null) {
                    numberLocation.setY(numberLocation.getY() + 2);
                    spawnRoomNumber(room.getRoomNumber(), numberLocation);
                }
            }
        });
    }




    private Location getRandomLocation(Location baseLocation) {
        int xOffset = 200 + random.nextInt(300);  // Al menos 100 bloques de separación en X
        int zOffset = 200 + random.nextInt(300);  // Al menos 100 bloques de separación en Z
        int yOffset = 10 + random.nextInt(50);

        return baseLocation.clone().add(xOffset, yOffset, zOffset);
    }

    private boolean isOverlapping(Location location, Set<Location> placedLocations) {
        // Aumentar el umbral de distancia para evitar superposiciones cercanas
        int minDistanceSquared = 10000;  // (100 bloques)^2 = 10000
        for (Location loc : placedLocations) {
            if (loc.distanceSquared(location) < minDistanceSquared) {
                return true;
            }
        }
        return false;
    }


    public static void spawnRoomNumber(int i, Location location) {
        if (location == null) {
            PandoDungeons.getPlugin(PandoDungeons.class).getLogger().warning("La ubicación para spawnear el número de habitación es null");
            return;
        }

        location.add(0.5, 1.7, 0.5);
        JavaPlugin plugin = PandoDungeons.getPlugin(PandoDungeons.class);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

            // Configurar el nombre del ArmorStand
            armorStand.setCustomName(ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "Habitacion #" + i);
            armorStand.setCustomNameVisible(true);

            // Otras configuraciones para el ArmorStand
            armorStand.setGravity(false); // Desactivar la gravedad para que no caiga
            armorStand.setInvisible(true); // Hacer el ArmorStand invisible
            armorStand.setMarker(true); // Hacerlo un marcador para que no colisione con nada
            armorStand.setPersistent(true);
            armorStand.setRemoveWhenFarAway(false);
        });
    }

    public static void spawnRoomNumberLater(int i, Location location) {
        // Spawnear el ArmorStand en la ubicación dada
        location = StructureUtils.findDriedKelpBlock(location, 50);
        location.add(0.5, 1.1,0.5);
        JavaPlugin plugin = PandoDungeons.getPlugin(PandoDungeons.class);
        Location finalLocation = location;
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            ArmorStand armorStand = (ArmorStand) finalLocation.getWorld().spawnEntity(finalLocation, EntityType.ARMOR_STAND);

            // Configurar el nombre del ArmorStand
            armorStand.setCustomName(ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "Habitacion #" + i);
            armorStand.setCustomNameVisible(true);

            // Otras configuraciones para el ArmorStand
            armorStand.setGravity(false); // Desactivar la gravedad para que no caiga
            armorStand.setInvisible(true); // Hacer el ArmorStand invisible
            armorStand.setMarker(true); // Hacerlo un marcador para que no colisione con nada
            armorStand.setPersistent(true);
            armorStand.setRemoveWhenFarAway(false);
        });
    }

    private Location findNetheriteBlock(Location roomLocation, int searchRadius) {
        return StructureUtils.findNetheriteBlock(roomLocation, searchRadius);
    }

    private static Location findDriedKelpBlock(Location roomLocation, int searchRadius) {
        return StructureUtils.findDriedKelpBlock(roomLocation, searchRadius);
    }

    private static final Set<Material> validMaterials = new HashSet<>();

    static {
        validMaterials.add(Material.IRON_BLOCK);
        validMaterials.add(Material.REDSTONE_BLOCK);
        validMaterials.add(Material.COAL_BLOCK);
        validMaterials.add(Material.EMERALD_BLOCK);
        validMaterials.add(Material.GOLD_BLOCK);
        validMaterials.add(Material.DIAMOND_BLOCK);
        validMaterials.add(Material.PURPLE_GLAZED_TERRACOTTA);
        validMaterials.add(Material.BLACK_GLAZED_TERRACOTTA);
        validMaterials.add(Material.GRAY_GLAZED_TERRACOTTA);
        validMaterials.add(Material.MAGENTA_GLAZED_TERRACOTTA);
    }

    public static boolean isValidBlock(Block block) {
        return validMaterials.contains(block.getType());
    }

    private void spawnMobsInRoom(Location roomLocation, Clipboard clipboard) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if(roomLocation.getWorld() == null){
                return;
            }
            BlockVector3 dimensions = clipboard.getDimensions();
            int searchRadiusX = dimensions.x();
            int searchRadiusY = dimensions.y();
            int searchRadiusZ = dimensions.z();

            List<Location> mobSpawnLocations = new ArrayList<>();

            int baseX = roomLocation.getBlockX();
            int baseY = roomLocation.getBlockY();
            int baseZ = roomLocation.getBlockZ();

            World world = roomLocation.getWorld();

            // Escanear en las dimensiones de la habitacion
            for (int x = 0; x <= searchRadiusX; x++) {
                for (int y = 0; y <= searchRadiusY; y++) {
                    for (int z = 0; z <= searchRadiusZ; z++) {
                        Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
                        if (isValidBlock(block)) {
                            mobSpawnLocations.add(new Location(world, baseX + x + 0.5, baseY + y + 0.5, baseZ + z + 0.5));
                        }
                    }
                }
            }

            // Spawnear los mobs sincrónicamente en lotes
            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                private final Iterator<Location> iterator = mobSpawnLocations.iterator();

                @Override
                public void run() {
                    int batchSize = 5; // Número de mobs a generar por tick
                    int count = 0;

                    while (iterator.hasNext() && count < batchSize) {
                        Location loc = iterator.next();
                        Block block = world.getBlockAt(loc);
                        MobSpawnUtils.spawnMobs(loc, block.getType(), world);
                        count++;
                    }

                    if (iterator.hasNext()) {
                        plugin.getServer().getScheduler().runTask(plugin, this); // Reprogramar para el siguiente tick
                    }
                }
            });
        });
    }


    public static void spawnMobsLater(Location roomLocation) {
        int radius = 50;
        World world = roomLocation.getWorld();

        int startX = roomLocation.getBlockX() - radius;
        int startY = roomLocation.getBlockY() - radius;
        int startZ = roomLocation.getBlockZ() - radius;
        int endX = roomLocation.getBlockX() + radius;
        int endY = roomLocation.getBlockY() + radius;
        int endZ = roomLocation.getBlockZ() + radius;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    Location loc = new Location(world, x, y, z);
                    Block block = loc.getBlock();
                    if (isValidBlock(block)) {
                        MobSpawnUtils.spawnMobs(loc, block.getType(), world);
                    }
                }
            }
        }
    }



    public static int getRoomNumber(Location location) {
        for (Room room : rooms) {
            if (room.getLocation().equals(location)) {
                return room.getRoomNumber(); // Habitaciones numeradas del 1 al tamaño de la lista
            }
        }
        return -1; // Si no se encuentra la ubicación de la habitación
    }

    public static Location getRoomLocatioByNumber(int i){
        for(Room room : rooms){
            if(room.getRoomNumber() == i){
                return room.getLocation();
            }
        }
        return null;
    }

    public Location getRoom(int i){
        return rooms.get(i).getLocation();
    }
}
