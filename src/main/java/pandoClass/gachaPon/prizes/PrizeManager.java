package pandoClass.gachaPon.prizes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandoClass.gachaPon.prizes.epic.*;
import pandoClass.gachaPon.prizes.legendary.*;
import pandoClass.gachaPon.prizes.mithic.*;
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
        prizeItems.add(new ReparationShardPrize(plugin));
        prizeItems.add(new TeleShardPrize(plugin));
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

    public Inventory createPrizeInventory() {

        // Organizar los ítems por calidad
        List<PrizeItem> basura = new ArrayList<>();
        List<PrizeItem> raro = new ArrayList<>();
        List<PrizeItem> epico = new ArrayList<>();
        List<PrizeItem> legendario = new ArrayList<>();
        List<PrizeItem> mitico = new ArrayList<>();

        for (PrizeItem prize : prizeItems) {
            switch (prize.getQuality()) {
                case INFERIOR -> basura.add(prize);
                case RARO -> raro.add(prize);
                case EPICO -> epico.add(prize);
                case LEGENDARIO -> legendario.add(prize);
                case MITICO -> mitico.add(prize);
            }
        }

        // Unir todas las listas en orden
        List<PrizeItem> orderedPrizes = new ArrayList<>();
        orderedPrizes.addAll(basura);
        orderedPrizes.addAll(raro);
        orderedPrizes.addAll(epico);
        orderedPrizes.addAll(legendario);
        orderedPrizes.addAll(mitico);

        // Calcular el tamaño del inventario: el mínimo múltiplo de 9 que pueda contener todos los ítems
        int itemCount = orderedPrizes.size();
        int inventorySize = ((itemCount + 8) / 9) * 9;
        Inventory inv = Bukkit.createInventory(null, inventorySize, ChatColor.LIGHT_PURPLE + "Premios Gachapon");

        // Agregar los ítems en orden con lore indicando su calidad (sin modificar los originales)
        for (PrizeItem prize : orderedPrizes) {
            ItemStack item = new ItemStack(prize.getItem()); // Crear una copia del ítem
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                Quality quality = prize.getQuality();
                List<PrizeItem> group = getGroup(quality,basura,raro,epico,legendario,mitico);
                List<String> lore = (meta.getLore() != null) ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                lore.add(ChatColor.LIGHT_PURPLE + quality.name()); // Añade la calidad en mayúsculas
                lore.add(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD +"%" + ChatColor.WHITE + getPercentageFromQuality(quality) / group.size());
                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            inv.addItem(item);
        }

        return inv;
    }

    @SafeVarargs
    public final List<PrizeItem> getGroup(Quality quality, List<PrizeItem>... lists){
        return switch (quality){
            case INFERIOR -> lists[0];
            case RARO -> lists[1];
            case EPICO -> lists[2];
            case LEGENDARIO -> lists[3];
            case MITICO -> lists[4];
        };
    }

    public double getPercentageFromQuality(Quality quality){
        return switch (quality){
            case INFERIOR -> 30.0;
            case RARO -> 40.0;
            case EPICO -> 20.0;
            case LEGENDARIO -> 8.0;
            case MITICO -> 2.0;
        };
    }
}
