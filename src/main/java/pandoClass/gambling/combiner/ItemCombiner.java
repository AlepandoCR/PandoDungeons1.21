package pandoClass.gambling.combiner;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.tags.DeprecatedItemTagType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import pandoClass.RPGPlayer;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.*;

import static pandoClass.gachaPon.prizes.legendary.EscudoReflectantePrize.isReflectShield;
import static pandoClass.gachaPon.prizes.legendary.SlingShotPrize.isCustomCrossbow;
import static pandoClass.gachaPon.prizes.legendary.StormSwordPrize.isStormSword;
import static pandoClass.gachaPon.prizes.mithic.JetPackPrize.isJetPack;
import static pandoClass.gachaPon.prizes.mithic.MapachoBladePrize.isMapachoBlade;
import static pandoClass.upgrade.ItemUpgrade.updateUpgrade;
import static pandodungeons.pandodungeons.Utils.ItemUtils.isGarabiThor;

public class ItemCombiner {

    private final ItemStack item1;
    private final ItemStack item2;
    private final PandoDungeons plugin;
    private final NamespacedKey upgradeKey;


    public ItemCombiner(ItemStack item1, ItemStack item2, PandoDungeons plugin) {
        this.item1 = item1.clone();
        this.item2 = item2.clone();
        this.plugin = plugin;
        this.upgradeKey = new NamespacedKey(plugin, "item_upgrade");
    }

