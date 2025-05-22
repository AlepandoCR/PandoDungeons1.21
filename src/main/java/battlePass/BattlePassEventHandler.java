package battlePass;

import battlePass.rewards.BattlePassMenu;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import pandodungeons.PandoDungeons;

public class BattlePassEventHandler implements Listener {
    private final PandoDungeons plugin;

    public BattlePassEventHandler(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("Battle Pass")) {
            event.setCancelled(true);
            BattlePassMenu battlePassMenu = new BattlePassMenu(
                    BattlePass.getPlayerBattlePass(
                            (Player) event.getWhoClicked()
                            ,plugin
                    )
            );
            battlePassMenu.handleMenuClick(event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        Entity entity = event.getEntity();

        World world = Bukkit.getWorld("spawn");
        if (world == null) {
            player.sendMessage("§cMundo 'spawn' no encontrado.");
            return;
        }

        if (!entity.getWorld().equals(world)) return;
        if (!(entity instanceof ArmorStand armorStand)) return;

        if (!armorStand.getScoreboardTags().contains("battlePass")) return;

        event.setCancelled(true); // Evita que la entidad reciba daño

        player.sendMessage("§aAbriendo BattlePass...");

        BattlePassMenu battlePassMenu = new BattlePassMenu(
                BattlePass.getPlayerBattlePass(player, plugin)
        );

        player.openInventory(battlePassMenu.getInventory());
    }


}
