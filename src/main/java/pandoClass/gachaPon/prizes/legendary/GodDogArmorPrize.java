package pandoClass.gachaPon.prizes.legendary;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class GodDogArmorPrize extends PrizeItem {

    NamespacedKey wolfArmorKey;

    public GodDogArmorPrize(PandoDungeons plugin){
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

            AttributeModifier otherModifier = new AttributeModifier(
                    wolfArmorKey, 6,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.ARMOR
            );

            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, otherModifier);

            meta.addAttributeModifier(Attribute.MOVEMENT_SPEED, otherModifier);

            meta.addAttributeModifier(Attribute.MAX_HEALTH, armorModifier);

            // Aplicar el modificador al atributo de armadura
            meta.addAttributeModifier(Attribute.ARMOR, armorModifier);

            // Opcional: Cambiar el nombre de la armadura
            meta.setDisplayName(ChatColor.AQUA + "Armadura Dios de perro");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + "Debes curar a tu perro despues de aplicarla");

            meta.setLore(lore);

            // Aplicar los cambios al ItemStack
            dogArmor.setItemMeta(meta);
        }

        return dogArmor;
    }
}
