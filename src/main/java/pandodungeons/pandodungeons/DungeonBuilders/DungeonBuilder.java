package pandodungeons.pandodungeons.DungeonBuilders;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import pandodungeons.pandodungeons.Game.PlayerStatsManager;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.Elements.Room;
import pandodungeons.pandodungeons.Utils.LocationUtils;
import pandodungeons.pandodungeons.Utils.MobSpawnUtils;
import pandodungeons.pandodungeons.Utils.StructureUtils;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static pandodungeons.pandodungeons.Utils.StructureUtils.findDriedKelpBlock;
import static pandodungeons.pandodungeons.Utils.StructureUtils.isWorldLoaded;

public class DungeonBuilder {
    private final PandoDungeons plugin;
    private final World world;
    private final Random random;
    private final List<Room> rooms;
    private final Player player;
    private final List<Location> placedLocations;
    private final String subclassKey;

    private static final Set<Material> validMaterials = new HashSet<>();

    public DungeonBuilder(PandoDungeons plugin, World world, Player player, String subclassKey) {
        this.plugin = plugin;
        this.world = world;
        this.random = new Random();
        this.player = player;
        this.rooms = new ArrayList<>();
        this.placedLocations = new ArrayList<>();
        this.subclassKey = subclassKey;
    }

    static  {
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

    public void buildDungeon(Location baseLocation, String theme, String playerName) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (world == null || !isWorldLoaded(world)) return;

            PlayerStatsManager stats = PlayerStatsManager.getPlayerStatsManager(player);
            int prestige = stats.getPrestige();
            int numRooms = 7 + random.nextInt(4) + (prestige / 3);
            AtomicInteger roomIndex = new AtomicInteger(0);

            Runnable placeRoomTask = new Runnable() {
                @Override
                public void run() {
                    if (roomIndex.get() >= numRooms) {
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            LocationUtils.saveDungeonRoomLocations(playerName, placedLocations);
                            spawnRoomsAndMobs(new ArrayList<>(rooms), 0);
                        });
                        return;
                    }

                    int attempts = 0;
                    Location roomLocation;
                    do {
                        roomLocation = findValidRoomLocation(baseLocation);
                        attempts++;
                    } while (placedLocations.contains(roomLocation) && attempts < 20);

                    if (attempts >= 20) {
                        plugin.getLogger().warning("No se pudo colocar una habitación después de 20 intentos");
                        return;
                    }

                    Clipboard clipboard = StructureUtils.loadStructure(roomLocation, theme, playerName, world);
                    if (clipboard != null) {
                        if(roomLocation == null)return;
                        roomLocation.getChunk().load();
                        placedLocations.add(roomLocation);
                        Room room = new Room(roomLocation, rooms.size() + 1, false);
                        room.setClipboard(clipboard);
                        rooms.add(room);
                        plugin.getLogger().info("Se creó la habitación #" + room.getRoomNumber() +
                                " en: " + roomLocation.getX() + ", " + roomLocation.getY() + ", " +
                                roomLocation.getZ() + " en el mundo: " + roomLocation.getWorld().getName());
                    }

                    roomIndex.incrementAndGet();
                    plugin.getServer().getScheduler().runTaskLater(plugin, this, 80L);
                }
            };

            plugin.getServer().getScheduler().runTask(plugin, placeRoomTask);
        });
    }

    private Location findValidRoomLocation(Location base) {
        int maxAttempts = 1000;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            Location candidate = getRandomLocation(base);

            boolean tooClose = false;
            for (Location placed : placedLocations) {
                if (candidate.distanceSquared(placed) < (100 * 100)) {
                    tooClose = true;
                    break;
                }
            }

            if (!tooClose) {
                return candidate;
            }
        }
        return null; // No se encontró ubicación válida tras muchos intentos
    }


    private void spawnRoomsAndMobs(List<Room> rooms, int index) {
        if (index >= rooms.size() || world == null || !isWorldLoaded(world)) return;

        Room room = rooms.get(index);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Location numberLocation = findDriedKelpBlock(room.getLocation(), 50);
            if (numberLocation != null) spawnRoomNumber(room.getRoomNumber(), numberLocation);
            spawnMobsInRoom(room.getLocation(), room.getClipBoard());
        });

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> spawnRoomsAndMobs(rooms, index + 1), 80L);
    }

    private Location getRandomLocation(Location center) {
        int radius = 500;
        int x = center.getBlockX() + random.nextInt(radius * 2) - radius;
        int z = center.getBlockZ() + random.nextInt(radius * 2) - radius;
        int y = center.getBlockY(); // Asumimos nivel plano

        return new Location(world, x, y, z);
    }


    public static void spawnRoomNumber(int i, Location location) {
        if (location == null) return;

        JavaPlugin plugin = PandoDungeons.getPlugin(PandoDungeons.class);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location.add(0.5, 1.7, 0.5), EntityType.ARMOR_STAND);
            armorStand.setCustomName(ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "Habitación #" + i);
            armorStand.setCustomNameVisible(true);
            armorStand.setGravity(false);
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            armorStand.setPersistent(true);
            armorStand.setRemoveWhenFarAway(false);
        });
    }

    public static boolean isValidBlock(Block block) {
        return validMaterials.contains(block.getType());
    }

    private void spawnMobsInRoom(Location roomLocation, Clipboard clipboard) {
        if (roomLocation.getWorld() == null || clipboard == null) return;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            BlockVector3 dimensions = clipboard.getDimensions();
            List<Location> mobSpawnLocations = new ArrayList<>();

            World world = roomLocation.getWorld();
            int baseX = roomLocation.getBlockX();
            int baseY = roomLocation.getBlockY();
            int baseZ = roomLocation.getBlockZ();

            for (int x = 0; x <= dimensions.x(); x++) {
                for (int y = 0; y <= dimensions.y(); y++) {
                    for (int z = 0; z <= dimensions.z(); z++) {
                        Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
                        if (isValidBlock(block)) {
                            mobSpawnLocations.add(new Location(world, baseX + x + 0.5, baseY + y + 0.5, baseZ + z + 0.5));
                        }
                    }
                }
            }

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (Location loc : mobSpawnLocations) {
                    // Pass the subclassKey to the overloaded spawnMobs method
                    MobSpawnUtils.spawnMobs(loc, loc.getBlock().getType(), world, this.subclassKey);
                }
            });
        });
    }

    public static void spawnMobsLater(Location roomLocation, String subclassKey) {
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
                        // Pass the subclassKey to the overloaded spawnMobs method
                        MobSpawnUtils.spawnMobs(loc, block.getType(), world, subclassKey);
                    }
                }
            }
        }
    }


}
