package pandodungeons.pandodungeons;

import battlePass.BattlePassEventHandler;
import battlePass.premium.PremiumRewardManager;
import battlePass.regular.DefaultRewardManager;
import controlledEntities.modeled.pets.PetCommand;
import controlledEntities.modeled.pets.PetGachaCommand;
import controlledEntities.modeled.pets.PetsListener;
import controlledEntities.modeled.pets.PetsManager;
import displays.DisplayManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.*;
import pandoClass.campsListener.CampsListener;
import pandoClass.classes.ClassCommand;
import pandoClass.classes.CobrarCommand;
import pandoClass.classes.TopCoinsCommand;
import pandoClass.classes.farmer.skils.GolemHandler;
import pandoClass.classes.mage.skills.orb.OrbsManager;
import pandoClass.files.RPGPlayerDataManager;
import pandoClass.gachaPon.GachaCommand;
import pandoClass.gachaPon.prizes.PrizeListener;
import pandoClass.gachaPon.prizes.PrizeManager;
import pandoClass.gambling.GambleCommand;
import pandoClass.gambling.GamblingSession;
import pandoClass.quests.MissionListener;
import pandoClass.quests.MissionManager;
import pandoClass.quests.QuestCommand;
import pandoToros.game.ToroStatManager;
import pandoToros.listeners.ToroGameListener;
import pandoToros.utils.RedondelCommand;
import pandodungeons.pandodungeons.CustomEntities.Ball.BallEventHandler;
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
import java.net.MalformedURLException;
import java.util.*;


import static controlledEntities.ControlledEntity.startMonitoringControlledEntities;
import static controlledEntities.modeled.pets.PetNameTagHandler.startGlobalUpdater;
import static pandoClass.classes.mage.skills.TimeRewindSkill.startTracking;
import static pandoClass.classes.mage.skills.orb.Orb.handleStands;
import static pandoClass.gachaPon.GachaHolo.*;
import static pandodungeons.pandodungeons.Utils.CompanionUtils.loadAllCompanions;
import static pandodungeons.pandodungeons.Utils.ItemUtils.*;

public final class PandoDungeons extends JavaPlugin {
    private CommandManager commandManager;
    public PlayerPartyList playerPartyList = new PlayerPartyList();
    public Map<Player, ClassRPG> playerAndClassAssosiation = new HashMap<>();
    public PrizeManager prizeManager = new PrizeManager(this);
    private BukkitRunnable runnable;
    public MissionManager missionManager = new MissionManager();
    public DefaultRewardManager defaultRewardManager;
    public PremiumRewardManager premiumRewardManager;
    public OrbsManager orbsManager;
    public RPGPlayerDataManager rpgPlayerDataManager;
    public InitMenu initMenu;
    public PetsManager petsManager;
    public RpgManager rpgManager;
    public DisplayManager displayManager;

    public GamblingSession gamblingSession;

    @Override
    public void onEnable() {
        handleStandsHere();
        startGlobalUpdater(this);
        rpgManager = new RpgManager(this);
        petsManager = new PetsManager(this);
        rpgPlayerDataManager = new RPGPlayerDataManager(this);
        startTracking(this);
        initMenu = new InitMenu(this);
        orbsManager = new OrbsManager(this);
        defaultRewardManager = new DefaultRewardManager(this);
        premiumRewardManager = new PremiumRewardManager(this);
        premiumRewardManager.InitRewards();
        startGamble(this);
        removeAllGachaHolosOnStart(this);
        playerAndClassAssosiation = rpgPlayerDataManager.getRPGPlayerMap();
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


        PrizeListener prizeListener = new PrizeListener(this);

        registerListeners(prizeListener);

        registerCommands();

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

        prizeListener.tickChoneteStacks();

        startMonitoringControlledEntities(this);

        loadRecipes();

        unlockRecipes();

        loadAllCompanions();

        loadData(playerDataFolder);

        displayManager = new DisplayManager(this);
    }

