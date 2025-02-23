package pandoClass.gachaPon.prizes.rare;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;

public class OmniousKeyPrize extends PrizeItem {
    @Override
    protected ItemStack createItem() {
        return new ItemStack(Material.OMINOUS_TRIAL_KEY,1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.RARO;
    }
}
