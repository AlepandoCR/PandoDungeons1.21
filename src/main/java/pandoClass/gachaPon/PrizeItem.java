package pandoClass.gachaPon;

import org.bukkit.inventory.ItemStack;
import pandodungeons.PandoDungeons;

public abstract class PrizeItem {
    protected ItemStack item;
    protected Quality quality;
    protected PandoDungeons plugin;

    public PrizeItem(PandoDungeons plugin){
        this.plugin = plugin;
        quality = selectQuality();
        item = createItem();
    }

    public PrizeItem(){
        quality = selectQuality();
        item = createItem();
    }

    protected abstract ItemStack createItem();

    protected abstract Quality selectQuality();

    public ItemStack getItem() {
        return item;
    }

    public Quality getQuality() {
        return quality;
    }
}
