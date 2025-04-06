package controlledEntities.modeled.pets;

import controlledEntities.modeled.pets.types.miner.MinerPet;
import controlledEntities.modeled.pets.types.racoon.RacoonPet;
import controlledEntities.modeled.pets.types.sakura.SakuraPet;
import org.bukkit.entity.Player;
import pandoClass.RPGPlayer;
import pandodungeons.pandodungeons.PandoDungeons;

import static controlledEntities.modeled.pets.PetType.MAPACHE;
import static controlledEntities.modeled.pets.PetType.MINERO;

public class PetFactory {

    public static Pet createPet(PetType petType, Player player, PandoDungeons plugin) {
        return switch (petType) {
            case MAPACHE -> new RacoonPet(player, plugin); // Retorna un Mapache
            case MINERO -> new MinerPet(player, plugin); // Retorna un Minero
            case SAKURA -> new SakuraPet(player,plugin);
        };
    }

    public static void savePetName(PetType petType, String name, Player player, PandoDungeons plugin){
        RPGPlayer rpgPlayer = new RPGPlayer(player,plugin);

        switch (petType) {
            case MAPACHE ->  rpgPlayer.setRacoonPetName(name);
            case MINERO -> rpgPlayer.setMinerPetName(name);
            case SAKURA -> rpgPlayer.setSakuraPetName(name); // En caso de que no se reconozca el tipo de mascota
        };
    }

    public static String getPetName(PetType petType, Player player, PandoDungeons plugin){
        RPGPlayer rpgPlayer = new RPGPlayer(player,plugin);

        return switch (petType) {
            case MAPACHE ->  rpgPlayer.getRacoonPetName();
            case MINERO -> rpgPlayer.getMinerPetName();
            case SAKURA -> rpgPlayer.getSakuraPetName();
        };
    }
}
