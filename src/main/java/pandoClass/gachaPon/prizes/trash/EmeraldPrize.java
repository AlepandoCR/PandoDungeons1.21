package pandoClass.gachaPon.prizes.trash;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;

public class EmeraldPrize extends PrizeItem {
    @Override
    protected ItemStack createItem() {
        return new ItemStack(Material.EMERALD,20);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.INFERIOR;
    }
}
