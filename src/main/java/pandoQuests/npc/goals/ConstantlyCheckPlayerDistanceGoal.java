package pandoQuests.npc.goals;

import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalSelector;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pandoQuests.npc.human.BasicNPC;

public class ConstantlyCheckPlayerDistanceGoal implements Goal {

    private final BasicNPC npc;
    private final Player player;
    private final double requiredDistance;
    private boolean isComplete = false;

    /**
     * Crea un objetivo que mantiene al NPC siempre activo y verifica la proximidad del jugador.
     *
     * @param npc             El NPC que ejecutará este objetivo.
     * @param player          El jugador cuya proximidad se debe verificar.
     * @param requiredDistance La distancia mínima requerida entre el jugador y el NPC.
     */
    public ConstantlyCheckPlayerDistanceGoal(BasicNPC npc, Player player, double requiredDistance) {
        this.npc = npc;
        this.player = player;
        this.requiredDistance = requiredDistance;
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

        if (distanceToPlayer > requiredDistance) {
            npc.getNpc().getDefaultGoalController().removeGoal(new OrbitPlayerGoal(npc,player, 20,1));
            npc.delete();
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
