package pandoToros.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerArmorChecker {

    /**
     * Verifica si el jugador tiene alguna o todas las piezas de armadura equipadas.
     *
     * @param player El jugador a verificar.
     * @param checkAll Si es true, verifica si tiene todas las piezas de armadura equipadas.
     *                 Si es false, verifica si tiene al menos una pieza de armadura equipada.
     * @return true si cumple la condición, false en caso contrario.
     */
    public static boolean hasArmor(Player player, boolean checkAll) {
        ItemStack[] armorContents = player.getInventory().getArmorContents();

        if (checkAll) {
            // Verificar si todas las piezas están equipadas
            for (ItemStack armor : armorContents) {
                if (armor == null) {
                    return false;
                }
            }
            return true;
        } else {
            // Verificar si al menos una pieza está equipada
            for (ItemStack armor : armorContents) {
                if (armor != null) {
                    return true;
                }
            }
            return false;
        }
    }
}
