package pandoQuests.npc.goals;

import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalSelector;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pandoQuests.npc.human.BasicNPC;
import pandoClass.quests.Mission;

public class GuideMissionGoal implements Goal {

    private final BasicNPC npc;
    private final Player player;
    private final Mission mission;
    private final double maxDistance;
    private boolean isComplete = false;

    /**
     * Crea un objetivo para que el NPC guíe al jugador hacia las coordenadas de la misión.
     *
     * @param npc         El NPC que ejecutará este objetivo.
     * @param player      El jugador que debe ser guiado.
     * @param mission     La misión asociada.
     * @param maxDistance La distancia máxima permitida entre el jugador y el NPC.
     */
    public GuideMissionGoal(BasicNPC npc, Player player, Mission mission, double maxDistance) {
        this.npc = npc;
        this.player = player;
        this.mission = mission;
        this.maxDistance = maxDistance;
    }

    @Override
    public void reset() {
        isComplete = false;
    }

    @Override
    public void run(GoalSelector selector) {
        if (!player.isOnline() || !npc.isSpawned()) {
            cancelMission(selector);
            return;
        }

        Location playerLocation = player.getLocation();
        double distanceToPlayer = playerLocation.distance(npc.getNpc().getEntity().getLocation());

        if (distanceToPlayer > maxDistance) {
            player.sendMessage("Te has alejado demasiado. La misión ha sido cancelada.");
            cancelMission(selector);
            return;
        }

        if (distanceToPlayer > 10) {
            // Esperar al jugador si está lejos
            npc.getNpc().getNavigator().setTarget(playerLocation);
        } else {
            // Guía hacia el destino
//            Location target = mission.getTargetLocation();
//            double distanceToTarget = npc.getNpc().getEntity().getLocation().distance(target);
//
//            if (distanceToTarget < 2) {
//                player.sendMessage("¡Has llegado al destino de la misión!");
//                isComplete = true;
//                selector.finish();
//            } else {
//                npc.getNpc().getNavigator().setTarget(target);
//            }
        }
    }

    @Override
    public boolean shouldExecute(GoalSelector selector) {
        return !isComplete;
    }

    private void cancelMission(GoalSelector selector) {
        isComplete = true;
        selector.finish();
    }
}
