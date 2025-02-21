package pandoQuests.npc.goals;

import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pandoQuests.npc.human.BasicNPC;

public class OrbitPlayerGoal implements Goal {

    private final BasicNPC npc;
    private final Player player;
    private final double orbitRadius;
    private final double orbitSpeed;
    private boolean isComplete = false;
    private double angle = 0;  // Para hacer que el NPC orbite

    /**
     * Crea un objetivo que hace que el NPC orbite alrededor del jugador.
     *
     * @param npc          El NPC que ejecutará este objetivo.
     * @param player       El jugador alrededor del cual orbitará el NPC.
     * @param orbitRadius  El radio de la órbita (distancia entre el NPC y el jugador).
     * @param orbitSpeed   La velocidad de la órbita (cuánto se mueve el NPC por tick).
     */
    public OrbitPlayerGoal(BasicNPC npc, Player player, double orbitRadius, double orbitSpeed) {
        this.npc = npc;
        this.player = player;
        this.orbitRadius = orbitRadius;
        this.orbitSpeed = orbitSpeed;
    }

    @Override
    public void reset() {
        isComplete = false;
        angle = 0; // Resetear el ángulo para el siguiente ciclo
    }

    @Override
    public void run(GoalSelector selector) {
        if (!player.isOnline() || !npc.isSpawned()) {
            cancelMission(selector);
            return;
        }

        Location playerLocation = player.getLocation();
        // Calculamos las nuevas coordenadas para la órbita usando sen y cos para crear un movimiento circular
        double xOffset = orbitRadius * Math.cos(angle);
        double zOffset = orbitRadius * Math.sin(angle);

        // Establecemos la nueva ubicación del NPC con un desplazamiento alrededor del jugador
        Location targetLocation = playerLocation.clone().add(xOffset, 0, zOffset);

        // Mover al NPC a la nueva ubicación
        npc.getNpc().getNavigator().setTarget(targetLocation);

        // Incrementamos el ángulo para la próxima posición en la órbita
        angle += orbitSpeed;

        // Aseguramos que el ángulo no se pase de 360 grados (o 2PI en radianes)
        if (angle >= 2 * Math.PI) {
            angle = 0;
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
