package displays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class HeadUtils {

    public static ItemStack getPlayerHead(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if(player != null){

            meta.setOwningPlayer(player);

        } else if (offlinePlayer.isOnline() || offlinePlayer.hasPlayedBefore()) {

            meta.setOwningPlayer(offlinePlayer);

        }

        skull.setItemMeta(meta);

        return skull;
    }
}
