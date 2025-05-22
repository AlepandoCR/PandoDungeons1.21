package pandoClass.gachaPon.prizes.mithic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.PandoDungeons;

import java.util.*;

public class ChonetePrize extends PrizeItem {

    private NamespacedKey CHONETE_KEY;

    public ChonetePrize(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        this.CHONETE_KEY = new NamespacedKey(plugin, "choneteViento");
        return crearChonete();
    }

    @Override
    protected Quality selectQuality() {
        return Quality.MITICO;
    }

    private ItemStack crearChonete() {
        ItemStack item = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Chonete del Viento");
            meta.setLore(List.of(
                    ChatColor.GRAY + "Este sombrero canaliza la brisa pura de los llanos.",
                    ChatColor.GRAY + "Acumula velocidad al moverte.",
                    ChatColor.GRAY + "Pierdes acumulaciones si dejas de moverte o tomas daño."
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.getPersistentDataContainer().set(CHONETE_KEY, PersistentDataType.BOOLEAN, true);

            CustomModelDataComponent component = meta.getCustomModelDataComponent();

            component.setStrings(List.of("chonete"));

            meta.setCustomModelDataComponent(component);

            item.setItemMeta(meta);
        }
        return item;
    }

}
