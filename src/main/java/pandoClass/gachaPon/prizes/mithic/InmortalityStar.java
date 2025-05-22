package pandoClass.gachaPon.prizes.mithic;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class InmortalityStar extends PrizeItem {
    private NamespacedKey starKey;

    public InmortalityStar(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        this.starKey = new NamespacedKey(plugin, "starKey");
        ItemStack star = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = star.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(starKey, PersistentDataType.BOOLEAN, true);
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Estrella de la Inmortalidad Eterna");

            CustomModelDataComponent component = meta.getCustomModelDataComponent();

            component.setStrings(List.of("estrella"));

            meta.setCustomModelDataComponent(component);


            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + "Cargas: " + 10);
            lore.add(ChatColor.YELLOW + "Mata Evocadores para conseguir cargas.");
            meta.setLore(lore);

            star.setItemMeta(meta);
        }
        return star;
    }

    @Override
    protected Quality selectQuality() {
        return Quality.MITICO;
    }

    /**
     * Verifica si el item es una Estrella de la Inmortalidad.
     */
    public static boolean isInmortalityStar(PandoDungeons plugin, ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return false;
        ItemMeta meta = stack.getItemMeta();
        return meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "starKey"), PersistentDataType.BOOLEAN);
    }

    /**
     * Obtiene la cantidad de cargas de la Estrella de la Inmortalidad.
     */
    public static int getStarCharges(ItemStack star) {
        if (star == null || !star.hasItemMeta()) return 0;
        ItemMeta meta = star.getItemMeta();
        if (!meta.hasLore()) return 0;

        List<String> lore = meta.getLore();
        if (lore == null) return 0;

        for (String line : lore) {
            if (line.contains("Cargas:")) {
                try {
                    return Integer.parseInt(ChatColor.stripColor(line).replace("Cargas: ", "").trim());
                } catch (NumberFormatException e) {
                    return 0; // Si el número no es válido, devuelve 0
                }
            }
        }
        return 0;
    }

    /**
     * Establece la cantidad de cargas de la Estrella de la Inmortalidad.
     */
    public static void setStarCharges(ItemStack star, int charges) {
        if (star == null || charges < 0 || !star.hasItemMeta()) return;

        ItemMeta meta = star.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();

        boolean found = false;
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).contains("Cargas:")) {
                lore.set(i, ChatColor.GOLD + "Cargas: " + charges);
                found = true;
                break;
            }
        }

        // Si no se encontró, se agrega la línea de carga
        if (!found) {
            lore.add(0, ChatColor.GOLD + "Cargas: " + charges);
        }

        meta.setLore(lore);
        star.setItemMeta(meta);
    }

    public static void useStar(PandoDungeons plugin, PlayerDeathEvent event) {
        Player player = event.getEntity();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (!isInmortalityStar(plugin, offHand)) {
            return;
        }

        int charges = getStarCharges(offHand);
        if (charges > 0) {
            event.setCancelled(true);

            player.heal(8.0);
            player.setAbsorptionAmount(6.0);
            player.setFireTicks(0);

            Location spawnLocation = player.getBedSpawnLocation();

            if(spawnLocation == null){
                spawnLocation = Bukkit.getWorld("world").getSpawnLocation();
            }

            player.teleport(spawnLocation);

            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);

            setStarCharges(offHand, charges - 1);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Tu Estrella de la Inmortalidad Eterna te ha salvado. Cargas restantes: " + (charges - 1));

            event.setCancelled(true);
        } else {
            player.sendMessage(ChatColor.RED + "Tu Estrella de la Inmortalidad Eterna no tiene cargas y no funcionó.");
        }
    }

    public static void chargeStar(PandoDungeons plugin, EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (entity.getType() == EntityType.EVOKER && killer != null) {
            ItemStack offHand = killer.getInventory().getItemInOffHand();
            if (!isInmortalityStar(plugin, offHand)) {
                return;
            }

            int charges = getStarCharges(offHand);
            setStarCharges(offHand, charges + 1);
            killer.sendMessage(ChatColor.GREEN + "Has obtenido 1 carga en tu Estrella de la Inmortalidad Eterna.");
        }
    }
}
