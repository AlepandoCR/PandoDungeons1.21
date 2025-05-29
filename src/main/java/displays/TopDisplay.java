package displays;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.PandoDungeons;

import java.util.*;

public class TopDisplay {

    private final int count;
    private final List<DisplayData> dataList;
    private final Location baseLocation;
    private float scale = 1.0f;
    private final PandoDungeons plugin;
    private boolean update;
    private final NamespacedKey TOP_DISPLAY_TAG;

    public TopDisplay(PandoDungeons plugin, Location location, int count, List<DisplayData> dataList) {
        this.plugin = plugin;
        this.count = count;
        this.baseLocation = location.clone();
        this.dataList = dataList;
        this.update = true;
        this.TOP_DISPLAY_TAG = new NamespacedKey(plugin, "top_display");
        generateDisplays();
        startUpdater();
    }

    public void setScale(float scale) {
        this.scale = scale;
        updateDisplayScales();
    }

    private void updateDisplayScales() {
        for (DisplayData data : dataList) {
            data.setScale(scale);
        }
    }

    public void generateDisplays() {
        for (int i = 0; i < Math.min(count, dataList.size()); i++) {
            DisplayData data = dataList.get(i);
            data.generateDisplays(baseLocation, scale, i);
        }
    }

    public void startUpdater() {
        new BukkitRunnable() {

            int cycles = 0;

            @Override
            public void run() {
                if(!update){
                    cancel();
                    return;
                }
                respawnAllDisplays();

                updateHeads();

                updateData();

                cycles++;
            }

            private void updateData() {
                for (DisplayData data : dataList) {
                    data.update();
                }
            }

            private void updateHeads() {
                if(cycles % 2 == 0){
                    for (DisplayData data : dataList) {
                        data.updateHead();
                    }

                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 5);
    }

    public void remove(){
        update = false;
        for (DisplayData displayData : dataList) {
            displayData.remove();
        }
    }

    public void respawnAllDisplays() {
        removeAllTaggedDisplays();
        generateDisplays();
    }

    private void removeAllTaggedDisplays() {
        baseLocation.getWorld().getEntities().stream()
                .filter(entity -> entity.getPersistentDataContainer().has(TOP_DISPLAY_TAG, PersistentDataType.INTEGER))
                .forEach(Entity::remove);
    }




}
