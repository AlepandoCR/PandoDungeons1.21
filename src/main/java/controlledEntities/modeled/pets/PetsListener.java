package controlledEntities.modeled.pets;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import pandoClass.gachaPon.Gachapon;
import pandoClass.quests.Mission;
import pandoClass.quests.questTypes.KillQuest;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import static com.fastasyncworldedit.core.util.Permission.hasPermission;
import static controlledEntities.modeled.pets.PetGachapon.activePetGachapon;
import static controlledEntities.modeled.pets.PetSelectionMenu.petMenuListener;
import static controlledEntities.modeled.pets.PetSelectionMenu.petPermissions;
import static pandoClass.gachaPon.Gachapon.activeGachapon;

public class PetsListener implements Listener {

    PandoDungeons plugin;
    // Constructor que recibe la lista de misiones
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
