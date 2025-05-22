package pandoClass.gachaPon.prizes.epic;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

import static pandoToros.game.ArenaMaker.isRedondelWorld;
import static pandodungeons.Utils.LocationUtils.isDungeonWorld;

public class TeleportationHeartPrize extends PrizeItem {


    NamespacedKey locX;
    NamespacedKey locY;
    NamespacedKey locZ;
    NamespacedKey locWorld;
    NamespacedKey hasLoc;



    public TeleportationHeartPrize(PandoDungeons plugin){
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        locX = new NamespacedKey(plugin,"locX");
        locY = new NamespacedKey(plugin,"locY");
        locZ = new NamespacedKey(plugin,"locZ");
        locWorld = new NamespacedKey(plugin,"locWorld");
        hasLoc = new NamespacedKey(plugin,"hasLoc");
        return teleporterItem(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.EPICO;
    }

    private ItemStack teleporterItem(int amount) {
        ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA, amount);
        ItemMeta meta = item.getItemMeta();

        // Establecer el nombre con colores y negrita
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Teletransportador");

        // Inicializar datos persistentes para la ubicación (por defecto sin asignar)
        meta.getPersistentDataContainer().set(locX, PersistentDataType.DOUBLE, 0.0);
        meta.getPersistentDataContainer().set(locY, PersistentDataType.DOUBLE, 0.0);
        meta.getPersistentDataContainer().set(locZ, PersistentDataType.DOUBLE, 0.0);
        meta.getPersistentDataContainer().set(locWorld, PersistentDataType.STRING, "");
        meta.getPersistentDataContainer().set(hasLoc, PersistentDataType.BOOLEAN, false);

        // Agregar lore descriptivo y decorado
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Teletransporta a su portador y a su party cercana.");
        lore.add(ChatColor.AQUA + "Click izquierdo para activar el teletransporte.");
        lore.add(ChatColor.AQUA + "Click Derecho para guardar la ubicación.");
        lore.add(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "/party create" + ChatColor.WHITE + " Para crear una party");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }


    public static boolean isTeleporterItem(PandoDungeons plugin, ItemStack stack){
        if(!stack.hasItemMeta()){
            return false;
        }
        return stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin,"hasLoc"), PersistentDataType.BOOLEAN);
    }

    public static Location getStoredLocation(PandoDungeons plugin, ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = stack.getItemMeta();
        NamespacedKey keyHasLoc = new NamespacedKey(plugin, "hasLoc");
        if (!isTeleporterItem(plugin,stack)) {
            return null;
        }
        boolean hasLocation = meta.getPersistentDataContainer().getOrDefault(keyHasLoc, PersistentDataType.BOOLEAN, false);
        if (!hasLocation) {
            return null;
        }
        double x = meta.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "locX"), PersistentDataType.DOUBLE,0.0);
        double y = meta.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "locY"), PersistentDataType.DOUBLE,0.0);
        double z = meta.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "locZ"), PersistentDataType.DOUBLE,0.0);
        String worldName = meta.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "locWorld"), PersistentDataType.STRING, "spawn");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, x, y, z);
    }

    public static void setStoredLocation(PandoDungeons plugin, ItemStack stack, @NotNull Location loc) {
        if (stack == null || !stack.hasItemMeta()) {
            return;
        }

        String worldName = loc.getWorld().getName();

        if(isDungeonWorld(worldName) || isRedondelWorld(worldName) || worldName.equalsIgnoreCase("pvp") || worldName.equalsIgnoreCase("parkours") || worldName.equalsIgnoreCase("laberintos")){
            return;
        }

        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "locX"), PersistentDataType.DOUBLE, loc.getX());
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "locY"), PersistentDataType.DOUBLE, loc.getY());
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "locZ"), PersistentDataType.DOUBLE, loc.getZ());
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "locWorld"), PersistentDataType.STRING, worldName);
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "hasLoc"), PersistentDataType.BOOLEAN, true);
        stack.setItemMeta(meta);
    }

    public static void teleportWithAnimation(PandoDungeons plugin, Player player, ItemStack teleporter) {
        Location startLocation = player.getLocation().clone(); // Guardamos la ubicación inicial
        World world = player.getWorld();
        Location destination = getStoredLocation(plugin, teleporter); // Ubicación guardada en el teletransportador

        if (destination == null) {
            player.sendMessage(ChatColor.RED + "No hay ubicación guardada en este Teletransportador.");
            return;
        }

        player.sendMessage(ChatColor.AQUA + "Canalizando el teletransporte... No te muevas.");

        new BukkitRunnable() {
            int ticks = 0;
            boolean cancelled = false;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                // Verificar si el jugador se ha movido (ignorando la rotación)
                if (!player.getLocation().getBlock().equals(startLocation.getBlock())) {
                    player.sendMessage(ChatColor.RED + "El teletransporte fue cancelado porque te moviste.");
                    cancelled = true;
                    cancel();
                    return;
                }

                // Crear animación en círculo
                double radius = 3.0;
                double angle = (ticks * Math.PI) / 8;
                for (int i = 0; i < 8; i++) {
                    double x = startLocation.getX() + radius * Math.cos(angle + (i * Math.PI / 4));
                    double z = startLocation.getZ() + radius * Math.sin(angle + (i * Math.PI / 4));
                    Location particleLoc = new Location(world, x, startLocation.getY(), z);
                    world.spawnParticle(Particle.END_ROD, particleLoc, 2, 0, 0.1, 0, 0);
                }

                ticks += 10;

                if (ticks >= 80) { // 4 segundos
                    teleportEntities(player, startLocation, destination, plugin);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 10L); // Ejecutar cada 10 ticks (0.5 segundos)
    }

    private static void teleportEntities(Player player, Location start, Location destination, PandoDungeons plugin) {
        World world = start.getWorld();

        // Teletransportar solo mobs aliados en un radio de 3 bloques
        for (Entity entity : world.getNearbyEntities(start, 3, 3, 3)) {
            if (entity instanceof Player player1){
                if(plugin.playerPartyList.isMember(player1)){
                    if(plugin.playerPartyList.getPartyByMember(player1).isMember(player)){
                        player1.teleport(destination);
                    }
                }
            }

        }

        // Teletransportar al jugador
        player.teleport(destination);
        player.sendMessage(ChatColor.GREEN + "¡Teletransporte completado!");
        player.getWorld().playSound(destination, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
    }


}