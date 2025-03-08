package pandoClass.quests;

import org.bukkit.entity.Player;
import pandoClass.quests.questTypes.KillQuest;

import java.util.ArrayList;
import java.util.List;

public class MissionManager {

    private final List<Mission<?>> missions = new ArrayList<>();

    // Registrar una misión en el manager
    public <T> void registerMission(Mission<T> mission) {
        missions.add(mission);
    }

    public List<Mission<?>> getMissions(){
        return missions;
    }

    public void removeMission(Mission<?> mission){
        missions.remove(mission);
    }

    public Mission<?> getMission(Player player) {
        for (Mission<?> mission : missions) {
            Player missionPlayer = mission.getPlayer();
            if (missionPlayer != null && missionPlayer.equals(player)) {
                return mission;
            }
        }
        return null;
    }

}
