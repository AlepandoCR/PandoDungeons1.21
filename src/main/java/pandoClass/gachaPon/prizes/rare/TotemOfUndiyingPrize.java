package pandoClass.gachaPon.prizes.rare;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;

public class TotemOfUndiyingPrize extends PrizeItem {
    @Override
    protected ItemStack createItem() {
        return new ItemStack(Material.TOTEM_OF_UNDYING,1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.RARO;
    }
}
