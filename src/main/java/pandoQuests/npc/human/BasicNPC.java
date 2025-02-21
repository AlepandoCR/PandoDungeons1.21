package pandoQuests.npc.human;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BasicNPC {

    private final NPC npc;

    /**
     * Crea un NPC básico.
     *
     * @param name El nombre del NPC.
     * @param location La ubicación inicial del NPC.
     */
    public BasicNPC(String name, Location location) {
        // Crear el NPC usando CitizensAPI
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        // Establecer la posición inicial
        this.npc.spawn(location);
    }

    /**
     * Establece un mensaje que el NPC dirá al ser clicado.
     *
     * @param message El mensaje que el NPC dirá.
     */
    public void setClickMessage(String message) {
        this.npc.getDefaultSpeechController().speak(new SpeechContext(message));
    }

    /**
     * Mueve el NPC a una nueva ubicación.
     *
     * @param location La nueva ubicación.
     */
    public void moveTo(Location location) {
        if (npc.isSpawned()) {
            npc.getNavigator().setTarget(location);
        }
    }

    /**
     * Teletransporta instantáneamente el NPC a una ubicación.
     *
     * @param location La nueva ubicación.
     */
    public void teleport(Location location) {
        if (npc.isSpawned()) {
            npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    /**
     * Elimina el NPC del mundo y del registro.
     */
    public void delete() {
        if (npc != null) {
            npc.destroy();
        }
    }

    public void addGoal(Goal goal, int priority){
        if(npc != null){
            npc.getDefaultGoalController().addGoal(goal, priority);
        }
    }

    /**
     * Agrega un trait personalizado al NPC.
     *
     * @param trait El trait a agregar.
     */
    public void addTrait(Trait trait) {
        if (npc != null) {
            npc.addTrait(trait.getClass());
        }
    }

    /**
     * Devuelve si el NPC está actualmente en el mundo.
     *
     * @return true si el NPC está activo, de lo contrario false.
     */
    public boolean isSpawned() {
        return npc.isSpawned();
    }

    /**
     * Obtiene el NPC subyacente.
     *
     * @return El objeto NPC.
     */
    public NPC getNpc() {
        return this.npc;
    }
}
