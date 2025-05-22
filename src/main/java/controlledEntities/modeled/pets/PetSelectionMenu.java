package controlledEntities.modeled.pets;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static controlledEntities.modeled.pets.PetFactory.*;
import static controlledEntities.modeled.pets.PetRename.openRenameMenu;

public class PetSelectionMenu {

    public static final Map<String, String> petTextures = new HashMap<>(); // Nombre de la mascota -> Textura
    public static final Map<String, String> petPermissions = new HashMap<>(); // Nombre de la mascota -> Permiso requerido

    public PetSelectionMenu() {
    }

    static {
        registerPetInMenu("Mapache", "37bd204512728130b7f65ff2714b98d48735ff0288b6e9a50270002290fa58d7", "mascota.mapache");
        registerPetInMenu("Minero", "7d915e395587c5cd4a7e6416195575f5bfdb6c476398fe8e3a87e3c7fbb894eb", "mascota.minero");
        registerPetInMenu("Sakura", "db61521259c6c3402f9cae3b6867ddb481fd7a834674f26bf4fcf11c978e0051", "mascota.sakura");
        registerPetInMenu("Jojo", "a60e246bdfd263e545a90913e6f00e9b4156f1a8fa8711b439c124f30f3945c4", "mascota.jojo");
    }

    private static void registerPetInMenu(String name, String display, String permission) {
        petTextures.put(name, display);
        petPermissions.put(name, permission);
    }

    public static void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player, 27, "Selecciona tu Mascota");

        for (Map.Entry<String, String> entry : petTextures.entrySet()) {
            String petName = entry.getKey();
            String texture = entry.getValue();
            String permission = petPermissions.get(petName);

            boolean unlocked = hasPermission(player, permission);
            ItemStack head = createHead(texture, petName, unlocked, player.hasPermission("mascota.nombres"));

            menu.addItem(head);
        }

        player.openInventory(menu);
    }

    private static boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }

    private static ItemStack createHead(String textureUrl, String petName, boolean unlocked, boolean nameEdit) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        try {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();
            String finalUrl = unlocked ? textureUrl : "da99b05b9a1db4d29b5e673d77ae54a77eab66818586035c8a2005aeb810602a";
            textures.setSkin(new URL("https://textures.minecraft.net/texture/" + finalUrl));
            profile.setTextures(textures);
            meta.setPlayerProfile(profile);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        meta.setDisplayName(unlocked ? "§a" + petName : "§c???");
        List<String> lore = new ArrayList<>();
        lore.add(unlocked ? "§7¡Haz clic para seleccionar!" : "§cMascota bloqueada");
        if(unlocked && nameEdit){
            lore.add(ChatColor.GRAY + "Click derecho para editar el nombre");
        }
        meta.setLore(lore);

        head.setItemMeta(meta);
        return head;
    }



    public static void petMenuListener(InventoryClickEvent event, PandoDungeons plugin) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();

        // Menú principal de mascotas
        if (title.equals("Selecciona tu Mascota")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            String petName = clicked.getItemMeta().getDisplayName()
                    .replace("§a", "").replace("§c???", "");

            if (petPermissions.containsKey(petName) && player.hasPermission(petPermissions.get(petName))) {
                if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                    // Abrir el anvil para renombrar
                    if(player.hasPermission("mascota.nombres")){
                        openRenameMenu(player, getPetName(PetType.fromString(petName),player,plugin),petName);
                    }
                    return;
                }

                player.sendMessage("§aHas seleccionado la mascota: " + petName);
                plugin.petsManager.destroyPets(player);
                createPet(PetType.fromString(petName), player, plugin);
            } else {
                player.sendMessage("§cNo tienes esta mascota desbloqueada.");
            }
        }
    }


}
