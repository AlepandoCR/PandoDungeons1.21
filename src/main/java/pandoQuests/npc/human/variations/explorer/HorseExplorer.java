package pandoQuests.npc.human.variations.explorer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandoQuests.npc.goals.GuideMissionGoal;
import pandoQuests.npc.human.variations.HorseHuman;
import pandoClass.quests.Mission;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.UUID;

public class HorseExplorer extends HorseHuman implements Listener {

    private final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    private static final double MAX_DISTANCE = 40.0;
    private final HashMap<UUID, Mission> activeMissions = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> followTasks = new HashMap<>();

    /**
     * Crea un NPC montado en un caballo que funciona como explorador.
     *
     * @param name     El nombre del NPC.
     * @param location La ubicación inicial del NPC.
     */
    public HorseExplorer(String name, Location location, Player orbitadedPlayer) {
        super(name, location, orbitadedPlayer);
    }

    public void handleRightClick(Player player) {
        // Generar misión con coordenadas aleatorias cercanas
        Location missionTarget = player.getLocation().add(
                Math.random() * 50 - 25,
                0,
                Math.random() * 50 - 25
        );
        missionTarget.setY(missionTarget.getWorld().getHighestBlockYAt(missionTarget));

       //Mision AQUI

        // Agregar objetivo personalizado al NPC
//        GuideMissionGoal goal = new GuideMissionGoal(this, player, mission, MAX_DISTANCE);
//        getNpc().getDefaultGoalController().addGoal(goal, 1);

        player.sendMessage("¡Una nueva misión te espera! Vamos a las coordenadas: " +
                "X: " + missionTarget.getBlockX() + " Y: " + missionTarget.getBlockY() +
                " Z: " + missionTarget.getBlockZ());
    }

    @Override
    public void delete() {
        // Cancelar todas las misiones activas
        for (UUID playerId : followTasks.keySet()) {
            followTasks.get(playerId).cancel();
        }
        followTasks.clear();
        activeMissions.clear();

        super.delete();
    }
}
