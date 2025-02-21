package pandoQuests.quests;

import org.bukkit.Location;

public class Mission {
    private final String missionName;
    private final Location targetLocation;


    /**
     * Constructor para crear una misión.
     *
     * @param missionName    Nombre de la misión.
     * @param targetLocation Ubicación objetivo de la misión.
     */
    public Mission(String missionName, Location targetLocation) {
        this.missionName = missionName;
        this.targetLocation = targetLocation;
    }

    public String getMissionName() {
        return missionName;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }
}
