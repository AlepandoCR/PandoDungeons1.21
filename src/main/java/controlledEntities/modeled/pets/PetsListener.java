package controlledEntities.modeled.pets;

import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import pandodungeons.PandoDungeons;

import java.util.Set;

import static controlledEntities.modeled.pets.PetGachapon.activePetGachapon;
import static controlledEntities.modeled.pets.PetRename.*;
import static controlledEntities.modeled.pets.PetSelectionMenu.petMenuListener;

public class PetsListener implements Listener {

    PandoDungeons plugin;
    public PetsListener(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPetWorld(PlayerChangedWorldEvent e){
        plugin.petsManager.handlePlayerWorldChange(e);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        petMenuListener(event, plugin);
        onAnvilClick(event, plugin);
    }

    @EventHandler
    public void invClose(InventoryCloseEvent event){
        onAnvilClose(event);
    }

    @EventHandler
    public void prepareAnvil(PrepareAnvilEvent event){
        onPrepareAnvil(event);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();

        // Solo procesamos si el entity es un ArmorStand
        if (!(entity instanceof ArmorStand armorStand)) {
            return;
        }

        Set<String> tags = armorStand.getScoreboardTags();
        Player player = event.getPlayer();


        if(tags.contains("PetGachapon")){
            ItemStack stack = event.getPlayer().getInventory().getItem(event.getHand());
            int amount = event.getPlayer().getInventory().getItem(event.getHand()).getAmount();
            if(plugin.prizeManager.hasGatchaPetToken(stack)){
                PetGachapon gachapon = new PetGachapon(plugin, player);

                if(activePetGachapon != null){
                    player.sendMessage(ChatColor.RED + "Hay otro gachapon activo");
                    return;
                }

                gachapon.trigger();
                player.getInventory().getItem(event.getHand()).setAmount(amount - 1);
            }
            event.setCancelled(true);
        }
    }
}
