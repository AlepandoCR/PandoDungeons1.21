package pandodungeons.pandodungeons.commands.admin.enchantments;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static pandodungeons.pandodungeons.Game.enchantments.souleater.SoulEaterEnchantment.createSoulEaterEnchantedBook;

public class getEnchantment implements CommandExecutor {
    private JavaPlugin plugin;

    public getEnchantment(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }

        Player player = ((Player) sender).getPlayer();

        assert player != null;
        if(!player.isOp()){
            player.sendMessage(ChatColor.RED + "No tienes permiso para hacer esto");
            return true;
        }

        if (args.length != 2) {
            return true;
        }

        if(args[1].equalsIgnoreCase("souleater")){
            player.getInventory().addItem(createSoulEaterEnchantedBook());
        }

        return false;
    }
}
