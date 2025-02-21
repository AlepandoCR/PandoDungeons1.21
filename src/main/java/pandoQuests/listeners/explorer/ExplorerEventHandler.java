import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pandoQuests.npc.human.variations.explorer.HorseExplorer;

public class ExplorerEventHandler implements Listener {

    private final JavaPlugin plugin;

    public ExplorerEventHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (!(event.getNPC().getEntity() instanceof HorseExplorer explorer)) return;

        explorer.handleRightClick(event.getClicker());
    }
}
