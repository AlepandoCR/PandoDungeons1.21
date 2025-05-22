package pandoClass.gachaPon.prizes.legendary;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class EscudoReflectantePrize extends PrizeItem{

    private NamespacedKey reflectKey;

    public EscudoReflectantePrize(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        this.reflectKey = new NamespacedKey(plugin, "escudo_reflectante");
        return createReflectShield();
    }

    @Override
    protected Quality selectQuality() {
        return Quality.LEGENDARIO;
    }

    private ItemStack createReflectShield() {
        ItemStack item = new ItemStack(Material.SHIELD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Escudo Reflectante");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Un escudo con una superficie extraña...");
        lore.add(ChatColor.LIGHT_PURPLE + "Puede devolver proyectiles a su atacante.");
        meta.setLore(lore);

        CustomModelDataComponent component = meta.getCustomModelDataComponent();
        component.setStrings(List.of("bounceshield"));
        meta.setCustomModelDataComponent(component);

        // Agregar NBT
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(reflectKey, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isReflectShield(ItemStack itemStack, PandoDungeons plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "escudo_reflectante");
        return itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN);
    }

}
