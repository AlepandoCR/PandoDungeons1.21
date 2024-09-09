package pandodungeons.pandodungeons.bossfights.fights.randomFight;

import org.bukkit.Location;
import pandodungeons.pandodungeons.bossfights.fights.*;

import java.util.Random;

public class RandomFight {

    private final Location location;

    public RandomFight(Location location) {
        this.location = location;
    }

    public void startRandomFight() {
        Random random = new Random();
        int fightIndex = random.nextInt(6); // Escoge entre 0 y 4

        switch (fightIndex) {
            case 0:
                startQueenBeeFight();
                break;
            case 1:
                startTritonFight();
                break;
            case 2:
                startVexFight();
                break;
            case 3:
                startForestGuardianFight();
                break;
            case 4:
                startSpiderFight();
                break;
            case 5:
                startMagmaCubeBossFight();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + fightIndex);
        }
    }


    private void startMagmaCubeBossFight() {
        CubitoBoomFight cubitoBoomFight = new CubitoBoomFight(location);
        cubitoBoomFight.startMagmaCubeBossFight();
    }

    private void startQueenBeeFight() {
        QueenBeeFight queenBeeFight = new QueenBeeFight(location);
        queenBeeFight.startQueenBeeFight();
    }

    private void startTritonFight() {
        TritonFight tritonFight = new TritonFight(location);
        tritonFight.startTritonFight();
    }

    private void startVexFight(){
        VexBossFight fight = new VexBossFight(location);
        fight.startVexBossFight();
    }
    private void startForestGuardianFight(){
        ForestGuardianBossFight fight = new ForestGuardianBossFight(location);
        fight.startForestGuardianBossFight();;
    }
    private void startSpiderFight(){
        SpiderBossFight fight = new SpiderBossFight(location);
        fight.startSpiderBossFight();
    }
}
