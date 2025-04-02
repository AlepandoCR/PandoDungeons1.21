package controlledEntities.modeled.pets;

import controlledEntities.modeled.pets.types.miner.MinerPet;
import controlledEntities.modeled.pets.types.racoon.RacoonPet;
import org.bukkit.entity.Player;
import pandodungeons.pandodungeons.PandoDungeons;

public class PetFactory {

    public static Pet createPet(String petType, Player player, PandoDungeons plugin) {
        return switch (petType.toLowerCase()) {
            case "mapache" -> new RacoonPet(player, plugin); // Retorna un Mapache
            case "minero" -> new MinerPet(player, plugin); // Retorna un Minero
            default -> null; // En caso de que no se reconozca el tipo de mascota
        };
    }
}
