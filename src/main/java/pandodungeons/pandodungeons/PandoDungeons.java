package pandodungeons.pandodungeons;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import pandoClass.ClassRPG;
import pandoClass.RPGListener;
import pandoClass.RPGPlayer;
import pandoClass.campsListener.CampsListener;
import pandoClass.gachaPon.GachaCommand;
import pandoClass.gachaPon.prizes.PrizeListener;
import pandoClass.gachaPon.prizes.PrizeManager;
import pandoToros.game.ToroStatManager;
import pandoToros.listeners.ToroGameListener;
import pandoToros.utils.RedondelCommand;
import pandodungeons.pandodungeons.CustomEntities.Ball.BallEventHandler;
import pandodungeons.pandodungeons.Utils.ItemUtils;
import pandodungeons.pandodungeons.Utils.PlayerPartyList;
import pandodungeons.pandodungeons.commands.Management.CommandManager;
import pandodungeons.pandodungeons.Listeners.PlayerEventListener;
import pandodungeons.pandodungeons.Game.PlayerStatsManager;
import pandodungeons.pandodungeons.Utils.LocationUtils;
import pandodungeons.pandodungeons.Utils.StructureUtils;
import pandodungeons.pandodungeons.commands.game.PartyCommand;
import textures.TextureCommand;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static pandoClass.files.RPGPlayerDataManager.getRPGPlayerMap;
import static pandoClass.files.RPGPlayerDataManager.loadAllPlayers;
import static pandodungeons.pandodungeons.Utils.CompanionUtils.loadAllCompanions;
import static pandodungeons.pandodungeons.Utils.ItemUtils.*;

public final class PandoDungeons extends JavaPlugin {
    private CommandManager commandManager;
    public PlayerPartyList playerPartyList = new PlayerPartyList();
    public Map<Player, ClassRPG> rpgPlayersList = new HashMap<>();
    public PrizeManager prizeManager = new PrizeManager(this);

    @Override
    public void onEnable() {

        rpgPlayersList = getRPGPlayerMap();
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

        File rpgPlayers = new File(getDataFolder(), "rpgPlayers");
        if (!rpgPlayers.exists()) {
            rpgPlayers.mkdirs();
        }


        // Register events and commands
        getServer().getPluginManager().registerEvents(new PrizeListener(this), this);
        getServer().getPluginManager().registerEvents(new CampsListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);
        getServer().getPluginManager().registerEvents(new ToroGameListener(), this);
        getServer().getPluginManager().registerEvents(new BallEventHandler(this), this);
        getServer().getPluginManager().registerEvents(new RPGListener(this), this);

        this.getCommand("gachatoken").setExecutor(new GachaCommand(this));
        this.getCommand("texturas").setExecutor(new TextureCommand(this));
        this.getCommand("dungeons").setExecutor(new CommandManager(this));
        this.getCommand("redondel").setExecutor(new RedondelCommand(this));
        this.getCommand("party").setExecutor(new PartyCommand(this));

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
        snifferCompanionCustomRecipe();
        pufferFishCompanionCustomRecipe();
        copperGumRecipe();
        soccerBallRecipe();

        unlockRecipeForAllPlayers(getPufferFishCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getOsoCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getArmadilloCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getBreezeCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getAllayCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getSnifferCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getCopperGumRecipe());
        unlockRecipeForAllPlayers(getSoccerBallRecipe());
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

        getServer().getOnlinePlayers().forEach(player -> {
            try {
                File playerFile = new File(playerDataFolder, player.getUniqueId() + "_Toro" + ".yml");
                if (!playerFile.exists()) {
                    playerFile.createNewFile();
                }
                ToroStatManager.getToroStatsManager(player);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        ToroStatManager.loadAllToroPlayerStats();
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
