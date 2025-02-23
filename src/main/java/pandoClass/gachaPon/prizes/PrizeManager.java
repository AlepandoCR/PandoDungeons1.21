package pandoClass.gachaPon.prizes;

import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.prizes.epic.HeavyCorePrize;
import pandoClass.gachaPon.prizes.epic.NetherStarPrize;
import pandoClass.gachaPon.prizes.legendary.PrestigePrize;
import pandoClass.gachaPon.prizes.mithic.GarabiThorPrize;
import pandoClass.gachaPon.prizes.rare.NetheriteBarPrize;
import pandoClass.gachaPon.prizes.rare.OmniousKeyPrize;
import pandoClass.gachaPon.prizes.rare.ShulkerBoxPrize;
import pandoClass.gachaPon.prizes.rare.SoulWritterPrize;
import pandoClass.gachaPon.prizes.trash.EmeraldPrize;
import pandoClass.gachaPon.prizes.trash.EndCrystalPrize;
import pandoClass.gachaPon.prizes.trash.NetheriteScrapPrize;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class PrizeManager {

    private final List<PrizeItem> prizeItems = new ArrayList<>();
    private final PandoDungeons plugin;

    public PrizeManager(PandoDungeons plugin){
        this.plugin = plugin;
        setPrizeItems();
    }

    private void setPrizeItems(){
        prizeItems.add(new HeavyCorePrize());
        prizeItems.add(new NetherStarPrize());
        prizeItems.add(new PrestigePrize());
        prizeItems.add(new GarabiThorPrize(plugin));
        prizeItems.add(new NetheriteBarPrize());
        prizeItems.add(new OmniousKeyPrize());
        prizeItems.add(new ShulkerBoxPrize());
        prizeItems.add(new SoulWritterPrize(plugin));
        prizeItems.add(new EmeraldPrize());
        prizeItems.add(new EndCrystalPrize());
        prizeItems.add(new NetheriteScrapPrize());
    }

    public List<PrizeItem> getPrizeItems(){
        return prizeItems;
    }
}