    public boolean canCombine(Player player) {
        if (item1.getType() != item2.getType()) {
            player.sendMessage("§cAmbos objetos deben ser del mismo material para combinarlos.");
            return false;
        }

        if (item1.getType() == Material.AIR || item2.getType() == Material.AIR) {
            player.sendMessage("§cDebes tener un objeto en cada mano para combinar.");
            return false;
        }

        if (!item1.hasItemMeta() || !item2.hasItemMeta()) {
            player.sendMessage("§cAmbos objetos deben tener mejoras o encantamientos para combinar.");
            return false;
        }

        if (hasNoFusion(item1) || hasNoFusion(item2)) {
            player.sendMessage("§cAlguno o ambos de los item no se pueden fusionar");
            return false;
        }

        boolean enchantable = false;
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.canEnchantItem(item1)) {
                enchantable = true;
                break;
            }
        }

        if (!enchantable) {
            player.sendMessage("§cEste tipo de objeto no puede ser combinado (no es encantable).");
            return false;
        }

        return chargeCost(player, 64, 250000);
    }

    public boolean hasNoFusion(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;

        if(isMapachoBlade(item,plugin) || isGarabiThor(item) || isStormSword(item,plugin)|| isCustomCrossbow(item,plugin) || isReflectShield(item,plugin) || isJetPack(item,plugin)) return true;

        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        if (!nmsItem.getComponents().has(DataComponents.CUSTOM_DATA)) return false;

        return nmsItem.getComponents().get(DataComponents.CUSTOM_DATA).contains("nofusion");
    }


    private boolean chargeCost(Player player, int diamonds, int chargedCoins) {
        RPGPlayer rpgPlayer = plugin.rpgManager.getPlayer(player);
        ItemStack[] contents = player.getInventory().getContents();

        // Verificamos primero las monedas
        if (rpgPlayer.getCoins() < chargedCoins) {
            player.sendMessage("§c✖ No tienes suficientes ☃ §c.");
            player.sendMessage("§7Necesitas: §e" + chargedCoins + " ☃");
            return false;
        }

        // Verificar si hay suficientes bloques de diamante SIN eliminarlos
        int totalDiamonds = 0;
        for (ItemStack item : contents) {
            if (item != null && item.getType() == Material.DIAMOND_BLOCK) {
                totalDiamonds += item.getAmount();
            }
        }

        if (totalDiamonds < diamonds) {
            player.sendMessage("§c✖ No tienes suficientes §b⬛ Bloques de Diamante§c.");
            player.sendMessage("§7Necesitas: §b" + diamonds + " ⬛");
            return false;
        }

        // Ahora sí, eliminar los diamantes necesarios
        int diamondsRemoved = 0;
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == Material.DIAMOND_BLOCK) {
                int count = item.getAmount();

                if (count > (diamonds - diamondsRemoved)) {
                    item.setAmount(count - (diamonds - diamondsRemoved));
                    diamondsRemoved = diamonds;
                    break;
                } else {
                    diamondsRemoved += count;
                    player.getInventory().setItem(i, null);
                }

                if (diamondsRemoved >= diamonds) break;
            }
        }

        // Quitar monedas
        rpgPlayer.removeCoins(chargedCoins);

        // Confirmación
        player.sendMessage("§a✔ ¡Combinación exitosa!");
        player.sendMessage("§7Has usado §b" + diamonds + "§b⬛ Bloques de diamante§7 y §e" + chargedCoins + " ☃ §7.");
        return true;
    }



    public ItemStack combine(Player player) {
        if (!canCombine(player)) {
            return null;
        }

        ItemStack result = new ItemStack(item1.getType());
        ItemMeta meta1 = item1.getItemMeta();
        ItemMeta meta2 = item2.getItemMeta();

        if (meta1 == null || meta2 == null) return null;

        // Copia encantamientos (mayor nivel gana)
        Map<Enchantment, Integer> combinedEnchants = new HashMap<>(meta1.getEnchants());
        for (Map.Entry<Enchantment, Integer> entry : meta2.getEnchants().entrySet()) {
            combinedEnchants.merge(entry.getKey(), entry.getValue(), Math::max);
        }
        combinedEnchants.forEach(result::addUnsafeEnchantment);

        ItemMeta resultMeta = result.getItemMeta();
        if (resultMeta == null) return null;

        // Copia nombre si alguno tiene
        if (meta1.hasDisplayName()) resultMeta.displayName(meta1.displayName());
        else if (meta2.hasDisplayName()) resultMeta.displayName(meta2.displayName());

        // Copia lore si alguno tiene
        if (meta1.hasLore()) resultMeta.lore(meta1.lore());
        else if (meta2.hasLore()) resultMeta.lore(meta2.lore());

        // Copia CustomModelData si hay
        if (meta1.hasCustomModelData()) resultMeta.setCustomModelDataComponent(meta1.getCustomModelDataComponent());
        else if (meta2.hasCustomModelData()) resultMeta.setCustomModelDataComponent(meta2.getCustomModelDataComponent());

        // Copia ItemFlags (unión de los dos sets)
        Set<ItemFlag> flags = new HashSet<>();
        flags.addAll(meta1.getItemFlags());
        flags.addAll(meta2.getItemFlags());
        flags.forEach(resultMeta::addItemFlags);

        // Combina daño (si aplica)
        if (meta1 instanceof Damageable d1 && meta2 instanceof Damageable d2 && resultMeta instanceof Damageable dr) {

            dr.setDamage(Math.min(d1.getDamage(), d2.getDamage()));
        }

        // ✅ Fusiona todos los PersistentData
        PersistentDataContainer container1 = meta1.getPersistentDataContainer();
        PersistentDataContainer container2 = meta2.getPersistentDataContainer();
        PersistentDataContainer resultContainer = resultMeta.getPersistentDataContainer();

        mergePersistentData(container1, resultContainer);
        mergePersistentData(container2, resultContainer);

        int upgrades = getTotalUpgrades(resultContainer);

        if(upgrades > 0) updateUpgrade(resultMeta,upgrades);

        result.setItemMeta(resultMeta);

        chargeItems(player);

        return result;
    }

    private void chargeItems(Player player) {
        player.getInventory().getItemInMainHand().setAmount(0);
        player.getInventory().getItemInOffHand().setAmount(0);
    }

    public int getTotalUpgrades(PersistentDataContainer container){
        return container.getOrDefault(upgradeKey,PersistentDataType.INTEGER,0);
    }

    private void mergePersistentData(PersistentDataContainer source, PersistentDataContainer target) {
        for (NamespacedKey key : source.getKeys()) {
            Object data = null;
            PersistentDataType<?, ?> matchedType = null;

            if(key.equals(upgradeKey)){
                int sourceUpg = source.getOrDefault(key, PersistentDataType.INTEGER, 0);
                int targetUpg = target.getOrDefault(key, PersistentDataType.INTEGER, 0);
                int sum = sourceUpg + targetUpg;

                plugin.getLogger().info("Merging upgrades: source=" + sourceUpg + " target=" + targetUpg + " -> total=" + sum);

                target.set(upgradeKey, PersistentDataType.INTEGER, sum);
            }else{
                for (PersistentDataType<?, ?> type : getKnownTypes()) {
                    try {
                        data = source.get(key, type);
                        if (data != null) {
                            matchedType = type;
                            break;
                        }
                    } catch (IllegalArgumentException | ClassCastException ignored) {

                    }
                }

                if (data != null) {
                    @SuppressWarnings("unchecked")
                    PersistentDataType<Object, Object> castedType = (PersistentDataType<Object, Object>) matchedType;
                    target.set(key, castedType, data);
                }
            }
        }
    }




    private Collection<PersistentDataType<?, ?>> getKnownTypes() {
        return List.of(
                PersistentDataType.STRING,
                PersistentDataType.INTEGER,
                PersistentDataType.LONG,
                PersistentDataType.FLOAT,
                PersistentDataType.DOUBLE,
                PersistentDataType.BYTE,
                PersistentDataType.SHORT,
                PersistentDataType.BYTE_ARRAY,
                PersistentDataType.INTEGER_ARRAY,
                PersistentDataType.LONG_ARRAY,
                PersistentDataType.TAG_CONTAINER,
                PersistentDataType.TAG_CONTAINER_ARRAY
        );
    }
}
