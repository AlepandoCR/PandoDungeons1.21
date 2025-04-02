package controlledEntities.modeled.pets;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static controlledEntities.modeled.pets.PetFactory.createPet;

public class PetSelectionMenu implements Listener {

    public static final Map<String, String> petTextures = new HashMap<>(); // Nombre de la mascota -> Textura
    public static final Map<String, String> petPermissions = new HashMap<>(); // Nombre de la mascota -> Permiso requerido

    public PetSelectionMenu() {
    }

    static  {
        registerPetInMenu("Mapache", "37bd204512728130b7f65ff2714b98d48735ff0288b6e9a50270002290fa58d7", "mascota.mapache");
        registerPetInMenu("Minero", "7d915e395587c5cd4a7e6416195575f5bfdb6c476398fe8e3a87e3c7fbb894eb", "mascota.minero");
    }

    private static void registerPetInMenu(String name, String display, String permission){
        petTextures.put(name, display);
        petPermissions.put(name, permission);
    }

    public void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player, 27, "Selecciona tu Mascota");

        for (Map.Entry<String, String> entry : petTextures.entrySet()) {
            String petName = entry.getKey();
            String texture = entry.getValue();
            String permission = petPermissions.get(petName);

            boolean unlocked = hasPermission(player, permission);
            ItemStack head = createHead(texture, petName, unlocked);

            menu.addItem(head);
        }

        player.openInventory(menu);
    }

    private boolean hasPermission(Player player, String permission) {
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            if (perm.getPermission().equalsIgnoreCase(permission) && perm.getValue()) {
                return true;
            }
        }
        return false;
    }

    private ItemStack createHead(String textureUrl, String petName, boolean unlocked) {
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
        meta.setLore(lore);

        head.setItemMeta(meta);
        return head;
    }

    public static void petMenuListener(InventoryClickEvent event, PandoDungeons plugin) {
        if (event.getView().getTitle().equals("Selecciona tu Mascota")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();

            if (clicked == null || !clicked.hasItemMeta()) return;

            String petName = clicked.getItemMeta().getDisplayName().replace("§a", "").replace("§c???", "");
            if (petPermissions.containsKey(petName) && player.hasPermission(petPermissions.get(petName))) {
                player.sendMessage("§aHas seleccionado la mascota: " + petName);

                plugin.petsManager.destroyPets(player);

                createPet(petName,player,plugin);

            } else {
                player.sendMessage("§cNo tienes esta mascota desbloqueada.");
            }
        }
    }
}
