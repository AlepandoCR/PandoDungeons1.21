package pandoClass;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pandoClass.files.RPGPlayerDataManager.load;

public class InitMenu {

    private static final String MENU_NAME = ChatColor.DARK_GRAY + "Elección de clase" + "  " + ChatColor.BOLD + ChatColor.RED + "Solo podrás elegirlo una vez";

    // Método que crea el menú
    public static Inventory createClassSelectionMenu(Player player) throws MalformedURLException {
        Inventory menu = Bukkit.createInventory(null, 9,  MENU_NAME);

        RPGPlayer rpgPlayer = new RPGPlayer(player);

        // Crear las cabezas y signarles sus posiciones
        ItemStack head1 = createArcherHead(rpgPlayer);
        ItemStack head2 = createTankHead(rpgPlayer);
        ItemStack head3 = createAssassinHead(rpgPlayer);

        // Asignar las cabezas a las posiciones 2, 4 y 6
        menu.setItem(2, head1);
        menu.setItem(4, head2);
        menu.setItem(6, head3);

        return menu;
    }

    private static ItemStack createHead(String displayName, String textureUrl) throws MalformedURLException {
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

    private static ItemStack createAssassinHead(RPGPlayer player) throws MalformedURLException {
        int lvl = player.getLevel();
        String url;
        if(lvl < 25 || player.getClassKey() == null){
            url = "4b8d04e6463c5a1a99e07916e01fe722dab257b124836ce603b46bc90ee349c8";
        }else if(lvl < 50){
            url = "45e59692fcd14c0364a8cbd4cf5cb82eb421a43009f6184123745f8bfd5d9833";
        }else if(lvl < 75){
            url = "9300bb58ef1ab6cc9c0cf5e3d60e2febbe6c431e3d6bf3c6db3cf824791ff914";
        }else{
            url = "2638583cf2c761fac3f83982589ac26ee5771a183863b47a2490e4cb506ad26";
        }

        ItemStack head = createHead(ChatColor.RED.toString() + ChatColor.BOLD + "Asesíno", url);
        SkullMeta meta = (SkullMeta) head.getItemMeta();




        // Agregar el lore personalizado
        List<String> lore = new ArrayList<>();
        if(player.getClassKey() == null){

        }else{
            lore.add(ChatColor.GREEN + "Nivel actual: " + ChatColor.GOLD + lvl);
        }
        meta.setLore(lore);
        meta.setCustomModelData(333);
        head.setItemMeta(meta);
        return head;
    }

    private static ItemStack createArcherHead(RPGPlayer player) throws MalformedURLException {
        int lvl = player.getLevel();
        String url;

        if(lvl < 25){
            url = "543d59164ad6c139961b1d0790acca667b19afebcf9678c7ee86a6a927cff7fe";
        }else if(lvl < 50){
            url = "16a06c812c51255f44e7a8d946b30f70c57cc3531613793c00d43443298a09ca";
        }else if(lvl < 75){
            url = "bfa62e6642c717201be1c021d4c275b3bdbcccb64de4855bce01453d64b3c422";
        }else{
            url = "145eb4dcb633155fcb383006e2c626353cc680220074928e57bded2a1c955666";
        }

        ItemStack head = createHead(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Arquero", url);
        SkullMeta meta = (SkullMeta) head.getItemMeta();


        // Agregar el lore personalizado
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Nivel actual: " + ChatColor.GOLD + lvl);
        meta.setLore(lore);
        meta.setCustomModelData(111);
        head.setItemMeta(meta);
        return head;
    }

    private static ItemStack createTankHead(RPGPlayer player) throws MalformedURLException {
        int lvl = player.getLevel();

        String url;

        if(lvl < 25 || lvl == 0){
            url = "69848f6db5ed185630e044e478b81deadc63d6719c195b9a563f745446f61daf";
        }else if(lvl < 50){
            url = "25de4ff8be70eee4d103b1eedf454f0abb9f0568f5f326ecba7cab6a47f9ade4";
        }else if(lvl < 75){
            url = "2a018d5109c002565ecf8841fd9d1ebcdc4b371752188b7a3ed6ea925bd2fb98";
        }else{
         url = "6d402a1f9629b124c265273c1fd6aa2210fe204fb0d3416012c615aca4760b5d";
        }

        ItemStack head = createHead(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Tanque", url);
        SkullMeta meta = (SkullMeta) head.getItemMeta();


        // Agregar el lore personalizado
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Nivel actual: " + ChatColor.GOLD + lvl);
        meta.setLore(lore);
        meta.setCustomModelData(222);
        head.setItemMeta(meta);
        return head;
    }
}
