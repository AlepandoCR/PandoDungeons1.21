package displays;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.codehaus.plexus.util.Scanner;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.*;

public class TopDisplay {

    private final int count;
    private final List<DisplayData> dataList;
    private final Location baseLocation;
    private float scale = 1.0f;
    private final PandoDungeons plugin;
    private boolean update;

    public TopDisplay(PandoDungeons plugin, Location location, int count, List<DisplayData> dataList) {
        this.plugin = plugin;
        this.count = count;
        this.baseLocation = location.clone();
        this.dataList = dataList;
        this.update = true;
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

}
