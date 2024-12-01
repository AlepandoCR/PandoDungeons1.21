package pandoToros.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import static pandoToros.game.ArenaMaker.isRedondelWorld;


public class ToroGameListener implements Listener {

    @EventHandler
    private void buildBarricades(BlockPlaceEvent event){
        Player player = event.getPlayer();

        if(isRedondelWorld(player.getWorld().getName()) && !player.isOp()) {
            if (!event.getBlock().getType().equals(Material.BAMBOO_BLOCK) || event.getBlock().getLocation().getY() > -1) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void breakBlocks(BlockBreakEvent event){
        Player player = event.getPlayer();

        if(isRedondelWorld(player.getWorld().getName()) && !player.isOp()) {
            event.setCancelled(true);
        }
    }

}
