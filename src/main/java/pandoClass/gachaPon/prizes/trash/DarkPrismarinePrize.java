package pandoClass.gachaPon.prizes.trash;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;

public class DarkPrismarinePrize extends PrizeItem {
    @Override
    protected ItemStack createItem() {
        return new ItemStack(Material.DARK_PRISMARINE,64);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.INFERIOR;
    }
}
