package pandoToros.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static pandoToros.Entities.toro.Toro.summonToro;
import static pandoToros.game.ArenaMaker.createRedondelWorld;
import static pandoToros.game.ArenaMaker.deleteRedondelWorld;
import static pandoToros.utils.Anim.gateAnim;
import static pandoToros.utils.Anim.gateAnimClose;
import static pandoToros.utils.PlayerArmorChecker.hasArmor;

public class RedondelGame {

    private static final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    public static void StartRedondel(String creator, List<Player> players) {
        World newWorld = createRedondelWorld(creator); // Método para crear el mundo
        if (newWorld != null) {
            newWorld.setSpawnLocation(8, -3, 12);
            for (Player player : players) {
                if (hasArmor(player, false)) { // Verifica si el jugador tiene armadura
                    player.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Aviso: "
                            + ChatColor.RED + "Tienes armadura, por lo que recibirás daño extra. "
                            + "Puedes quitártela ahora y no recibirás daño extra.");
                }
                player.teleport(newWorld.getSpawnLocation()); // Teletransporta a los jugadores al spawn
            }

            // Configura el tiempo de la partida (por ejemplo, 5 minutos)
            int gameDuration = 300; // Duración en segundos (5 minutos)
            gateAnimClose(newWorld);

            new BukkitRunnable() {
                private int remainingTime = gameDuration;


                @Override
                public void run() {
                    if (remainingTime <= 0) {
                        // Finaliza la partida
                        for (Player player : players) {
                            player.sendMessage(ChatColor.GREEN + "¡La partida ha terminado!");
                            if(player.getRespawnLocation() != null){
                                player.teleport(player.getRespawnLocation());
                            }else if(Bukkit.getWorld("spawn") != null){
                                player.teleport(Objects.requireNonNull(Bukkit.getWorld("spawn")).getSpawnLocation()); // Teletransporta a los jugadores al mundo principal
                            }else if(Bukkit.getWorld("world") != null){
                                player.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
                            }else{
                                player.damage(100);
                            }

                        }
                        deleteRedondelWorld("redondel_" + creator.toLowerCase(Locale.ROOT));
                        cancel(); // Detiene el loop
                        return;
                    }

                    // Envía un aviso a los jugadores cada minuto
                    for (Player player : players) {
                        player.sendActionBar(ChatColor.YELLOW + "Tiempo restante: " + ChatColor.RED + remainingTime + ChatColor.YELLOW + " segundos.");
                    }

                    if(remainingTime == 290){
                        gateAnim(newWorld);
                        summonToro(new Location(newWorld, 8,-2,-12));
                    }

                    // Decrece el tiempo
                    remainingTime--;
                }
            }.runTaskTimer(plugin, 0L, 20L); // Ejecuta cada segundo
        }
    }

}
