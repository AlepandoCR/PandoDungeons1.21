package pandoClass.gachaPon.prizes.legendary;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

import static pandoToros.game.ArenaMaker.isRedondelWorld;
import static pandodungeons.pandodungeons.Utils.LocationUtils.isDungeonWorld;

public class StormSwordPrize extends PrizeItem {


    private NamespacedKey SWORD_KEY;



    public StormSwordPrize(PandoDungeons plugin){
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        SWORD_KEY = new NamespacedKey(plugin, "stormSword");
        return createStormSword();
    }

    @Override
    protected Quality selectQuality() {
        return Quality.LEGENDARIO;
    }

    /**
     * Crea la Espada de las Tormentas con características personalizadas.
     * @return Un ItemStack que representa la Espada de las Tormentas.
     */
    private ItemStack createStormSword() {
        // Crea una espada de diamante
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sword.getItemMeta();

        if (meta != null) {
            // Establece el nombre y la descripción (lore) del ítem
            meta.setDisplayName(ChatColor.AQUA + "Espada de las Tormentas");
            meta.setLore(java.util.Arrays.asList(
                    ChatColor.GRAY + "Al golpear a un enemigo,",
                    ChatColor.GRAY + "hay una probabilidad de que caiga un rayo."));

            // Añade una clave persistente para identificar que este ítem es una "Espada de las Tormentas"
            meta.getPersistentDataContainer().set(SWORD_KEY, PersistentDataType.BOOLEAN, true);

            CustomModelDataComponent component = meta.getCustomModelDataComponent();

            component.setStrings(List.of("thunderSword"));

            meta.setCustomModelDataComponent(component);

            // Establece los metadatos del ítem
            sword.setItemMeta(meta);
        }

        return sword;
    }


    /**
     * Verifica si un ítem es la "Espada de las Tormentas".
     * @param item El ítem que se va a verificar.
     * @return true si el ítem es la Espada de las Tormentas, false de lo contrario.
     */
    public static boolean isStormSword(ItemStack item, PandoDungeons plugin) {
        if (item == null || !item.hasItemMeta()) return false;

        NamespacedKey SWORD_KEY = new NamespacedKey(plugin, "stormSword");

        // Obtiene los metadatos del ítem
        ItemMeta meta = item.getItemMeta();

        // Verifica si el ítem tiene la clave persistente que identifica a la espada
        return meta != null && meta.getPersistentDataContainer().has(SWORD_KEY, PersistentDataType.BOOLEAN);
    }
}