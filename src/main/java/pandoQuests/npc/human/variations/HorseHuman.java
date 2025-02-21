package pandoQuests.npc.human.variations;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import pandoQuests.npc.goals.ConstantlyCheckPlayerDistanceGoal;
import pandoQuests.npc.goals.OrbitPlayerGoal;
import pandoQuests.npc.human.BasicNPC;

public class HorseHuman extends BasicNPC {

    private final NPC horseNPC;

    /**
     * Crea un NPC montado en un caballo.
     *
     * @param name     El nombre del NPC.
     * @param location La ubicación inicial del NPC.
     */
    public HorseHuman(String name, Location location, Player player) {
        super(name, location);

        ConstantlyCheckPlayerDistanceGoal distanceGoal = new ConstantlyCheckPlayerDistanceGoal(this, player, 100);
        OrbitPlayerGoal orbitPlayerGoal = new OrbitPlayerGoal(this, player, 20,1);

        addGoal(distanceGoal,0);
        addGoal(orbitPlayerGoal,1);

        // Crear el NPC del caballo
        this.horseNPC = CitizensAPI.getNPCRegistry().createNPC(EntityType.HORSE, "Horse for " + name);

        // Spawnear el caballo en la ubicación dada
        this.horseNPC.spawn(location);

        // Obtener el caballo como entidad
        if (this.horseNPC.getEntity() instanceof Horse horse) {
            horse.setTamed(true); // Asegurar que el caballo esté domado
            horse.setOwner(null); // Sin dueño
            horse.setCustomName("Mount of " + name);
            horse.setCustomNameVisible(true);

            // Hacer que el NPC humano monte el caballo
            if (getNpc().isSpawned() && getNpc().getEntity() instanceof LivingEntity rider) {
                horse.addPassenger(rider);
            }
        }
    }

    @Override
    public void moveTo(Location location) {
        // Mover el caballo y al NPC montado
        if (horseNPC.isSpawned()) {
            horseNPC.getNavigator().setTarget(location);
        }
    }

    @Override
    public void teleport(Location location) {
        // Teletransportar tanto al caballo como al NPC montado
        if (horseNPC.isSpawned()) {
            horseNPC.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
        super.teleport(location);
    }

    @Override
    public void delete() {
        // Eliminar el caballo y al NPC humano
        if (horseNPC != null) {
            horseNPC.destroy();
        }
        super.delete();
    }

    /**
     * Obtiene el NPC del caballo.
     *
     * @return El NPC del caballo.
     */
    public NPC getHorseNPC() {
        return this.horseNPC;
    }
}
