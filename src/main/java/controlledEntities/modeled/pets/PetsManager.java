package controlledEntities.modeled.pets;

import controlledEntities.modeled.pets.types.graywolf.GrayWolfPet;
import controlledEntities.modeled.pets.types.jojo.JojoPet;
import controlledEntities.modeled.pets.types.miner.MinerPet;
import controlledEntities.modeled.pets.types.racoon.RacoonPet;
import controlledEntities.modeled.pets.types.sakura.SakuraPet;
import controlledEntities.modeled.pets.types.spectralwolf.SpectralWolfPet;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class PetsManager {
    private final List<Pet> pets = new ArrayList<>();
    private final PandoDungeons plugin;

    public PetsManager(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    public PandoDungeons getPlugin() {
        return plugin;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void addPet(Pet pet){
        pets.add(pet);
    }

    public void removePet(Pet pet){
        pets.remove(pet);
    }

    public void destroyPet(Pet pet){
        removePet(pet);
        pet.destroy();
    }

    public void destroyPets(Player player) {
        if (player == null) {
            return;
        }

        List<Pet> petsToDestroy = new ArrayList<>();

        for (Pet pet : pets) {
            if (pet != null && pet.getOwner() != null && pet.getOwner().equals(player)) {
                petsToDestroy.add(pet);
            }
        }

        for (Pet pet : petsToDestroy) {
            try {
                destroyPet(pet);
                pets.remove(pet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    public void handlePlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if(getPets().isEmpty()) return;

        // Buscar la mascota del jugador
        Pet pet = getPets().stream()
                .filter(p -> p.getOwner().equals(player))
                .findFirst()
                .orElse(null);

        if (pet == null) return;// El jugador no tiene mascota

        // Destruir la mascota antes del cambio
        destroyPet(pet);

        // Volver a crear la mascota después de 1 segundo para asegurar que el cambio de mundo se complete
        switch (pet) {
            case RacoonPet ignored -> new RacoonPet(player, plugin);
            case MinerPet ignored -> new MinerPet(player, plugin);
            case SakuraPet ignored -> new SakuraPet(player, plugin);
            case JojoPet ignored -> new JojoPet(player, plugin);
            case GrayWolfPet ignored -> new GrayWolfPet(player,plugin);
            case SpectralWolfPet ignored -> new SpectralWolfPet(player,plugin);
            default -> player.sendMessage("No es pet");
        }

    }

    public void destroyAllPets(){
        pets.forEach(pet -> destroyPets(pet.getOwner()));
    }
}
