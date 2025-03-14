package pandoClass.upgrade;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.RPGPlayer;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.*;

import static pandoClass.gachaPon.prizes.epic.InstaUpgradeShard.isUpgradeShardItem;
import static pandoClass.gachaPon.prizes.mithic.InstaMegaUpgradeShard.isMegaUpgradeShardItem;

public class ItemUpgrade {
    private final PandoDungeons plugin;
    private final NamespacedKey upgradeKey;
    private final Random random = new Random();

    public ItemUpgrade(PandoDungeons plugin) {
        this.plugin = plugin;
        this.upgradeKey = new NamespacedKey(plugin, "item_upgrade");
    }

    public void upgradeItem(ItemStack item, Player player, Location villagerLocation, Villager villager) {
        if (item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        Map<Enchantment, Integer> enchantments = meta.getEnchants();
        if (enchantments.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Este objeto no tiene encantamientos para mejorar.");
            return;
        }

        boolean megaUpgrade = removeMegaUpgradeShard(player, 1);

        // **Filtrar encantamientos que no pueden mejorar más**
        List<Enchantment> eligibleEnchantments = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            if (entry.getValue() < 8 && !megaUpgrade) { // Solo permitir mejoras si el nivel es menor a 8
                eligibleEnchantments.add(entry.getKey());
            }else if(megaUpgrade){
                eligibleEnchantments.add(entry.getKey());
            }
        }

        // **Si no hay encantamientos elegibles, notificar al jugador**
        if (eligibleEnchantments.isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "Este objeto ya ha alcanzado su nivel máximo de mejora.");
            return;
        }

        PersistentDataContainer data = meta.getPersistentDataContainer();
        int upgradeCount = data.getOrDefault(upgradeKey, PersistentDataType.INTEGER, 0);

        // **Calcular costo en monedas** (10,000 + extra basado en las mejoras previas)
        int coinCost = 10000 + (upgradeCount * 10000);
        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);

        if(!megaUpgrade){
            if (!removeUpgradeShard(player, 1)) {
                if (rpgPlayer.getCoins() < coinCost) {
                    player.sendMessage(ChatColor.RED + "No tienes suficientes monedas. Se requieren " + coinCost + " monedas.");
                    return;
                }

                // **Verificar y eliminar 20 bloques de diamante**
                if (!removeDiamonds(player, 20)) {
                    player.sendMessage(ChatColor.AQUA + "No tienes suficientes bloques de diamante. Se requieren 20 bloques de diamante.");
                    return;
                }
                // **Cobrar monedas**
                rpgPlayer.removeCoins(coinCost);
            }
        }

        // **Elegir un encantamiento aleatorio de los elegibles y mejorar su nivel**
        Enchantment chosenEnchant = eligibleEnchantments.get(random.nextInt(eligibleEnchantments.size()));
        int newLevel = enchantments.get(chosenEnchant) + 1;

        meta.addEnchant(chosenEnchant, newLevel, true);

        // **Actualizar mejoras en el item**
        data.set(upgradeKey, PersistentDataType.INTEGER, upgradeCount + 1);

        List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();

        // **Eliminar la línea anterior de mejoras antes de agregar la nueva**
        lore.removeIf(line -> ChatColor.stripColor(line).startsWith("Mejora #"));

        // **Agregar la nueva línea de mejora**
        lore.add(ChatColor.GREEN + "Mejora " + ChatColor.YELLOW + "#" + (upgradeCount + 1));
        meta.setLore(lore);

        item.setItemMeta(meta);
        new UpgradeAnim(plugin, player, villagerLocation, villager).animateItem(item);

        sendMessage(player, "Mejoraste " + ChatColor.LIGHT_PURPLE + chosenEnchant.getKey().getKey() + ChatColor.RESET + " a nivel " + ChatColor.DARK_PURPLE + newLevel + ChatColor.RESET + "!");
    }


    private boolean removeDiamonds(Player player, int amount) {
        ItemStack[] contents = player.getInventory().getContents();
        int diamondsRemoved = 0;

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == Material.DIAMOND_BLOCK) {
                int count = item.getAmount();

                if (count > (amount - diamondsRemoved)) {
                    item.setAmount(count - (amount - diamondsRemoved));
                    diamondsRemoved = amount;
                } else {
                    diamondsRemoved += count;
                    player.getInventory().setItem(i, null);
                }

                if (diamondsRemoved >= amount) {
                    return true;
                }
            }
        }
        return false; // No tenía suficientes diamantes
    }

    private boolean removeUpgradeShard(Player player, int amount) {
        ItemStack[] contents = player.getInventory().getContents();
        int shardsRemoved = 0;

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && isUpgradeShardItem(plugin ,item)) {
                int count = item.getAmount();

                if (count > (amount - shardsRemoved)) {
                    item.setAmount(count - (amount - shardsRemoved));
                    shardsRemoved = amount;
                } else {
                    shardsRemoved += count;
                    player.getInventory().setItem(i, null);
                }

                if (shardsRemoved >= amount) {
                    return true;
                }
            }
        }
        return false; // No tenía suficientes diamantes
    }

    private boolean removeMegaUpgradeShard(Player player, int amount) {
        ItemStack[] contents = player.getInventory().getContents();
        int shardsRemoved = 0;

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && isMegaUpgradeShardItem(plugin ,item)) {
                int count = item.getAmount();

                if (count > (amount - shardsRemoved)) {
                    item.setAmount(count - (amount - shardsRemoved));
                    shardsRemoved = amount;
                } else {
                    shardsRemoved += count;
                    player.getInventory().setItem(i, null);
                }

                if (shardsRemoved >= amount) {
                    return true;
                }
            }
        }
        return false; // No tenía suficientes diamantes
    }

    private void sendMessage(Player player, String chosenEnchant) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendMessage(chosenEnchant);
            }
        }.runTaskLater(plugin, 200);
    }
}
