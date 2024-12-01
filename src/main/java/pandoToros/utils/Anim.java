package pandoToros.utils;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;

public class Anim {

    private static final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    public static void gateAnim(World world) {
        new BukkitRunnable() {
            private int step = 1;

            @Override
            public void run() {
                switch (step) {
                    case 1:
                        world.getBlockAt(7, -3, -10).setType(Material.AIR); // Cambia los bloques de la primera capa
                        world.getBlockAt(8, -3, -10).setType(Material.AIR);
                        world.getBlockAt(9, -3, -10).setType(Material.AIR);
                        break;
                    case 2:
                        world.getBlockAt(7, -2, -10).setType(Material.AIR); // Cambia los bloques de la segunda capa
                        world.getBlockAt(8, -2, -10).setType(Material.AIR);
                        world.getBlockAt(9, -2, -10).setType(Material.AIR);
                        break;
                    case 3:
                        world.getBlockAt(7, -1, -10).setType(Material.AIR); // Cambia los bloques de la tercera capa
                        world.getBlockAt(8, -1, -10).setType(Material.AIR);
                        world.getBlockAt(9, -1, -10).setType(Material.AIR);
                        break;
                    case 4:
                        world.getBlockAt(8, 0, -10).setType(Material.AIR); // Cambia el bloque final
                        break;
                    default:
                        cancel(); // Termina la animación
                        return;
                }
                step++;
            }
        }.runTaskTimer(plugin, 0L, 10L); // Ejecuta cada 0.5 segundos (10 ticks)
    }

    public static void gateAnimClose(World world) {
        new BukkitRunnable() {
            private int step = 1;

            @Override
            public void run() {
                switch (step) {
                    case 1:
                        world.getBlockAt(7, -3, -10).setType(Material.TUFF_WALL); // Cambia los bloques de la primera capa
                        world.getBlockAt(8, -3, -10).setType(Material.TUFF_WALL);
                        world.getBlockAt(9, -3, -10).setType(Material.TUFF_WALL);
                        break;
                    case 2:
                        world.getBlockAt(7, -2, -10).setType(Material.TUFF_WALL); // Cambia los bloques de la segunda capa
                        world.getBlockAt(8, -2, -10).setType(Material.TUFF_WALL);
                        world.getBlockAt(9, -2, -10).setType(Material.TUFF_WALL);
                        break;
                    case 3:
                        world.getBlockAt(7, -1, -10).setType(Material.TUFF_WALL); // Cambia los bloques de la tercera capa
                        world.getBlockAt(8, -1, -10).setType(Material.TUFF_WALL);
                        world.getBlockAt(9, -1, -10).setType(Material.TUFF_WALL);
                        break;
                    case 4:
                        world.getBlockAt(8, 0, -10).setType(Material.AIR); // Cambia el bloque final
                        break;
                    default:
                        cancel(); // Termina la animación
                        return;
                }
                step++;
            }
        }.runTaskTimer(plugin, 0L, 1L); // Ejecuta cada 0.5 segundos (10 ticks)
    }


}
