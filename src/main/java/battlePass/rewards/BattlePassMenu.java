package battlePass.rewards;

import battlePass.BattlePass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class BattlePassMenu {
    private BattlePass battlePass;
    private int currentPage;

    public BattlePassMenu(BattlePass battlePass) {
        this.battlePass = battlePass;
        this.currentPage = 0;
    }

    public Inventory getInventory() {
        // Usamos currentPage para definir el título
        Inventory inventory = Bukkit.createInventory(null, 9, "Battle Pass - Página " + (currentPage + 1));
        int daysInMonth = 31; // Suponiendo que el mes tiene 31 días
        int totalDays = daysInMonth; // Se muestran 31 días
        int totalPages = (int) Math.ceil(totalDays / 7.0);

        int startDay = currentPage * 7 + 1;
        // Recorremos hasta 7 días o hasta agotar los días
        for (int i = 0; i < 7; i++) {
            int day = startDay + i;
            if (day > totalDays) {
                break;
            }

            Reward reward = battlePass.getRewardForDay(day);
            ItemStack item;
            if (reward != null) {
                item = reward.getItem().clone();
            } else {
                // Si no hay recompensa para el día, se crea un item vacío
                item = new ItemStack(Material.BARRIER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("Día " + day + " - Sin recompensa");
                item.setItemMeta(meta);
            }

            ItemMeta meta = item.getItemMeta();
            meta.setLore(Arrays.asList("Día " + day));
            item.setItemMeta(meta);
            inventory.setItem(i + 1, item);
        }

        if (currentPage > 0) {
            inventory.setItem(0, createNavigationItem("Anterior"));
        }
        if (currentPage < totalPages - 1) { // Mostrar "Siguiente" solo si no es la última página
            inventory.setItem(8, createNavigationItem("Siguiente"));
        }
        return inventory;
    }

    private ItemStack createNavigationItem(String name) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public void handleMenuClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        String itemName = clickedItem.getItemMeta().getDisplayName();

        // Extraer la página actual del título del inventario
        Inventory inventory = event.getView().getTopInventory();
        String title = event.getView().getTitle();
        int extractedPage = 0;
        try {
            // Se espera un título del tipo "Battle Pass - Página X"
            String[] parts = title.split("Página ");
            if (parts.length > 1) {
                extractedPage = Integer.parseInt(parts[1].trim()) - 1;
            }
        } catch (Exception e) {
            extractedPage = this.currentPage;
        }

        int daysInMonth = 31;
        int totalPages = (int) Math.ceil(daysInMonth / 7.0);

        if (itemName.equals("Anterior")) {
            if (extractedPage > 0) {
                extractedPage--;
            }
        } else if (itemName.equals("Siguiente")) {
            if (extractedPage < totalPages - 1) {
                extractedPage++;
            }
        }

        // Actualizamos la variable interna y reabrimos el inventario
        this.currentPage = extractedPage;
        event.getWhoClicked().openInventory(getInventory());
    }
}
