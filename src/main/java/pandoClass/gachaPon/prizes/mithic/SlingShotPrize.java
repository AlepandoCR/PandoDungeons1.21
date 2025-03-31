package pandoClass.gachaPon.prizes.mithic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.PandoDungeons;
import java.util.ArrayList;
import java.util.List;

public class SlingShotPrize extends PrizeItem {

    private NamespacedKey customCrossbowKey;

    public SlingShotPrize(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        this.customCrossbowKey = new NamespacedKey(plugin, "SlingShot");
        return createCustomCrossbow(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.MITICO;
    }

    private ItemStack createCustomCrossbow(int amount) {
        ItemStack item = new ItemStack(Material.CROSSBOW, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "Resortera Clandestína");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Una resortera mítica con poder de disparar 2 flechas más ("+ ChatColor.WHITE + "Compatible con Multishot" + ChatColor.GOLD + ").");
        meta.setLore(lore);

        CustomModelDataComponent component = meta.getCustomModelDataComponent();

        component.setStrings(List.of("slingshot"));

        meta.setCustomModelDataComponent(component);

        meta.getPersistentDataContainer().set(customCrossbowKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isCustomCrossbow(ItemStack itemStack, PandoDungeons plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "SlingShot");
        if (itemStack.hasItemMeta()) {
            return itemStack.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN);
        }
        return false;
    }

    public static void fireCustomCrossbow(Player player, ItemStack crossbow,PandoDungeons plugin) {
        if(!isCustomCrossbow(crossbow,plugin))return;
        int multishotLevel = crossbow.getEnchantmentLevel(Enchantment.MULTISHOT);
        int totalArrows = 2 + multishotLevel; // Dispara 2 flechas adicionales al nivel de Multishot
        double spreadAngle = Math.PI / (totalArrows - 1); // Ángulo entre flechas para formar un semicírculo

        Vector direction = player.getLocation().getDirection();
        double initialYaw = Math.atan2(direction.getZ(), direction.getX());

        for (int i = 0; i < totalArrows; i++) {
            double angle = initialYaw - Math.PI / 2 + (spreadAngle * i);
            Vector arrowDirection = new Vector(Math.cos(angle), direction.getY(), Math.sin(angle)).normalize();
            Arrow arrow = player.launchProjectile(Arrow.class);
            arrow.setVelocity(arrowDirection.multiply(2));
        }
    }
}
