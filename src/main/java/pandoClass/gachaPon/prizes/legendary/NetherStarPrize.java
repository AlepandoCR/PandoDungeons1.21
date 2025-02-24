package pandoClass.gachaPon.prizes.legendary;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;

public class NetherStarPrize extends PrizeItem {
    @Override
    protected ItemStack createItem() {
        return new ItemStack(Material.NETHER_STAR,1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.LEGENDARIO;
    }
}
