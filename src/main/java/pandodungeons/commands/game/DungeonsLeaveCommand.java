package pandodungeons.commands.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.Utils.LocationUtils;
import pandodungeons.Game.RoomManager;
import pandodungeons.Utils.StructureUtils;

import java.util.Iterator;
import java.util.Locale;

public class DungeonsLeaveCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public DungeonsLeaveCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por jugadores.");
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getUniqueId().toString();
        String nombreJugador = player.getName().toLowerCase(Locale.ROOT);

        String dungeonID = "dungeon_" + playerName;
        if (!LocationUtils.hasActiveDungeon(playerName)) {
            player.sendMessage("No tienes ninguna dungeon activa");
            return true;
        }

        // Obtener la ubicación guardada antes de entrar a la dungeon
        Location savedLocation = LocationUtils.getLocationFromJSON(LocationUtils.getPlayerLocationData(playerName));
        if (savedLocation == null) {
            player.sendMessage("No se encontró la ubicación guardada antes de entrar a la dungeon.");
            return true;
        }


        removeAllBossBars(player);
        player.teleport(savedLocation);

        // Detener el RoomManager si está activo
        RoomManager roomManager = RoomManager.getActiveRoomManager(player);
        if(roomManager != null){
            roomManager.stopRoomHandling(); // Detener el bucle de manejo de habitaciones
        }

        StructureUtils.removeDungeon(nombreJugador, plugin);
        LocationUtils.removeDungeonLocationData(dungeonID);
        LocationUtils.removePlayerLocationData(playerName);

        player.sendMessage("Has salido de tu dungeon personal.");
        player.setGameMode(GameMode.SURVIVAL);

        return true;
    }
    public static void removeAllBossBars(Player player) {
        // Obtener el iterador de todas las boss bars registradas en el servidor
        Iterator<KeyedBossBar> bossBarIterator = Bukkit.getBossBars();

        // Itera sobre todas las boss bars usando el iterador
        while (bossBarIterator.hasNext()) {
            KeyedBossBar bossBar = bossBarIterator.next();
            // Si el jugador está recibiendo esta boss bar, la elimina
            if (bossBar.getPlayers().contains(player)) {
                bossBar.removePlayer(player);
            }
        }
    }
}
