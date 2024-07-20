package pandodungeons.pandodungeons.CustomEntities.CompanionLoops;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Companion {
    protected int level = 1;
    protected Player playerCompanion;
    protected LivingEntity livingEntity;
    protected JavaPlugin plugin;
    protected final double maxDistance = 10.0; // Distancia máxima permitida
    protected boolean keepLooping = true;

    private static final Map<UUID, Companion> activeCompanions = new HashMap<>();

    public Companion(Player player, int level) {
        this.level = level;
        this.playerCompanion = player;
        this.plugin = JavaPlugin.getPlugin(PandoDungeons.class);
        addCompanion(player);
    }

    private void addCompanion(Player player) {
        activeCompanions.put(player.getUniqueId(), this);
    }

    public static boolean hasActiveCompanion(Player player) {
        return activeCompanions.containsKey(player.getUniqueId());
    }

    public static Companion getCompanion(Player player) {
        return activeCompanions.get(player.getUniqueId());
    }

    public void setLevel(int level){
        this.level = level;
    }

    protected void setLivingEntity(LivingEntity entity) {
        this.livingEntity = entity;
    }

    public LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    public EntityType getEntityType(){
        return  getLivingEntity().getType();
    }

    public int getLevel(){
        return level;
    }

    public String getCompanionType() {
        if (livingEntity == null) {
            return "No companion";
        }
        return livingEntity.getType().name();
    }

    protected void startCompanionLoop(Runnable task) {
        if (keepLooping) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    task.run();
                    if (!keepLooping || !playerCompanion.isOnline()){
                        activeCompanions.remove(playerCompanion.getUniqueId());
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L); // Ejecuta cada segundo (20 ticks)
        }
    }

    protected boolean checkDistanceAndTeleport(org.bukkit.entity.Entity entity) {
        if (playerCompanion == null || !playerCompanion.isOnline() || entity == null ) {
            return true;
        }

        if(!playerCompanion.getWorld().equals(entity.getWorld())){
            return true;
        }

        double distance = playerCompanion.getLocation().distance(entity.getLocation());
        if (distance > maxDistance || !playerCompanion.getWorld().equals(entity.getWorld())) {
            entity.teleport(playerCompanion);
        }
        return false;
    }
}
