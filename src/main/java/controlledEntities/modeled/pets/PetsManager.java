package controlledEntities.modeled.pets;

import controlledEntities.modeled.pets.types.miner.MinerPet;
import controlledEntities.modeled.pets.types.racoon.RacoonPet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
        pets.remove(pet);
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
        if(pet instanceof RacoonPet){
          new RacoonPet(player,plugin);
        }else if(pet instanceof MinerPet){
            new MinerPet(player,plugin);
        }else{
            player.sendMessage("No es pet");
        }

    }

    public void destroyAllPets(){
        pets.forEach(this::destroyPet);
    }
}
