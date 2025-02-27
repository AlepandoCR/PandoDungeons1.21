package pandoClass.gachaPon.prizes.mithic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class TeleVillagerShardPrize extends PrizeItem {


    NamespacedKey teleVillager;


    public TeleVillagerShardPrize(PandoDungeons plugin){
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        teleVillager = new NamespacedKey(plugin,"teleVillager");
        return teleShardItem(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.MITICO;
    }

    private ItemStack teleShardItem(int amount){
        ItemStack item = new ItemStack(Material.ECHO_SHARD, amount);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Cristal de Telequinesis Aldeanil");
        meta.getPersistentDataContainer().set(teleVillager, PersistentDataType.INTEGER, 100);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_AQUA + "Podrás mover aldeanos a tu antojo");
        lore.add(ChatColor.RED + "Requiere que esté cargado");

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public static boolean isTeleVillagerShardItem(PandoDungeons plugin, ItemStack stack){
        if(!stack.hasItemMeta()){
            return false;
        }
        return stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin,"teleVillager"), PersistentDataType.INTEGER);
    }

    public static int getTeleVillagerShardBatery(PandoDungeons plugin, ItemStack stack){
        if(isTeleVillagerShardItem(plugin,stack)){
            return stack.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin,"teleVillager"), PersistentDataType.INTEGER, 0);
        }
        return 0;
    }

    public static void setTeleVillagerShardBatery(PandoDungeons plugin, ItemStack stack, int amount) {
        if (isTeleVillagerShardItem(plugin, stack)) {
            // Obtén una copia del ItemMeta
            ItemMeta meta = stack.getItemMeta();
            if (meta == null) return;

            // Actualiza el valor de la batería en el PersistentDataContainer
            NamespacedKey key = new NamespacedKey(plugin, "teleVillager");
            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, amount);

            // Obtén el lore actual o crea uno nuevo si es null
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }

            // Busca una línea que ya muestre la batería, suponiendo que contiene "Batería:"
            boolean found = false;
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                if (line.contains("Batería:")) {
                    lore.set(i, ChatColor.LIGHT_PURPLE + "Batería: " + amount);
                    found = true;
                    break;
                }
            }

            // Si no se encontró la línea, se añade al final del lore
            if (!found) {
                lore.add(ChatColor.LIGHT_PURPLE + "Batería: " + amount);
            }

            // Actualiza el meta del item y lo establece de nuevo en el ItemStack
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
    }

}