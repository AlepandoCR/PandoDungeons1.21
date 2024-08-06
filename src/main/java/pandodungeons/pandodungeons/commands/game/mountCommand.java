package pandodungeons.pandodungeons.commands.game;

import net.minecraft.world.entity.EntityType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import pandodungeons.pandodungeons.CustomEntities.pandaMount.CustomPanda;

public class mountCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Uso: /dungeons montura <montura>");
            return true;
        }

        if (args[1].equalsIgnoreCase("panda")) {
            if (player.hasPermission("pandodungeons.pandaMount")) {
                PlayerInventory inventory = player.getInventory();
                int bambooCount = 0;

                // Contamos el total de bambú en el inventario
                for (ItemStack item : inventory.getContents()) {
                    if (item != null && item.getType() == Material.BAMBOO) {
                        bambooCount += item.getAmount();
                    }
                }

                if (bambooCount >= 7) {
                    // Reducimos el número de bambú en el inventario
                    int amountToRemove = 7;
                    for (int i = 0; i < inventory.getSize(); i++) {
                        ItemStack item = inventory.getItem(i);
                        if (item != null && item.getType() == Material.BAMBOO) {
                            if (item.getAmount() <= amountToRemove) {
                                amountToRemove -= item.getAmount();
                                inventory.setItem(i, null);
                            } else {
                                item.setAmount(item.getAmount() - amountToRemove);
                                break;
                            }
                        }
                        if (amountToRemove <= 0) break;
                    }

                    // Invocamos al panda
                    CustomPanda panda = new CustomPanda(EntityType.PANDA, ((CraftWorld) player.getWorld()).getHandle(), player);
                    panda.setRider(player);
                    player.sendMessage(ChatColor.GREEN + "¡Montura de panda invocada con éxito!");
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "No tienes 7 de bambú en tu inventario");
                }
            } else {
                player.sendMessage(ChatColor.RED + "No tienes permiso para usar esta montura.");
            }
        }
        return true;
    }
}
