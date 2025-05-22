package pandoToros.game.modes;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pandoToros.game.modes.teamScoreboard.TeamPoints;
import pandodungeons.PandoDungeons;

import java.util.*;
import java.util.List;

import static pandoToros.game.modes.PlatformMode.startPlatformMode;
import static pandoToros.game.modes.SoccerMatch.createSoccerGoals;
import static pandoToros.game.modes.ZoneMode.startZoneMode;

public class GameMode {
    protected PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);
    protected List<Player> players;
    protected List<Player> team1 = new ArrayList<>();
    protected List<Player> team2 = new ArrayList<>();
    protected boolean won = false;
    protected boolean isTeam = false;
    protected TeamPoints teamPoints = new TeamPoints(0,0);
    private final Random RANDOM = new Random();
    private final Map<Player, Integer> points = new HashMap<>();
    private final World world;
    public static org.bukkit.Color TEAM1COLOR = org.bukkit.Color.RED;
    public static org.bukkit.Color TEAM2COLOR = org.bukkit.Color.GREEN;

    public GameMode(List<Player> players, World world){
        this.players = players;
        this.world = world;
        for(Player player : players){
            points.put(player,0);
        }
    }

   public int whichTeam(Player player){
        if(isTeamGamemode()){
            if(team1.contains(player)){
                return 1;
            }else if(team2.contains(player)){
                return 2;
            }
        }
        plugin.getLogger().info("El jugador " + player.getName() + " no esta en ningun equipo");
        return 0;
   }

    public void start(){
        if(canTeam()){
            if(RANDOM.nextBoolean()){
                sortTeams();
                int cas = RANDOM.nextInt(1);
                switch (cas) {
                    case 0:
                        createSoccerGoals(world, players, teamPoints, team1, team2);
                        break;
                    default:
                        break;
                }

            }else{
                int cas = RANDOM.nextInt(2);
                switch (cas){
                    case 0:
                        startZoneMode(world,plugin,points,players);
                        break;
                    case 1:
                        startPlatformMode(world,plugin,points,players);
                    default:
                        break;
                }
            }

        }else{
            int cas = RANDOM.nextInt(2);
            switch (cas){
                case 0:
                    startZoneMode(world,plugin,points,players);
                    break;
                case 1:
                    startPlatformMode(world,plugin,points,players);
                default:
                    break;
            }
        }
    }

    public Player getWinningPlayer() {
        int maxPoints = Integer.MIN_VALUE; // Valor mínimo inicial para comparar
        Player winningPlayer = null;

        for (Player player : points.keySet()) {
            int playerPoints = points.getOrDefault(player, 0);
            if (playerPoints > maxPoints) {
                maxPoints = playerPoints;
                winningPlayer = player; // Actualizar el jugador ganador
            }
        }

        return winningPlayer; // Retorna null si no hay jugadores en el mapa
    }


    public List<Player> getWiningTeam(){
        if(teamPoints.getTeam1Points() > teamPoints.getTeam2Points()){
            return team1;
        }else if(teamPoints.getTeam2Points() > teamPoints.getTeam1Points()){
            return team2;
        }else {
            return players;
        }
    }

    public boolean isTeamGamemode(){
        return isTeam;
    }

    public boolean canTeam(){
        return players.size() > 1;
    }

    public void sortTeams() {
        // Verifica que haya más de un jugador para repartir
            // Limpia los equipos antes de asignar jugadores
            isTeam = true;
            team1.clear();
            team2.clear();

            // Alternar entre los equipos al agregar jugadores
            boolean addToTeam1 = true;
            for (Player player : players) {
                if (addToTeam1) {
                    team1.add(player);
                } else {
                    team2.add(player);
                }
                addToTeam1 = !addToTeam1; // Alternar el equipo
            }

            // Imprimir los equipos para verificar
            plugin.getLogger().info("Team Red: " + team1.size() + " players");
            plugin.getLogger().info("Team Green: " + team2.size() + " players");
    }


    public boolean isWon(){
        return won;
    }

}
