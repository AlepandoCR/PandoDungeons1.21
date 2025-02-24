package pandoClass.gachaPon.prizes.epic;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;

import java.util.Random;

public class RandomBannerPatternPrize extends PrizeItem {
    @Override
    protected ItemStack createItem() {
        Material[] bannerPatterns = new Material[]{
                Material.FIELD_MASONED_BANNER_PATTERN,
                Material.BORDURE_INDENTED_BANNER_PATTERN,
                Material.CREEPER_BANNER_PATTERN,
                Material.SKULL_BANNER_PATTERN,
                Material.FLOWER_BANNER_PATTERN,
                Material.MOJANG_BANNER_PATTERN,
                Material.GLOBE_BANNER_PATTERN,
                Material.PIGLIN_BANNER_PATTERN,
                Material.FLOW_BANNER_PATTERN,
                Material.GUSTER_BANNER_PATTERN
        };

        Random random = new Random();
        int index = random.nextInt(bannerPatterns.length);
        return new ItemStack(bannerPatterns[index], 1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.EPICO;
    }
}
