package displays.tops;

import displays.DisplayData;
import displays.TopDisplay;
import displays.handlers.DungeonsTopHandler;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class DungeonsTopDisplay {

    public DungeonsTopDisplay(PandoDungeons plugin, Location location) {
        TopDisplay topDisplay = new TopDisplay(plugin, location, 5, dataDisplays(plugin));
        plugin.getDisplayManager().addDisplay(topDisplay);
    }

    public List<DisplayData> dataDisplays(PandoDungeons plugin){
        List<DisplayData> r = new ArrayList<>();
        DungeonsTopHandler.getTop5DungeonPlayersWithDisplayData().forEach(dungeonsTopEntry -> {
            r.add(new DisplayData(dungeonsTopEntry.textSupplier(), dungeonsTopEntry.uuid(), plugin));
        });
        return r;
    }
}
