package pandodungeons.pandodungeons.commands.game;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.pandodungeons.Game.PlayerStatsBook;
import pandodungeons.pandodungeons.Game.PlayerStatsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GiveStatsBookCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 24 * 60 * 60 * 1000; // 1 día en milisegundos

    public GiveStatsBookCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        PlayerStatsManager.loadAllPlayerStats();

        // Verificar si el jugador es operador (OP)
        if (!player.isOp()) {
            // Verificar si el jugador está en cooldown
            if (cooldowns.containsKey(playerUUID)) {
                long lastUsed = cooldowns.get(playerUUID);
                long timeSinceLastUse = System.currentTimeMillis() - lastUsed;
                if (timeSinceLastUse < COOLDOWN_TIME) {
                    long timeLeft = (COOLDOWN_TIME - timeSinceLastUse) / (1000 * 60);
                    player.sendMessage(ChatColor.RED + "Debes esperar " + timeLeft + " minutos antes de usar este comando nuevamente.");
                    return true;
                }
            }
        }

        // Dar el libro al jugador
        player.getInventory().addItem(PlayerStatsBook.getPlayerStatsBook(player));
        player.sendMessage(ChatColor.GREEN + "Has recibido un libro con tus estadísticas de mazmorras.");

        // Registrar el tiempo de uso del comando
        cooldowns.put(playerUUID, System.currentTimeMillis());

        return true;
    }
}
