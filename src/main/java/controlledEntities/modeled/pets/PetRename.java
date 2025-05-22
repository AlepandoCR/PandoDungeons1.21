package controlledEntities.modeled.pets;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static controlledEntities.modeled.pets.PetFactory.savePetName;
import static controlledEntities.modeled.pets.PetSelectionMenu.openMenu;

public class PetRename {
    // Mapeo para saber qué jugador está renombrando qué mascota
    private static final Map<Player, String> renamingPet = new HashMap<>();

    public static void openRenameMenu(Player player, String currentPetName, String petName) {
        ItemStack renameItem = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = renameItem.getItemMeta();
        meta.setItemName(currentPetName);
        meta.setLore(List.of(ChatColor.AQUA + "Nombre actual"));
        renameItem.setItemMeta(meta);

        InventoryView view = player.openAnvil(player.getLocation(),true);

        view.setTitle(petName);

        AnvilInventory anvil = (AnvilInventory) view.getTopInventory();
        anvil.addItem(renameItem);
        renamingPet.put(player, petName);
    }

    public static void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!(event.getView().getPlayer() instanceof Player player)) return;
        if (!renamingPet.containsKey(player)) return;
        if(event.getResult() == null) return;
        ItemMeta meta = event.getResult().getItemMeta();
        meta.setLore(List.of(ChatColor.GREEN + "Click para confirmar"));
        event.getResult().setItemMeta(meta);
        event.setResult(event.getResult());
    }

    public static void onAnvilClick(InventoryClickEvent event, PandoDungeons plugin) {
        if (!(event.getInventory() instanceof AnvilInventory anvil)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!renamingPet.containsKey(player)) return;
        if (event.getRawSlot() != 2) return; // slot de resultado

        String petName = renamingPet.get(player);

        event.setCancelled(true);

        String newName = anvil.getResult().getItemMeta().getDisplayName();

        savePetName(PetType.fromString(petName),newName,player,plugin);

        renamingPet.remove(player);

        player.sendMessage(ChatColor.GREEN + "Tu mascota " + petName + " ahora se llama: " + ChatColor.RESET + newName);

        anvil.setItem(0, null); // Elimina la name tag
        anvil.setItem(2, null); // Elimina el resultado, por si acaso

        player.closeInventory();
    }

    public static void onAnvilClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!renamingPet.containsKey(player)) return;

        if (event.getInventory() instanceof AnvilInventory anvil) {
            // Remueve el item de entrada (slot 0) y el resultado (slot 2)
            anvil.setItem(0, null); // Elimina la name tag
            anvil.setItem(2, null); // Elimina el resultado, por si acaso
        }

        renamingPet.remove(player);

        openMenu(player);
    }

}
