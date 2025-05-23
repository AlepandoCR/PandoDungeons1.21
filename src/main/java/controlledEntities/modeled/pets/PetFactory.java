package controlledEntities.modeled.pets;

import controlledEntities.modeled.pets.types.graywolf.GrayWolfPet;
import controlledEntities.modeled.pets.types.jojo.JojoPet;
import controlledEntities.modeled.pets.types.miner.MinerPet;
import controlledEntities.modeled.pets.types.racoon.RacoonPet;
import controlledEntities.modeled.pets.types.sakura.SakuraPet;
import controlledEntities.modeled.pets.types.spectralwolf.SpectralWolfPet;
import org.bukkit.entity.Player;
import pandoClass.RPGPlayer;
import pandodungeons.PandoDungeons;

public class PetFactory {

    public static void createPet(PetType petType, Player player, PandoDungeons plugin) {
        switch (petType) {
            case MAPACHE -> new RacoonPet(player, plugin); // Retorna un Mapache
            case MINERO -> new MinerPet(player, plugin); // Retorna un Minero
            case SAKURA -> new SakuraPet(player, plugin);
            case JOJO -> new JojoPet(player, plugin);
            case LOBOGRIS -> new GrayWolfPet(player, plugin);
            case LOBOESPECTRAL -> new SpectralWolfPet(player, plugin);
        }
    }

    public static void savePetName(PetType petType, String name, Player player, PandoDungeons plugin){
        RPGPlayer rpgPlayer = plugin.rpgManager.getPlayer(player);

        switch (petType) {
            case MAPACHE ->  rpgPlayer.setRacoonPetName(name);
            case MINERO -> rpgPlayer.setMinerPetName(name);
            case SAKURA -> rpgPlayer.setSakuraPetName(name); // En caso de que no se reconozca el tipo de mascota
            case JOJO -> rpgPlayer.setJojoPetName(name);
            case LOBOGRIS -> rpgPlayer.setGrayWolfPetName(name);
            case LOBOESPECTRAL -> rpgPlayer.setSpectralWolfPetName(name);
        }
    }

    public static String getPetName(PetType petType, Player player, PandoDungeons plugin){
        RPGPlayer rpgPlayer =  plugin.rpgManager.getPlayer(player);

        return switch (petType) {
            case MAPACHE ->  rpgPlayer.getRacoonPetName();
            case MINERO -> rpgPlayer.getMinerPetName();
            case SAKURA -> rpgPlayer.getSakuraPetName();
            case JOJO -> rpgPlayer.getJojoPetName();
            case LOBOGRIS -> rpgPlayer.getGrayWolfPetName();
            case LOBOESPECTRAL -> rpgPlayer.getSpectralWolfPetName();
        };
    }
}
