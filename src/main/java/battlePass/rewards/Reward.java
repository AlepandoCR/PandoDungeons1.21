package battlePass.rewards;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Reward {
    private String name;
    private ItemStack item;

    public Reward(String name, ItemStack item) {
        this.name = name;
        this.item = item;
    }

    public void applyTo(Player player) {
        player.getInventory().addItem(item);
        player.sendMessage("Has recibido la recompensa: " + name);
    }

    public ItemStack getItem(){
        return item;
    }
}
