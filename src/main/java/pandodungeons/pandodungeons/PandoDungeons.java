package pandodungeons.pandodungeons;

import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;
import pandoClass.Camp;
import pandoClass.ClassRPG;
import pandoClass.RPGListener;
import pandoClass.RPGPlayer;
import pandoClass.campsListener.CampsListener;
import pandoClass.classes.ClassCommand;
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
    public Camp camp = new Camp();
    private BukkitRunnable runnable;

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
        this.getCommand("stats").setExecutor(new ClassCommand(this));

        // Ensure player data folder exists
        File playerDataFolder = new File(getDataFolder(), "PlayerData");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }

        File dungeonsDataFolder = new File(getDataFolder(), "dungeons_data");
        if (!dungeonsDataFolder.exists()) {
            dungeonsDataFolder.mkdirs();
        }

        startHordeLookout(this);

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
        runnable.cancel();
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

    private void startHordeLookout(PandoDungeons plugin) {
        runnable = new BukkitRunnable() {
            private int ticks = 0;

            @Override
            public void run() {

                if(!plugin.isEnabled()){
                    cancel();
                    return;
                }

                if (ticks % 36000 == 0) { // Cada media hora
                    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    if (!onlinePlayers.isEmpty()) {
                        Random random = new Random();
                        Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));
                        String worldName = randomPlayer.getWorld().getName();

                        if (worldName.equalsIgnoreCase("world") || worldName.equalsIgnoreCase("recursos")) {
                            randomPlayer.sendMessage(ChatColor.RED + "¡Ha aparecido una horda a tus alrededores, ten cuidado!");

                            Location spawnLoc = getValidHordeSpawnLocation(randomPlayer);
                            if (spawnLoc != null) {
                                camp = new Camp();
                                camp.startHorde(spawnLoc, plugin);
                            } else {
                                randomPlayer.sendMessage(ChatColor.YELLOW + "No se encontró una ubicación válida para la horda, te salvaste...");
                            }
                        }else{
                            randomPlayer.sendMessage("No estas en un mundo valido para la horda, te salvaste...");
                        }
                    }else{
                        Bukkit.getLogger().info("No hay jugadores online");
                    }
                }
                ticks++;
            }
        };

        runnable.runTaskTimer(this, 0L, 1L); // Ejecuta cada tick
    }


    // Método auxiliar para obtener una ubicación válida para iniciar la horda
    private Location getValidHordeSpawnLocation(Player player) {
        Location base = player.getLocation();
        World world = player.getWorld();
        Random random = new Random();
        int attempts = 0;

        // Intentamos hasta 100 veces encontrar una ubicación que cumpla con las condiciones
        while (attempts < 100) {
            // Generar un ángulo aleatorio y una distancia aleatoria entre 20 y 50 bloques
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = 40 + random.nextDouble() * 30;
            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;
            Location candidate = base.clone().add(offsetX, 0, offsetZ);

            // Ajustar la altura al bloque más alto en esa posición y añadir 1 para que no esté dentro del bloque
            candidate.setY(world.getHighestBlockYAt(candidate) + 1);

            // Verificar que el chunk esté cargado
            if (!candidate.getChunk().isLoaded()) {
                attempts++;
                continue;
            }

            // Comprobar que no haya ningún aldeano cerca (dentro de 20 bloques)
            boolean nearVillager = world.getNearbyEntities(candidate, 20, 20, 20)
                    .stream()
                    .anyMatch(e -> e instanceof Villager);
            if (nearVillager) {
                attempts++;
                continue;
            }

            // Si se cumplen todas las condiciones, devolvemos la ubicación candidata
            return candidate;
        }
        // Si no se encontró ninguna ubicación válida tras 100 intentos, devolvemos null
        return null;
    }



    public CommandManager getCommandManager() {
        return commandManager;
    }
}
