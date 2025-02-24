package pandoClass.gachaPon.prizes.rare;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;

import java.util.Random;

public class RandomEggPrize extends PrizeItem {
    @Override
    protected ItemStack createItem() {
        Material[] spawnEggs = new Material[]{
                Material.SPIDER_SPAWN_EGG,
                Material.ZOMBIE_SPAWN_EGG,
                Material.SKELETON_SPAWN_EGG,
                Material.CREEPER_SPAWN_EGG,
                Material.ENDERMAN_SPAWN_EGG,
                Material.WITCH_SPAWN_EGG,
                Material.ZOMBIE_VILLAGER_SPAWN_EGG,
                Material.IRON_GOLEM_SPAWN_EGG,
                Material.SLIME_SPAWN_EGG,
                Material.POLAR_BEAR_SPAWN_EGG,
                Material.LLAMA_SPAWN_EGG,
                Material.WOLF_SPAWN_EGG,
                Material.OCELOT_SPAWN_EGG,
                Material.CHICKEN_SPAWN_EGG,
                Material.PIG_SPAWN_EGG,
                Material.COW_SPAWN_EGG,
                Material.HORSE_SPAWN_EGG,
                Material.RABBIT_SPAWN_EGG,
                Material.DONKEY_SPAWN_EGG,
                Material.MULE_SPAWN_EGG,
                Material.SHEEP_SPAWN_EGG,
                Material.VILLAGER_SPAWN_EGG,
                Material.FOX_SPAWN_EGG,
                Material.CAT_SPAWN_EGG,
                Material.PANDA_SPAWN_EGG,
                Material.TURTLE_SPAWN_EGG,
                Material.AXOLOTL_SPAWN_EGG,
                Material.BEE_SPAWN_EGG,
                Material.FROG_SPAWN_EGG,
                Material.SNIFFER_SPAWN_EGG
        };

        Random random = new Random();
        int index = random.nextInt(spawnEggs.length);
        return new ItemStack(spawnEggs[index], 1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.RARO;
    }
}
