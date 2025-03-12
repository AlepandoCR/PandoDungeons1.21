package pandoClass;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import pandoClass.classes.archer.Archer;
import pandoClass.classes.assasin.Assasin;
import pandoClass.classes.farmer.Farmer;
import pandoClass.classes.tank.Tank;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExpandableClassMenu {
    public final String CLASS_MENU_TITLE;

    // Lista de clases disponibles en este menú
    private final List<ClassRPG> availableClasses = new ArrayList<>();

    private final RPGPlayer rpgPlayer;

    public ExpandableClassMenu(Player player){
        this.rpgPlayer = new RPGPlayer(player);
        CLASS_MENU_TITLE = ChatColor.DARK_GRAY +  "Cambiar clase          " + ChatColor.WHITE + "☃ " + ChatColor.RESET +  rpgPlayer.getCoins();
        setClasses();
    }

    public List<ClassRPG> getAvailableClasses() {
        return availableClasses;
    }

    private void setClasses(){
        availableClasses.add(new Farmer(rpgPlayer)); // Solo Granjero por ahora
        availableClasses.add(new Archer(rpgPlayer));
        availableClasses.add(new Tank(rpgPlayer));
        availableClasses.add(new Assasin(rpgPlayer));
    }

    public Inventory createExpandableClassMenu() throws MalformedURLException {
        // Calcula el tamaño del inventario basado en la cantidad de clases (mínimo 9, máximo 54)
        int size = Math.min(54, Math.max(9, (int) Math.ceil(availableClasses.size() / 9.0) * 9));
        Inventory menu = Bukkit.createInventory(null, size, CLASS_MENU_TITLE);

        // Genera los botones para cada clase
        for (int i = 0; i < availableClasses.size(); i++) {
            ClassRPG classRPG = availableClasses.get(i);
            menu.setItem(i, createClassHead(rpgPlayer, classRPG));
        }

        return menu;
    }

    private ItemStack createClassHead(RPGPlayer player, ClassRPG classRPG) throws MalformedURLException {
        String textureUrl = getTextureForClass(classRPG);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // Asigna el perfil con la textura correspondiente
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(new URL("https://textures.minecraft.net/texture/" + textureUrl));
        profile.setTextures(textures);
        meta.setPlayerProfile(profile);

        meta.setDisplayName(ChatColor.GOLD + ChatColor.BOLD.toString() + classRPG.getName());
        meta.setLore(getClassLore(player, classRPG));

        head.setItemMeta(meta);
        return head;
    }

    private List<String> getClassLore(RPGPlayer player, ClassRPG classRPG) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Habilidades:");
        lore.add(ChatColor.AQUA + "1 - " + classRPG.firstSkill.getDescription());
        lore.add(ChatColor.AQUA + "2 - " + classRPG.secondSkill.getDescription());
        lore.add(ChatColor.AQUA + "3 - " + classRPG.thirdSkill.getDescription());

        if (!player.getClassKey().equalsIgnoreCase(classRPG.key)) {
            lore.add(ChatColor.RED + "Costo: " + ChatColor.YELLOW + "500 monedas");
        }
        return lore;
    }

    private String getTextureForClass(ClassRPG classRPG) {
        return switch (classRPG.getKey()) {
            case "FarmerClass" -> createFarmerHead(); // Textura del Granjero
            case "TankClass"  -> createTankHead();
            case "AssassinClass" -> createAssassinHead();
            case "ArcherClass" -> createArcherHead();
            default -> "default_texture_url"; // Cambia esto si agregas más clases
        };
    }

    private String  createFarmerHead() {
        int lvl = rpgPlayer.getLevel();
        String url;

        if(lvl < 25){
            url = "680727d390c3b34d809a4d5908c58b5ed6a891b8bdd0bca16065cd55f0d8de0c";
        }else if(lvl < 50){
            url = "7fe7b1d16e3e50b0d5f9739f1e93907c46eb21e3b953a9f11cae824949354a9a";
        }else if(lvl < 75){
            url = "8dd43768ab9a675aafea5539417a4d495c92bb2c8be27ea4e31ff657bcffbae5";
        }else{
            url = "7b43b23189dcf1326da4253d1d7582ef5ad29f6c27b171feb17e31d084e3a7d";
        }

        return url;
    }

    private String createArcherHead() {
        int lvl = rpgPlayer.getLevel();
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

        return url;
    }

    private String createAssassinHead() {
        int lvl = rpgPlayer.getLevel();
        String url;
        if(lvl < 25){
            url = "4b8d04e6463c5a1a99e07916e01fe722dab257b124836ce603b46bc90ee349c8";
        }else if(lvl < 50){
            url = "45e59692fcd14c0364a8cbd4cf5cb82eb421a43009f6184123745f8bfd5d9833";
        }else if(lvl < 75){
            url = "9300bb58ef1ab6cc9c0cf5e3d60e2febbe6c431e3d6bf3c6db3cf824791ff914";
        }else{
            url = "2638583cf2c761fac3f83982589ac26ee5771a183863b47a2490e4cb506ad26";
        }
        return url;
    }

    private String createTankHead() {
        int lvl = rpgPlayer.getLevel();

        String url;

        if(lvl < 25){
            url = "69848f6db5ed185630e044e478b81deadc63d6719c195b9a563f745446f61daf";
        }else if(lvl < 50){
            url = "25de4ff8be70eee4d103b1eedf454f0abb9f0568f5f326ecba7cab6a47f9ade4";
        }else if(lvl < 75){
            url = "2a018d5109c002565ecf8841fd9d1ebcdc4b371752188b7a3ed6ea925bd2fb98";
        }else{
            url = "6d402a1f9629b124c265273c1fd6aa2210fe204fb0d3416012c615aca4760b5d";
        }

        return url;
    }
}
