package pandoClass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PayCommand implements CommandExecutor, TabCompleter {

    private final PandoDungeons plugin;
    private static final ConcurrentHashMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private static final long COOLDOWN_TIME = 5000; // 5 segundos

    public PayCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player executingPlayer)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        if (args.length != 2) {
            executingPlayer.sendMessage(ChatColor.RED + "Uso correcto: /pagar [nombre] [cantidad]");
            return true;
        }

        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);

        // Verificar que el jugador existe
        if (targetPlayer == null) {
            executingPlayer.sendMessage(ChatColor.RED + "El jugador no esta en linea.");
            return true;
        }

        // Evitar pagos a sí mismo
        if (executingPlayer.equals(targetPlayer)) {
            executingPlayer.sendMessage(ChatColor.RED + "No puedes enviarte dinero a ti mismo.");
            return true;
        }

        // Manejo del monto
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            executingPlayer.sendMessage(ChatColor.RED + "Debes ingresar un número válido.");
            return true;
        }

        if (amount <= 0) {
            executingPlayer.sendMessage(ChatColor.RED + "Debes enviar una cantidad mayor a 0.");
            return true;
        }

        // Manejo de cooldowns
        long lastPayment = cooldowns.getOrDefault(executingPlayer.getUniqueId(), 0L);
        if (System.currentTimeMillis() - lastPayment < COOLDOWN_TIME) {
            executingPlayer.sendMessage(ChatColor.RED + "Debes esperar antes de hacer otra transferencia.");
            return true;
        }

        cooldowns.put(executingPlayer.getUniqueId(), System.currentTimeMillis());

        // Obtener los datos de los jugadores
        RPGPlayer senderData = new RPGPlayer(executingPlayer, plugin);
        RPGPlayer receiverData = new RPGPlayer(targetPlayer, plugin);

        // Verificar que el jugador tenga dinero suficiente
        if (senderData.getCoins() < amount) {
            executingPlayer.sendMessage(ChatColor.RED + "No tienes suficiente dinero.");
            return true;
        }

        // Transferir dinero
        senderData.removeCoins(amount);
        receiverData.addCoins(amount);

        // Guardar cambios
        senderData.save(senderData);
        receiverData.save(receiverData);

        executingPlayer.sendMessage(ChatColor.GREEN + "Has enviado " + ChatColor.GOLD + amount + " monedas" + ChatColor.GREEN + " a: " + ChatColor.LIGHT_PURPLE + targetPlayer.getName());
        if (targetPlayer.isOnline()) {
            targetPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Has recibido " + ChatColor.GOLD + amount + " monedas" + ChatColor.GREEN + " de: " + ChatColor.LIGHT_PURPLE + executingPlayer.getName());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }
}
