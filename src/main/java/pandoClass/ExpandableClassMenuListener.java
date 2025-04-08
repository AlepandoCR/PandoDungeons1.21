package pandoClass;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pandodungeons.pandodungeons.PandoDungeons;

import java.net.MalformedURLException;

public class ExpandableClassMenuListener implements Listener {

    private final PandoDungeons plugin;

    public ExpandableClassMenuListener(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClassMenuClick(InventoryClickEvent event) throws MalformedURLException {
        // Verifica si el inventario es el menú de clases ocultas
        Inventory inventory = event.getInventory();
        if (inventory == null || !event.getView().getTitle().contains("Cambiar clase")) {
            return;
        }

        event.setCancelled(true); // Cancela cualquier interacción con el menú

        // Verifica si el clic provino de un jugador
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        // Obtiene la clase seleccionada
        RPGPlayer rpgPlayer = plugin.rpgManager.getPlayer(player);
        ClassRPG selectedClass = getClassByItem(clickedItem, rpgPlayer);

        if (selectedClass == null) {
            return;
        }

        // Verifica si el jugador ya tiene esta clase
        if (rpgPlayer.getClassKey().equalsIgnoreCase(selectedClass.getKey())) {
            player.sendMessage(ChatColor.RED + "¡Ya tienes esta clase equipada!");
            return;
        }

        // Verifica si el jugador tiene suficiente dinero
        int cost = 500;
        if (rpgPlayer.getCoins() < cost) {
            player.sendMessage(ChatColor.RED + "No tienes suficientes monedas para cambiar de clase.");
            return;
        }

        handleClassChange(rpgPlayer,player,selectedClass.getKey());

        player.sendMessage(ChatColor.GREEN + "Has cambiado tu clase a " + ChatColor.GOLD + selectedClass.getName());
    }

    private ClassRPG getClassByItem(ItemStack item, RPGPlayer rpgPlayer) {
        for (ClassRPG classRPG : new ExpandableClassMenu(rpgPlayer.getPlayer(),plugin).getAvailableClasses()) {
            if (item.getItemMeta().getDisplayName().contains(classRPG.getName())) {
                return classRPG;
            }
        }
        return null;
    }

    private void handleClassChange(RPGPlayer rpgPlayer, Player player, String classKey) throws MalformedURLException {
        String currentKey = rpgPlayer.getClassKey();
        if(currentKey == null){
            rpgPlayer.setClassKey(classKey);
            player.closeInventory();
        }else{
            if (currentKey.isEmpty()) {
                rpgPlayer.setClassKey(classKey);
                player.closeInventory();
            } else if (!currentKey.equalsIgnoreCase(classKey)){
                if (rpgPlayer.getCoins() >= 500) {
                    rpgPlayer.removeCoins(500);
                    rpgPlayer.setClassKey(classKey);
                    player.openInventory(new ExpandableClassMenu(player,plugin).createExpandableClassMenu());
                } else {
                    player.sendMessage("No tienes suficientes monedas para cambiar tu clase");
                }
            } else{
                player.sendMessage("Ya haz seleccionado esa clase");
            }
        }
    }




}
