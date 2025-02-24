package pandoClass.gachaPon.prizes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.prizes.epic.*;
import pandoClass.gachaPon.prizes.legendary.*;
import pandoClass.gachaPon.prizes.mithic.GarabiThorPrize;
import pandoClass.gachaPon.prizes.mithic.GodDogArmorPrize;
import pandoClass.gachaPon.prizes.mithic.JetPackPrize;
import pandoClass.gachaPon.prizes.mithic.MapachoBladePrize;
import pandoClass.gachaPon.prizes.rare.*;
import pandoClass.gachaPon.prizes.trash.*;
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
        prizeItems.add(new EffectShieldPrize(plugin));
        prizeItems.add(new JetPackPrize(plugin));
        prizeItems.add(new RocketBootsPrize(plugin));
        prizeItems.add(new RandomEggPrize());
        prizeItems.add(new RandomEggPrize());
        prizeItems.add(new RandomEggPrize());
        prizeItems.add(new RandomEggPrize());
        prizeItems.add(new MapachoBladePrize(plugin));
        prizeItems.add(new TotemOfUndiyingPrize());
        prizeItems.add(new DogArmorPrize());
        prizeItems.add(new TankDogArmorPrize(plugin));
        prizeItems.add(new GodDogArmorPrize(plugin));
        prizeItems.add(new DarkPrismarinePrize());
        prizeItems.add(new SkulkSensorPrize());
        prizeItems.add(new EnchantedGolderApplePrize());
        prizeItems.add(new TntPrize());
        prizeItems.add(new RandomSmithingTemplatePrize());
        prizeItems.add(new RandomBannerPatternPrize());
    }

    public List<PrizeItem> getPrizeItems(){
        return prizeItems;
    }

    public NamespacedKey getGachaTokenKey(){
        return new NamespacedKey(plugin,"gachaToken");
    }

    public Boolean hasGatchaToken(ItemStack itemStack){
        return itemStack.getPersistentDataContainer().has(getGachaTokenKey(),PersistentDataType.BOOLEAN);
    }

    public ItemStack gachaToken(){
        ItemStack itemStack = new ItemStack(Material.SUNFLOWER,1);
        ItemMeta meta = itemStack.getItemMeta();

        meta.getPersistentDataContainer().set(getGachaTokenKey(), PersistentDataType.BOOLEAN, true);

        meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Ficha de gachapon");
        meta.setRarity(ItemRarity.EPIC);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Te servirá para abrir gachapon");
        lore.add(ChatColor.GOLD + "/warp gachapon");

        meta.setLore(lore);

        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
