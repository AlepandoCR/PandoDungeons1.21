package pandoToros.game.modes.teamScoreboard;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class Scoreboard {

    private final ArmorStand team1Stand;
    private final ArmorStand team2Stand;

    /**
     * Crea un marcador para mostrar los puntos de ambos equipos.
     *
     * @param world El mundo donde se colocará el marcador.
     * @param team1Name Nombre del equipo 1.
     * @param team2Name Nombre del equipo 2.
     */
    public Scoreboard(World world, String team1Name, String team2Name) {
        // Coordenadas para el marcador
        Location team1Location = new Location(world, 10, 3, -8);
        Location team2Location = new Location(world, 6, 3, -8);

        // Crear ArmorStands para los equipos
        this.team1Stand = createStand(team1Location, team1Name, ChatColor.RED);
        this.team2Stand = createStand(team2Location, team2Name, ChatColor.GREEN);
    }

    public void updateScore(TeamPoints teamPoints) {
        team1Stand.setCustomName(ChatColor.RED + "Equipo Rojo: " + ChatColor.WHITE + teamPoints.getTeam1Points());
        team2Stand.setCustomName(ChatColor.GREEN + "Equipo Verde: " + ChatColor.WHITE + teamPoints.getTeam2Points());
    }

    /**
     * Crea un ArmorStand para mostrar texto del marcador.
     *
     * @param location Ubicación donde se colocará el ArmorStand.
     * @param teamName Nombre del equipo.
     * @param color Color para el texto del equipo.
     * @return El ArmorStand creado.
     */
    private ArmorStand createStand(Location location, String teamName, ChatColor color) {
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setCustomName(color + teamName + ": 0");
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setVisible(false);
        return stand;
    }

    /**
     * Elimina el marcador del mundo.
     */
    public void removeScoreboard() {
        team1Stand.remove();
        team2Stand.remove();
    }
}
