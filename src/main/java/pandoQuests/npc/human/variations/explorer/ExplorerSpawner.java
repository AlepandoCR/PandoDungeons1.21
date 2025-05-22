package pandoQuests.npc.human.variations.explorer;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.PandoDungeons;

import java.util.Random;

public class ExplorerSpawner {

    private static final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    /**
     * Invoca un explorador en una ubicación aleatoria cercana al jugador.
     *
     * @param player El jugador alrededor del cual aparecerá el explorador.
     */
    public static void spawnExplorerNearPlayer(Player player) {
        World world = player.getWorld();
        Location playerLocation = player.getLocation();
        Random random = new Random();

        // Generar una ubicación aleatoria cercana al jugador
        Location spawnLocation = playerLocation.clone().add(
                random.nextInt(100) - 50, 0, random.nextInt(100) - 50
        );
        spawnLocation.setY(world.getHighestBlockYAt(spawnLocation) + 1);

        // Asignar comportamiento al explorador
        HorseExplorer horseExplorer = new HorseExplorer("Explorador", spawnLocation, player);

        // Desaparecer el explorador después de un tiempo
        scheduleExplorerDespawn(horseExplorer.getNpc(), horseExplorer.getHorseNPC()); // 10 minutos
    }

    /**
     * Programa la desaparición automática del explorador.
     *
     * @param npc El NPC del explorador.
     */
    private static void scheduleExplorerDespawn(NPC npc, NPC horse) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (npc.isSpawned()) {
                    npc.despawn();
                    npc.destroy();
                }
                if(horse.isSpawned()){
                    horse.despawn();
                    horse.destroy();
                }
            }
        }.runTaskLater(plugin, 12000);
    }
}
