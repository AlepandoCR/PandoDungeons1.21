package pandoClass;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import pandoClass.classes.archer.Archer;
import pandoClass.classes.assasin.Assasin;
import pandoClass.classes.tank.Tank;
import pandodungeons.pandodungeons.PandoDungeons;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InitMenu {
    public static final String INNIT_MENU_NAME = ChatColor.RED.toString() + ChatColor.BOLD + " Elección única" + ChatColor.DARK_GRAY + " de clase" ;

    private final PandoDungeons plugin;

    public InitMenu(PandoDungeons plugin){

        this.plugin = plugin;
    }

    public enum Reason {
        INNIT,
        SKILL_MENU,
        SHOP
    }

    // Método que crea el menú
    public Inventory createClassSelectionMenu(Player player, Reason reason) throws MalformedURLException {
        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);
        String menuTitle;
        menuTitle = switch (reason){
            case INNIT -> INNIT_MENU_NAME;
            case SKILL_MENU -> "Menu Skills                 " + ChatColor.DARK_GRAY + " " + ChatColor.BOLD + rpgPlayer.getOrbs() + ChatColor.WHITE +  "\uD83D\uDC80" ;
            case SHOP -> ChatColor.DARK_GRAY +  "Cambiar clase          " + ChatColor.WHITE + "☃ " + ChatColor.RESET +  rpgPlayer.getCoins();
        };

        Inventory menu = Bukkit.createInventory(null, 9, menuTitle);

        ItemStack head1 = null;
        ItemStack head2 = null;
        ItemStack head3 = null;

        switch (reason){
            case SHOP:
            case INNIT:
                head1 = createArcherHead(rpgPlayer);
                head2 = createTankHead(rpgPlayer);
                head3 = createAssassinHead(rpgPlayer);
                break;
            case SKILL_MENU:

                ClassRPG classRPG = rpgPlayer.getClassRpg();
                if(classRPG != null){
                    Skill first = classRPG.firstSkill;
                    Skill second = classRPG.secondSkill;
                    Skill third = classRPG.thirdSkill;

                    head1 = createHeadForSkill(first.getName(), first, rpgPlayer);
                    head2 = createHeadForSkill(second.getName(), second, rpgPlayer);
                    head3 = createHeadForSkill(third.getName(), third, rpgPlayer);
                }
                break;
        }


        // Asignar las cabezas a las posiciones 2, 4 y 6
        menu.setItem(2, head1);
        menu.setItem(4, head2);
        menu.setItem(6, head3);

        return menu;
    }

    private ItemStack createHeadForSkill(String displayName, Skill skill, RPGPlayer player) throws MalformedURLException {
        String textureUrl = skill.getDisplayValue();
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = getSkullMetaForSkill(player, head, skill);

        // Crear perfil y texturas del jugador
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(new URL("https://textures.minecraft.net/texture/" + textureUrl)); // Establecer la textura de la piel del companion
        profile.setTextures(textures);

        meta.setPlayerProfile(profile);
        meta.setCustomModelData(420);
        meta.setDisplayName(displayName); // Nombre del item
        head.setItemMeta(meta);
        return head;
    }

    private ItemStack createHead(String displayName, String textureUrl, ClassRPG classRPG) throws MalformedURLException {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = getSkullMetaForInfo(classRPG,head);

        // Crear perfil y texturas del jugador
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(new URL("https://textures.minecraft.net/texture/" + textureUrl)); // Establecer la textura de la piel del companion
        profile.setTextures(textures);

        meta.setPlayerProfile(profile);
        int modelData = switch (classRPG.getKey()){
            case "ArcherClass" -> 111;
            case "TankClass" -> 222;
            case "AssassinClass" -> 333;
            default -> 0;
        };
        meta.setCustomModelData(modelData);
        meta.setDisplayName(displayName); // Nombre del item
        head.setItemMeta(meta);
        return head;
    }

    private ItemStack createAssassinHead(RPGPlayer player) throws MalformedURLException {
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

        ItemStack head = createHead(ChatColor.RED.toString() + ChatColor.BOLD + "Asesíno", url, new Assasin(player,plugin));

        return head;
    }



    private @NotNull SkullMeta getSkullMetaForInfo(@Nullable ClassRPG classRPG, ItemStack head) {
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        List<String> lore = new ArrayList<>();
        if(classRPG != null){
            RPGPlayer rpgPlayer = new RPGPlayer(classRPG.rpgPlayer.getPlayer(), plugin);
            lore.add("§6§lHabilidades"); // Título en dorado y negrita
            lore.add("§b§l1 - §f" + classRPG.firstSkill.getDescription());
            lore.add("§b§l2 - §f" + classRPG.secondSkill.getDescription());
            lore.add("§b§l3 - §f" + classRPG.thirdSkill.getDescription());


            if(rpgPlayer.getClassKey() != null){
                if(!rpgPlayer.getClassKey().isEmpty()){
                    if(!rpgPlayer.getClassKey().equalsIgnoreCase(classRPG.key)){
                        lore.add("§cCosto: §e500 monedas");
                    }
                }
            }
        }
        meta.setLore(lore);
        return meta;
    }

    private @NotNull SkullMeta getSkullMetaForSkill(RPGPlayer player, ItemStack head, Skill skill) {
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("§6§lDescripción: §f" + skill.getDescription());
        lore.add("§6§lNivel Actual: §f" + skill.getLvl());
        lore.add(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Costo de mejora: " + ChatColor.WHITE + "4" + " 💀 ");
        meta.setLore(lore);
        return meta;
    }


    private ItemStack createArcherHead(RPGPlayer player) throws MalformedURLException {
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

        ItemStack head = createHead(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Arquero", url, new Archer(player,plugin));
        return head;
    }


    private ItemStack createTankHead(RPGPlayer player) throws MalformedURLException {
        int lvl = player.getLevel();

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

        ItemStack head = createHead(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Tanque", url, new Tank(player,plugin));
        return head;
    }
}
