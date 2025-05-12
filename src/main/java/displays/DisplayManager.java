package displays;

import displays.handlers.RPGTopProvider;
import displays.tops.CoinsTopDisplay;
import displays.tops.DungeonsTopDisplay;
import displays.tops.RpgTopDisplay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class DisplayManager {

    private final Location TOP_RPG_LOCATION;
    private final Location TOP_COINS_LOCATION;
    private final Location TOP_DUNGEON_LOCATION;

    private final List<TopDisplay> displays = new ArrayList<>();

    private final PandoDungeons plugin;

    public DisplayManager(PandoDungeons plugin) {
        this.plugin = plugin;

        this.TOP_RPG_LOCATION = new Location(Bukkit.getWorld("spawn"),280.5, 87.00, 466.5);
        this.TOP_DUNGEON_LOCATION = new Location(Bukkit.getWorld("spawn"),297.5, 90.00, 474.5);
        this.TOP_COINS_LOCATION = new Location(Bukkit.getWorld("spawn"),29.5, 80.00, 255.5);

        startDisplays();
    }

    public void startDisplays(){
        Bukkit.getScheduler().runTaskLater(plugin,r -> {
            new DungeonsTopDisplay(plugin,TOP_DUNGEON_LOCATION);
            new RpgTopDisplay(plugin,TOP_RPG_LOCATION);
            new CoinsTopDisplay(plugin,TOP_COINS_LOCATION);
        },80L);
    }

    public PandoDungeons getPlugin() {
        return plugin;
    }

    public List<TopDisplay> getDisplays() {
        return displays;
    }

    public void addDisplay(TopDisplay topDisplay){
        displays.add(topDisplay);
    }

    public void removeDisplay(TopDisplay topDisplay){
        displays.remove(topDisplay);
        topDisplay.remove();
    }

    public void removeAllDisplays(){
        for (TopDisplay topDisplay : displays) {
            topDisplay.remove();
        }
    }

}