    private void loadData(File playerDataFolder) {
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

    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    private void unlockRecipes() {
        unlockRecipeForAllPlayers(getPufferFishCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getOsoCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getArmadilloCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getBreezeCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getAllayCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getSnifferCompanionCustomRecipe());
        unlockRecipeForAllPlayers(getCopperGumRecipe());
        unlockRecipeForAllPlayers(getSoccerBallRecipe());
    }

    private static void loadRecipes() {
        breezeCompanionCustomRecipe();
        armadilloCompanionCustomRecipe();
        allayCompanionCustomRecipe();
        osoCompanionCustomRecipe();
        snifferCompanionCustomRecipe();
        pufferFishCompanionCustomRecipe();
        copperGumRecipe();
        soccerBallRecipe();
    }

    private void registerCommands() {
        this.getCommand("topmonedas").setExecutor(new TopCoinsCommand(this));
        this.getCommand("cobrar").setExecutor(new CobrarCommand(this));
        this.getCommand("mascotas").setExecutor(new PetCommand(this));
        this.getCommand("gachatoken").setExecutor(new GachaCommand(this));
        this.getCommand("texturas").setExecutor(new TextureCommand(this));
        this.getCommand("dungeons").setExecutor(new CommandManager(this));
        this.getCommand("redondel").setExecutor(new RedondelCommand(this));
        this.getCommand("party").setExecutor(new PartyCommand(this));
        this.getCommand("stats").setExecutor(new ClassCommand(this));
        this.getCommand("bet").setExecutor(new GambleCommand(this));
        this.getCommand("encargo").setExecutor(new QuestCommand(this));
        this.getCommand("pagar").setExecutor(new PayCommand(this));
        this.getCommand("petoken").setExecutor(new PetGachaCommand(this));
    }

    private void registerListeners(PrizeListener prizeListener) {
        getServer().getPluginManager().registerEvents(new BattlePassEventHandler(this),this);
        getServer().getPluginManager().registerEvents(new GolemHandler(this),this);
        getServer().getPluginManager().registerEvents(new ExpandableClassMenuListener(this),this);
        getServer().getPluginManager().registerEvents(prizeListener, this);
        getServer().getPluginManager().registerEvents(new CampsListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);
        getServer().getPluginManager().registerEvents(new ToroGameListener(), this);
        getServer().getPluginManager().registerEvents(new BallEventHandler(this), this);
        getServer().getPluginManager().registerEvents(new RPGListener(this), this);
        getServer().getPluginManager().registerEvents(new MissionListener(this),this);
        getServer().getPluginManager().registerEvents(new PetsListener(this),this);
    }


    private void startGamble(PandoDungeons plugin) {
        new BukkitRunnable(){
            @Override
            public void run() {
                World spawnWorld = Bukkit.getWorld("spawn");
                if (spawnWorld == null) {
                    getLogger().severe("El mundo 'spawn' no existe. No se puede iniciar la sesión de apuestas.");
                    return;
                }
                Location start = new Location(spawnWorld, 43.5,73,276.5);
                Location end = new Location(spawnWorld, 37.5,73,276.5);
                try {
                    gamblingSession = new GamblingSession(plugin, start,  end);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskLater(this,200);
    }

    @Override
    public void onDisable() {
        displayManager.removeAllDisplays();
        runnable.cancel();
        removeOrbs();
        handleStands(PandoDungeons.this);
        removeAllGachaHolos();
        if(this.gamblingSession != null){
            gamblingSession.removeHorses();
        }
        removePlayersFromDungeons();
        removeDungeons();
        petsManager.destroyAllPets();
    }

    private void removePlayersFromDungeons() {
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


    private void handleStandsHere(){
        new BukkitRunnable(){

            @Override
            public void run() {
                handleStands(PandoDungeons.this);
            }
        }.runTaskTimer(this,1,200);
    }

    private void removeDungeons() {
        for(String world : LocationUtils.getAllDungeonWorlds()){
            if(world == null){
                break;
            }
            StructureUtils.removeDungeon(world,this);
        }
    }

    private void removeOrbs() {
        if (orbsManager != null && orbsManager.getOrbs() != null) {
            // Crear una copia de la colección para evitar ConcurrentModificationException
            new HashMap<>(orbsManager.getOrbs()).forEach((player, orb) -> {
                if (orb != null) {
                    orb.remove();  // Eliminar visualmente el Orb y su ArmorStand
                    orbsManager.removeOrb(player, orb); // Remover del mapa original
                }
            });
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

                if (ticks % 18000 == 0) { // Cada 15 mins
                    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    if (!onlinePlayers.isEmpty()) {
                        Random random = new Random();
                        Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));
                        String worldName = randomPlayer.getWorld().getName();

                        if(plugin.rpgManager.getPlayer(randomPlayer).getLevel() < 25) return;

                        if (worldName.equalsIgnoreCase("world") || worldName.equalsIgnoreCase("recursos") || worldName.equalsIgnoreCase("world_the_end") || worldName.equalsIgnoreCase("world_nether") || worldName.equalsIgnoreCase("masmo")) {
                            randomPlayer.sendMessage(ChatColor.RED + "¡Ha aparecido una horda a tus alrededores, ten cuidado!");

                            Location spawnLoc = getValidHordeSpawnLocation(randomPlayer);
                            if (spawnLoc != null) {
                                Camp horde = new Camp(plugin);
                                horde.startHorde(spawnLoc, plugin);
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

        while (attempts < 10) {
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
