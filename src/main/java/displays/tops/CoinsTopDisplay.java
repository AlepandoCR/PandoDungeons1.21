package displays.tops;

import displays.DisplayData;
import displays.TopDisplay;
import displays.handlers.CoinsTopProvider;
import org.bukkit.Location;
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class CoinsTopDisplay {

    private final PandoDungeons plugin;

    public CoinsTopDisplay(PandoDungeons plugin, Location location) {
        this.plugin = plugin;
        TopDisplay topDisplay = new TopDisplay(plugin, location, 5, dataDisplays());
        plugin.getDisplayManager().addDisplay(topDisplay);
    }

    public List<DisplayData> dataDisplays(){
        return new ArrayList<>(CoinsTopProvider.getTopCoinPlayers(plugin, 5));
    }
}
