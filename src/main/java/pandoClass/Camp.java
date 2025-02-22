package pandoClass;

import io.papermc.paper.datacomponent.item.BundleContents;
import net.minecraft.world.item.BundleItem;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.BundleMeta;
import org.eclipse.sisu.space.BundleClassSpace;

import java.util.ArrayList;
import java.util.List;

public class Camp {
    int lvl;
    List<Entity> entities = new ArrayList<>();
}
