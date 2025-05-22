package pandoClass.gachaPon.prizes.mithic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class TeleShardPrize extends PrizeItem {


    NamespacedKey teleShard;


    public TeleShardPrize(PandoDungeons plugin){
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        teleShard = new NamespacedKey(plugin,"teleShard");
        return teleShardItem(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.MITICO;
    }

    private ItemStack teleShardItem(int amount){
        ItemStack item = new ItemStack(Material.ECHO_SHARD, amount);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Cristal de Telequinesis");
        meta.getPersistentDataContainer().set(teleShard, PersistentDataType.INTEGER, 100);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_AQUA + "Podrás mover enemigos a tu antojo");
        lore.add(ChatColor.RED + "Requiere que esté cargado");

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public static boolean isTeleShardItem(PandoDungeons plugin, ItemStack stack){
        if(!stack.hasItemMeta()){
            return false;
        }
        return stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin,"teleShard"), PersistentDataType.INTEGER);
    }

    public static int getTeleShardBatery(PandoDungeons plugin, ItemStack stack){
        if(isTeleShardItem(plugin,stack)){
            return stack.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin,"teleShard"), PersistentDataType.INTEGER, 0);
        }
        return 0;
    }

    public static void setTeleShardBatery(PandoDungeons plugin, ItemStack stack, int amount) {
        if (isTeleShardItem(plugin, stack)) {
            // Obtén una copia del ItemMeta
            ItemMeta meta = stack.getItemMeta();
            if (meta == null) return;

            // Actualiza el valor de la batería en el PersistentDataContainer
            NamespacedKey key = new NamespacedKey(plugin, "teleShard");
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