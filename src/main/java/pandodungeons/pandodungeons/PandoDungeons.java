package pandodungeons.pandodungeons;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import pandodungeons.pandodungeons.Utils.CompanionUtils;
import pandodungeons.pandodungeons.commands.Management.CommandManager;
import pandodungeons.pandodungeons.Listeners.PlayerEventListener;
import pandodungeons.pandodungeons.Game.PlayerStatsManager;
import pandodungeons.pandodungeons.Utils.LocationUtils;
import pandodungeons.pandodungeons.Utils.StructureUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static pandodungeons.pandodungeons.Utils.CompanionUtils.loadAllCompanions;
import static pandodungeons.pandodungeons.Utils.ItemUtils.*;

public final class PandoDungeons extends JavaPlugin {
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        // Create data folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Create dungeons_struct folder if it doesn't exist
        File structFolder = new File(getDataFolder(), "dungeons_struct");
        if (!structFolder.exists()) {
            structFolder.mkdirs();
        }


        File companions = new File(getDataFolder(), "companions");
        if (!companions.exists()) {
            companions.mkdirs();
        }

        // Register events and commands
        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);
        this.getCommand("dungeons").setExecutor(new CommandManager(this));

        // Ensure player data folder exists
        File playerDataFolder = new File(getDataFolder(), "PlayerData");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }

        File dungeonsDataFolder = new File(getDataFolder(), "dungeons_data");
        if (!dungeonsDataFolder.exists()) {
            dungeonsDataFolder.mkdirs();
        }
        breezeCompanionCustomRecipe();
        armadilloCompanionCustomRecipe();
        allayCompanionCustomRecipe();
        osoCompanionCustomRecipe();

        unlockRecipeForAllPlayers(getOsoCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getArmadilloCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getBreezeCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getAllayCompanionCustomRecipe());

        loadAllCompanions();

        // Create or load player stats files
        getServer().getOnlinePlayers().forEach(player -> {
            try {
                File playerFile = new File(playerDataFolder, player.getUniqueId() + ".yml");
                if (!playerFile.exists()) {
                    playerFile.createNewFile();
                }
                PlayerStatsManager.getPlayerStatsManager(player);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        PlayerStatsManager.loadAllPlayerStats();
    }

    @Override
    public void onDisable() {
        for(String world : LocationUtils.getAllDungeonWorlds()){
            if(world == null){
                break;
            }
            StructureUtils.removeDungeon(world,this);
        }
        for(Player player : getServer().getOnlinePlayers()){
            if(LocationUtils.hasActiveDungeon(player.getUniqueId().toString())){
                if(player.getBedSpawnLocation() != null){
                    player.teleport(player.getBedSpawnLocation());
                }
                if(Bukkit.getWorld("spawn") != null){
                    player.teleport(Objects.requireNonNull(Bukkit.getWorld("spawn")).getSpawnLocation());
                }
            }
        }
    }

    private void unlockRecipeForAllPlayers(Recipe recipe) {
        NamespacedKey key = ((ShapedRecipe) recipe).getKey();

        // Desbloquear la receta para todos los jugadores en línea
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.discoverRecipe(key);
        }

        // Evento para desbloquear la receta para jugadores que se unan posteriormente
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
                event.getPlayer().discoverRecipe(key);
            }
        }, this);
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
