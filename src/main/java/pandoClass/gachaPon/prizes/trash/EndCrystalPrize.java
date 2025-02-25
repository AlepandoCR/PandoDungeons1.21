package pandoClass.gachaPon.prizes.trash;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;

public class EndCrystalPrize extends PrizeItem {
    @Override
    protected ItemStack createItem() {
        return new ItemStack(Material.END_CRYSTAL,1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.INFERIOR;
    }
}
