package pandoToros.game;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandoToros.game.modes.cosmetic.base.effects.team.TeamPlayerEffect;
import pandodungeons.pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.util.*;

import static pandoToros.Entities.toro.Toro.summonToro;
import static pandoToros.game.ArenaMaker.createRedondelWorld;
import static pandoToros.game.ArenaMaker.deleteRedondelWorld;
import static pandoToros.game.RandomBox.createBox;
import static pandoToros.game.modes.GameMode.TEAM1COLOR;
import static pandoToros.game.modes.GameMode.TEAM2COLOR;
import static pandoToros.utils.Anim.gateAnim;
import static pandoToros.utils.Anim.gateAnimClose;
import static pandoToros.utils.PlayerArmorChecker.hasArmor;

public class RedondelGame {

    private static final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    public static Map<UUID,Boolean> activeRedondel = new HashMap<>();

    public static boolean hasActiveRedondel(Player player){
        if(!activeRedondel.isEmpty()){
            return activeRedondel.getOrDefault(player.getUniqueId(),false);
        }else{
            return false;
        }
    }

    public static void StartRedondel(String creator, List<Player> players, boolean classic) {
        World newWorld = createRedondelWorld(creator); // Método para crear el mundo
        pandoToros.game.modes.GameMode gameMode = new pandoToros.game.modes.GameMode(players, newWorld);
        if (newWorld != null) {
            newWorld.setSpawnLocation(8, -3, 12);
            for (Player player : players) {
                activeRedondel.put(player.getUniqueId(),true);
                for(ItemStack item : player.getInventory().getContents()){
                    if(item != null) {
                        if (item.getType().equals(Material.BAMBOO_BLOCK)) {
                            item.setAmount(0);
                        }
                    }
                }
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


            if(!classic){
                gameMode.start();
            }

            new BukkitRunnable() {
                private int remainingTime = gameDuration;
                private boolean allDeath;
                final Location spawnLocation = new Location(newWorld, 8, -2, -12);
                final TeamPlayerEffect team1PlayerEffect = new TeamPlayerEffect(TEAM1COLOR, 1, 10);
                final TeamPlayerEffect team2PlayerEffect = new TeamPlayerEffect(TEAM2COLOR, 1, 10);
                @Override
                public void run() {
                    if (remainingTime <= 0 || allDeath) {
                        // Finaliza la partida
                        for (Player player : newWorld.getPlayers()) {
                            activeRedondel.remove(player.getUniqueId());
                            player.sendMessage(ChatColor.GREEN + "¡La partida ha terminado!");
                            if(player.getRespawnLocation() != null && !player.getRespawnLocation().getWorld().equals(newWorld)){
                                player.teleport(player.getRespawnLocation());
                            }else if(Bukkit.getWorld("spawn") != null){
                                player.teleport(Objects.requireNonNull(Bukkit.getWorld("spawn")).getSpawnLocation()); // Teletransporta a los jugadores al mundo principal
                            }else if(Bukkit.getWorld("world") != null){
                                player.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
                            }else{
                                player.damage(100);
                            }

                        }
                        if(!classic){
                            if(gameMode.isTeamGamemode()){
                                for(Player player : gameMode.getWiningTeam()){
                                    player.sendMessage("Ganó tu equipo");
                                }
                            }else{
                                gameMode.getWinningPlayer().sendMessage("Ganaste el juego!");
                            }
                        }
                        deleteRedondelWorld("redondel_" + creator.toLowerCase(Locale.ROOT));
                        this.cancel(); // Detiene el loop
                        return;
                    }

                    if(!classic && gameMode.isTeamGamemode()){
                        for (Player player : newWorld.getPlayers()) {
                            if(gameMode.whichTeam(player) == 1){
                                team1PlayerEffect.applyEffect(player);
                            }else if(gameMode.whichTeam(player) == 2){
                                team2PlayerEffect.applyEffect(player);
                            }
                        }
                    }

                    allDeath = true;
                    for (Player player : players) {
                        if(player.getGameMode() != GameMode.SPECTATOR){
                            ToroStatManager.getToroStatsManager(player).addTime();
                        }
                        player.sendActionBar(ChatColor.YELLOW + "Tiempo restante: " + ChatColor.RED + remainingTime + ChatColor.YELLOW + " segundos.");
                        if(!player.getGameMode().equals(GameMode.SPECTATOR)){
                            allDeath = false;
                        }
                    }

                    if(remainingTime == 290){
                        gateAnim(newWorld);
                        newWorld.loadChunk(spawnLocation.getChunk());
                        summonToro(spawnLocation);
                    }

                    if(remainingTime % 60 == 0 && remainingTime != 300){
                        newWorld.loadChunk(spawnLocation.getChunk());
                        summonToro(spawnLocation);
                        for(Player player : players){
                            player.getInventory().addItem(new ItemStack(Material.BAMBOO_BLOCK,5));
                        }
                    }

                    if(remainingTime % 30 == 0 && remainingTime != 300){
                        try {
                            createBox(new Location(newWorld, 8, -3 ,10));
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // Decrece el tiempo
                    remainingTime--;
                }
            }.runTaskTimer(plugin, 0L, 20L); // Ejecuta cada segundo
        }
    }

}
