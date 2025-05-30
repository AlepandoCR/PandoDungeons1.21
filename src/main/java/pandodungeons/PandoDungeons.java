package pandodungeons;

import battlePass.BattlePassEventHandler;
import battlePass.premium.PremiumRewardManager;
import battlePass.regular.DefaultRewardManager;
import controlledEntities.modeled.pets.PetCommand;
import controlledEntities.modeled.pets.PetGachaCommand;
import controlledEntities.modeled.pets.PetsListener;
import controlledEntities.modeled.pets.PetsManager;
import displays.DisplayManager;
import gambling.commands.GambleCommand;
import gambling.core.GamblingManager;
import gambling.listener.HorseRaceInitListener;
import gambling.services.PlayerBalanceManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
import pandoClass.quests.MissionListener;
import pandoClass.quests.MissionManager;
import pandoClass.quests.QuestCommand;
import pandoToros.game.ToroStatManager;
import pandoToros.listeners.ToroGameListener;
import pandoToros.utils.RedondelCommand;
import pandodungeons.CustomEntities.Ball.BallEventHandler;
import pandodungeons.Utils.PlayerPartyList;
import pandodungeons.commands.Management.CommandManager;
import pandodungeons.Listeners.PlayerEventListener;
import pandodungeons.Game.PlayerStatsManager;
import pandodungeons.Utils.LocationUtils;
import pandodungeons.Utils.StructureUtils;
import pandodungeons.commands.game.PartyCommand;
import tcg.cards.engine.CardFactory;
import tcg.cards.engine.CardManager;
import tcg.cards.engine.CardReader;
import tcg.cards.skills.engine.SkillManager;
import tcg.commands.CardCommand;
import tcg.listeners.CardListener;
import textures.TextureCommand;


import java.io.File;
import java.io.IOException;
import java.util.*;


import static controlledEntities.ControlledEntity.startMonitoringControlledEntities;
import static controlledEntities.modeled.pets.PetNameTagHandler.startGlobalUpdater;
import static pandoClass.classes.mage.skills.TimeRewindSkill.startTracking;
import static pandoClass.classes.mage.skills.orb.Orb.handleStands;
import static pandoClass.gachaPon.GachaHolo.*;
import static pandodungeons.Utils.CompanionUtils.loadAllCompanions;
import static pandodungeons.Utils.ItemUtils.*;

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
    public SkillManager skillManager;
    public GamblingManager gamblingManager;
    private CardFactory cardFactory;
    private CardManager cardManager;
    private CardReader cardReader;
    private CardCommand cardCommand;

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
        gamblingManager = new GamblingManager(this, PlayerBalanceManager.INSTANCE);
        skillManager = new SkillManager(this);
        removeAllGachaHolosOnStart(this);
        playerAndClassAssosiation = rpgPlayerDataManager.getRPGPlayerMap();
        cardFactory = new CardFactory(this);
        cardManager = new CardManager(this);
        cardCommand = new CardCommand(this);
        cardReader = new CardReader(this);
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

    public CardFactory getCardFactory() {
        return cardFactory;
    }

    public CardReader getCardReader() {
        return cardReader;
    }

    public CardManager getCardManager() {
        return cardManager;
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
        this.getCommand("encargo").setExecutor(new QuestCommand(this));
        this.getCommand("pagar").setExecutor(new PayCommand(this));
        this.getCommand("petoken").setExecutor(new PetGachaCommand(this));
        this.getCommand("apostar").setExecutor(new GambleCommand(this, gamblingManager));
        this.getCommand("gamblingadmin").setExecutor(new GambleCommand(this, gamblingManager));
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
        getServer().getPluginManager().registerEvents(new HorseRaceInitListener(this),this);
        getServer().getPluginManager().registerEvents(new CardListener(this), this);
    }

    @Override
    public void onDisable() {
        displayManager.removeAllDisplays();
        runnable.cancel();
        removeOrbs();
        handleStands(PandoDungeons.this);
        removeAllGachaHolos();
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

    public GamblingManager getGamblingManager() {
        return gamblingManager;
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
            public void onPlayerJoin(PlayerJoinEvent event) {
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

    public SkillManager getSkillManager() {
        return skillManager;
    }

    // Método auxiliar para obtener una ubicación válida para iniciar la horda
    private Location getValidHordeSpawnLocation(Player player) {
        Location base = player.getLocation();
        World world = player.getWorld();
        Random random = new Random();
        int attempts = 0;

        while (attempts < 10) {

            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = 40 + random.nextDouble() * 30;
            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;
            Location candidate = base.clone().add(offsetX, 0, offsetZ);

            candidate.setY(world.getHighestBlockYAt(candidate) + 1);

            if (!candidate.getChunk().isLoaded()) {
                attempts++;
                continue;
            }

            boolean nearVillager = world.getNearbyEntities(candidate, 20, 20, 20)
                    .stream()
                    .anyMatch(e -> e instanceof Villager);
            if (nearVillager) {
                attempts++;
                continue;
            }

            return candidate;
        }
        return null;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
