package controlledEntities.modeled.pets;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import pandoClass.quests.Mission;
import pandoClass.quests.questTypes.KillQuest;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;

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

}
