package pandoClass.gachaPon.prizes.legendary;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.Arrays;
import java.util.List;

public class TankSword extends PrizeItem {

    private NamespacedKey SWORD_KEY;

    public TankSword(PandoDungeons plugin){
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        SWORD_KEY = new NamespacedKey(plugin, "tankSword");
        return createTankSword();
    }

    @Override
    protected Quality selectQuality() {
        return Quality.LEGENDARIO;
    }

    private ItemStack createTankSword() {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_AQUA + "Espada del Coloso");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Inflige más daño cuanta más",
                    ChatColor.GRAY + "vida tengas en ese momento.",
                    "",
                    ChatColor.DARK_GREEN + "¡Más tanque, más castigo!"
            ));


            CustomModelDataComponent component = meta.getCustomModelDataComponent();

            component.setStrings(List.of("tankSword"));

            meta.setCustomModelDataComponent(component);

            // Etiqueta NBT para indicar que es la Espada del Coloso
            meta.getPersistentDataContainer().set(
                    SWORD_KEY,
                    PersistentDataType.BYTE,
                    (byte) 1
            );

            sword.setItemMeta(meta);
        }

        return sword;
    }

    public NamespacedKey getSwordKey() {
        return SWORD_KEY;
    }
}
