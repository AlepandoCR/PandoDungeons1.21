package pandoClass.quests;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.checkerframework.checker.units.qual.K;
import pandoClass.quests.Mission;
import pandoClass.quests.questTypes.KillQuest;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class MissionListener implements Listener {

    PandoDungeons plugin;
    // Constructor que recibe la lista de misiones
    public MissionListener(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    // Este es el método que será llamado cuando se dispare el evento EntityDamageByEntityEvent
    @EventHandler
    public void onEntityDamage(EntityDeathEvent event) {
        // Iterar sobre una copia de la lista de misiones para evitar ConcurrentModificationException
        for (Mission<?> mission : new ArrayList<>(plugin.missionManager.getMissions())) {
            if (mission != null) {
                if (mission instanceof KillQuest) {
                    ((KillQuest) mission).listener(event);
                }
            }
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        // Verificamos si ya existe una misión para este jugador en la sesión.
        for (Mission<?> mission : plugin.missionManager.getMissions()){
            Player missionPlayer = mission.getPlayer();
            if(missionPlayer != null && missionPlayer.equals(player)){
                // Envia el mensaje de misión y el progreso actual.
                if(mission instanceof KillQuest) {
                    ((KillQuest) mission).sendMissionMessage();
                }
                return;

            }
        }
        // Si no hay una misión activa, se puede registrar una nueva.
        KillQuest quest = new KillQuest("matar mobs", player, plugin);
        plugin.missionManager.registerMission(quest);
    }

}
