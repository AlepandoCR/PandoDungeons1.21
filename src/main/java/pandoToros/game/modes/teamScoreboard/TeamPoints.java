package pandoToros.game.modes.teamScoreboard;

public class TeamPoints {
    private int team1Points;
    private int team2Points;

    public TeamPoints(int team1Points, int team2Points){
        this.team1Points = team1Points;
        this.team2Points = team2Points;
    }

    public int getTeam1Points() {
        return team1Points;
    }

    public void incrementTeam1Points() {
        team1Points++;
    }

    public int getTeam2Points() {
        return team2Points;
    }

    public void incrementTeam2Points() {
        team2Points++;
    }
}

