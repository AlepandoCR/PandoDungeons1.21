package pandodungeons.pandodungeons.Elements;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.event.entity.PufferFishStateChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import pandodungeons.pandodungeons.Utils.CompanionUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CompanionMenu {
    private final Player player;
    private final String companionType;
    private Inventory companionMenu;

    public CompanionMenu(Player player, String companionType){
        this.player = player;
        this.companionType = companionType;
        companionMenu = Bukkit.createInventory(player, 9 * 3, ChatColor.BOLD + companionType + " Menu");
    }

    public Inventory menu() throws MalformedURLException {

        ItemStack levelUpHead = createLevelUpHead();
        ItemStack backHead = createBackHead();
        ItemStack statsHead = createStatsHead();
        ItemStack selectHead = createSelectHead();

        // Posiciones centrales del inventario (asumiendo que el inventario es de 3 filas)
        companionMenu.setItem(11, levelUpHead);
        companionMenu.setItem(13, selectHead);
        companionMenu.setItem(15, statsHead);
        companionMenu.setItem(18, backHead);

        return companionMenu;
    }
    private ItemStack createHead(String displayName, String textureUrl) throws MalformedURLException {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // Crear perfil y texturas del jugador
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(new URL("https://textures.minecraft.net/texture/" + textureUrl)); // Establecer la textura de la piel del companion
        profile.setTextures(textures);

        meta.setPlayerProfile(profile);
        meta.setCustomModelData(420);
        meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.WHITE + ChatColor.BOLD + displayName); // Nombre del item
        head.setItemMeta(meta);
        return head;
    }

    private ItemStack createLevelUpHead() throws MalformedURLException {
        int level = CompanionUtils.getCompanionLevel(player, companionType.toLowerCase(Locale.ROOT));

        int costo = (int) (level * 0.5) + 1;

        ItemStack head = createHead(ChatColor.GREEN.toString() + ChatColor.BOLD + "Subir de nivel", "b221da4418bd3bfb42eb64d2ab429c61decb8f4bf7d4cfb77a162be3dcb0b927");
        SkullMeta meta = (SkullMeta) head.getItemMeta();


        // Agregar el lore personalizado
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Nivel actual: " + ChatColor.GOLD + level);
        lore.add(ChatColor.YELLOW + "Costo para mejorar: " + ChatColor.GOLD + ChatColor.BOLD + costo + " ✦");
        meta.setLore(lore);

        head.setItemMeta(meta);
        return head;
    }


    private ItemStack createBackHead() throws MalformedURLException {
        return createHead((ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Atrás"), "c49d271c5df84f8a3c8aa5d15427f62839341dab52c619a5987d38fbe18e464");
    }

    private ItemStack createSelectHead() throws MalformedURLException {
        return createHead((ChatColor.GREEN.toString() + ChatColor.BOLD + "Seleccionar"), "a92e31ffb59c90ab08fc9dc1fe26802035a3a47c42fee63423bcdb4262ecb9b6");
    }

    private ItemStack createStatsHead() throws MalformedURLException {
        ItemStack head = createHead((ChatColor.GOLD.toString() + ChatColor.BOLD + "Información"), "8a16038bc8e6518afa91498dab7675c01cb31a125d21c49b861294d39e1c560c");
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        List<String> lore = new ArrayList<>();
        if(companionType.toLowerCase(Locale.ROOT).equals("allay")){
            lore.add(ChatColor.BOLD + "Te devuelve cierto % del loot de los mobs");
        } else if (companionType.toLowerCase(Locale.ROOT).equalsIgnoreCase("breeze")) {
            lore.add(ChatColor.BOLD + "Te da velocidad y empuja a los mobs");
        } else if(companionType.toLowerCase(Locale.ROOT).equalsIgnoreCase("armadillo")){
            lore.add(ChatColor.BOLD + "Te da resistencia y ataca a los mobs");
        } else if(companionType.toLowerCase(Locale.ROOT).equalsIgnoreCase("oso")){
        lore.add(ChatColor.BOLD + "Congela a los mobs cercanos");
        } else if(companionType.toLowerCase(Locale.ROOT).equalsIgnoreCase("sniffer")){
            lore.add(ChatColor.BOLD + "Lanza proyectiles de fuego");
    }

        lore.add(ChatColor.RESET.toString() + ChatColor.RED + "Todos los efectos aumentan con el nivel");
        meta.setLore(lore);
        head.setItemMeta(meta);
        return head;
    }

}
