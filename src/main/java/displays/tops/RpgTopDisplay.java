package displays.tops;

import displays.DisplayData;
import displays.TopDisplay;
import displays.handlers.DungeonsTopHandler;
import displays.handlers.RPGTopProvider;
import org.bukkit.Location;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class RpgTopDisplay {

    private final PandoDungeons plugin;

    public RpgTopDisplay(PandoDungeons plugin, Location location) {
        this.plugin = plugin;
        TopDisplay topDisplay = new TopDisplay(plugin, location, 5, dataDisplays());
        plugin.getDisplayManager().addDisplay(topDisplay);
    }

    public List<DisplayData> dataDisplays(){
        return new ArrayList<>(RPGTopProvider.getTopRPGPlayers(plugin, 5));
    }
}
