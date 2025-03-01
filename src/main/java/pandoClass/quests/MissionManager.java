package pandoClass.quests;

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

}
