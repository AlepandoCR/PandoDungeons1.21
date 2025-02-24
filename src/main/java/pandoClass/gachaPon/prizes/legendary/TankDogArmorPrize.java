package pandoClass.gachaPon.prizes.legendary;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TankDogArmorPrize extends PrizeItem {

    NamespacedKey wolfArmorKey;

    public TankDogArmorPrize(PandoDungeons plugin){
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        wolfArmorKey = new NamespacedKey(plugin,"wolfArmorKey");
        return createDogArmor();
    }

    @Override
    protected Quality selectQuality() {
        return Quality.LEGENDARIO;
    }

    public ItemStack createDogArmor() {
        ItemStack dogArmor = new ItemStack(Material.WOLF_ARMOR, 1);
        ArmorMeta meta = (ArmorMeta) dogArmor.getItemMeta();

        if (meta != null) {
            // Crear un modificador de atributo para aumentar la armadura
            AttributeModifier armorModifier = new AttributeModifier(
                    wolfArmorKey, 20,  // +20 de armadura extra
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.ARMOR
            );

            meta.addAttributeModifier(Attribute.MAX_HEALTH, armorModifier);

            // Aplicar el modificador al atributo de armadura
            meta.addAttributeModifier(Attribute.ARMOR, armorModifier);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + "Debes curar a tu perro despues de aplicarla");

            meta.setLore(lore);

            // Opcional: Cambiar el nombre de la armadura
            meta.setDisplayName(ChatColor.YELLOW + "Armadura Tanque de perro");

            // Aplicar los cambios al ItemStack
            dogArmor.setItemMeta(meta);
        }

        return dogArmor;
    }
}
