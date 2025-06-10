package pandoClass;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.floodgate.api.FloodgateApi;
import pandoClass.classes.archer.Archer;
import pandoClass.classes.assasin.Assasin;
import pandoClass.classes.farmer.Farmer;
import pandoClass.classes.mage.Mage;
import pandoClass.classes.tank.Tank;
import pandoClass.gachaPon.GachaHolo;
import pandodungeons.PandoDungeons;

import javax.annotation.Nullable;
import java.io.File;
import java.math.MathContext;
import java.util.*;

import static pandoClass.RPGListener.magicShieldPlayers;
import static pandoClass.classes.archer.skills.ArrowExplotionSkill.explosiveAmmo;
import static pandoClass.classes.archer.skills.DoubleJumSkill.doubleJumping;
import static pandoClass.classes.archer.skills.SaveAmmoSkill.playersSavingAmmo;
import static pandoClass.classes.assasin.skills.LifeStealSkill.lifeStealingPlayers;
import static pandoClass.classes.assasin.skills.SilentStepSkill.silencedPlayers;
import static pandoClass.classes.farmer.skils.ExtraHarvestSkill.removeFarmingPlayer;
import static pandoClass.classes.farmer.skils.GolemSkill.removeGolemPlayer;
import static pandoClass.classes.farmer.skils.TameSkill.removeTamingPlayer;


public class RPGPlayer {
    private int level;
    private int exp;
    private int campsDefeated;
    private Long coins;
    private int orbs;
    private UUID player;
    private int firstSkilLvl;
    private int secondSkilLvl;
    private int thirdSkillLvl;
    private String classKey = null;
    private int orbProgress;
    private String playerName = "";
    private int gachaopen;
    private boolean texturePack = false;
    private boolean hasChosenTextures = false;
    private boolean isChoosing = false;
    private String racoonPetName  = "";
    private String minerPetName  = "";
    private String sakuraPetName  = "";
    private String jojoPetName = "";
    private String grayWolfPetName = "";
    private String spectralWolfPetName = "";

    private final transient PandoDungeons plugin;

    public static Map<Player, BossBar> activeBossBars = new HashMap<>();

    private static final Map<Player, BukkitRunnable> expBarTasks = new HashMap<>();

    public RPGPlayer(Player player, PandoDungeons plugin) {

        this.plugin = plugin;

        if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) return;

        this.player = player.getUniqueId();
        RPGPlayer loaded = plugin.rpgPlayerDataManager.load(getPlayer());

        if (loaded != null) {
            copyFrom(loaded);
            plugin.rpgManager.addPlayer(this);
            update();
        } else {
            if (getPlayerDataFile().exists()) {
                // El archivo existe pero está corrupto
                plugin.getLogger().severe("El archivo de datos del jugador " + getPlayer() + " está corrupto. No se pudo cargar.");
            }else{
                defaults();
                plugin.getLogger().info("El archivo de datos del jugador " + getPlayer() + " se ha creado por primera vez.");
                update();
            }
        }
    }

    public RPGPlayer(UUID player, PandoDungeons plugin) {
        this.player = player;
        this.plugin = plugin;
        RPGPlayer loaded = plugin.rpgPlayerDataManager.load(getPlayer());
        if (loaded != null) {
            copyFrom(loaded);
        } else {
            if (getPlayerDataFile().exists()) {
                plugin.getLogger().severe("El archivo de datos del jugador " + getPlayer() + " está corrupto. No se pudo cargar.");
            }
        }
    }

    private void copyFrom(RPGPlayer other) {
        this.orbProgress = other.orbProgress;
        this.orbs = other.orbs;
        this.level = other.level;
        this.exp = other.exp;
        this.campsDefeated = other.campsDefeated;
        this.coins = other.coins;
        this.firstSkilLvl = other.firstSkilLvl;
        this.secondSkilLvl = other.secondSkilLvl;
        this.thirdSkillLvl = other.thirdSkillLvl;
        this.classKey = other.classKey;
        this.playerName = other.playerName;
        this.gachaopen = other.gachaopen;
        this.texturePack = other.texturePack;
        this.hasChosenTextures = other.hasChosenTextures;
        this.isChoosing = other.isChoosing;
        this.racoonPetName  = other.racoonPetName;
        this.minerPetName  = other.minerPetName;
        this.sakuraPetName  = other.sakuraPetName;
        this.jojoPetName  = other.jojoPetName;
        this.grayWolfPetName = other.grayWolfPetName;
        this.spectralWolfPetName = other.spectralWolfPetName;
    }

    private void defaults() {
        this.orbProgress = 0;
        this.orbs = 0;
        this.level = 1;
        this.exp = 0;
        this.campsDefeated = 0;
        this.coins = 0L;
        this.firstSkilLvl = 1;
        this.secondSkilLvl = 1;
        this.thirdSkillLvl = 1;
        this.classKey = "";
        this.playerName = "";
        this.gachaopen = 0;
        this.texturePack = false;
        this.hasChosenTextures = false;
        this.isChoosing = false;
        this.racoonPetName  = "";
        this.minerPetName  = "";
        this.sakuraPetName  = "";
        this.jojoPetName  = "";
        this.grayWolfPetName = "";
        this.spectralWolfPetName = "";
    }

    public void setSpectralWolfPetName(String spectralWolfPetName) {
        this.spectralWolfPetName = spectralWolfPetName;
    }

    public String getSpectralWolfPetName() {
        return spectralWolfPetName;
    }

    public void setGrayWolfPetName(String grayWolfPetName) {
        this.grayWolfPetName = grayWolfPetName;
    }

    public String getGrayWolfPetName() {
        return grayWolfPetName;
    }

    public File getPlayerDataFile(){
        return new File(plugin.rpgPlayerDataManager.getDataFolder(), getPlayer().toString() + ".json");
    }

    public void addLevel(int level){
        this.level += level;
        save();
        update();
    }

    public void addOrb(int orb){
        this.orbs += orb;
        save();
        update();
    }

    public void addCoins(Long toAdd){
        coins += toAdd;
        save();
        update();
    }

    public boolean isTexturePack() {
        return texturePack;
    }

    public void setTexturePack(boolean texturePack) {
        this.texturePack = texturePack;
        save();
        update();
    }

    public void addExp(int toAdd) {
        exp += toAdd;

        expBar();

        while (exp >= calculateExpForNextLvl()) { // Usa while para permitir subir varios niveles si es necesario
            exp -= calculateExpForNextLvl(); // Resta en lugar de resetear a 0, para conservar el exceso de exp
            level++;
            orbs++; // Aumenta un orbe cada nivel
        }

        save();
        update();
    }

    public void resetGachaOpens(){
        gachaopen = 0;
        save();
        update();
    }

    public void addGachaOpen(){
        gachaopen++;
        save();
        new GachaHolo(plugin).showHolo(getPlayer());
        update();
    }

    public int getGachaopen(){
        return gachaopen;
    }

    public void expBar() {
        Player player = getPlayer();

        // Cancela el Runnable anterior si existe
        if (expBarTasks.containsKey(player)) {
            expBarTasks.get(player).cancel();
        }

        // Elimina la barra de experiencia anterior
        removeExpBossBar(player);

        // Muestra la nueva barra de experiencia
        showExpBossBar(player);

        // Crea y programa un nuevo BukkitRunnable
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                removeExpBossBar(player);
                expBarTasks.remove(player); // Elimina la referencia después de ejecutarse
            }
        };

        task.runTaskLater(plugin, 60L);
        expBarTasks.put(player, task); // Almacena la tarea en el mapa
    }

    public int calculateExpForNextLvl() {
        return (int) (500 + (50 * Math.pow(level, 1.2)));
    }

    public void exchangeLevelsForCoins(int levelsToExchange) {
        if (levelsToExchange <= 1 || levelsToExchange >= level) {
            getPlayer().sendMessage(ChatColor.RED + "No tienes suficientes niveles para intercambiar.");
            return;
        }

        resetOrbs();

        long totalCoins = 0;

        // Calculamos el costo en monedas basado en la experiencia requerida para cada nivel intercambiado
        for (int i = 0; i < levelsToExchange; i++) {
            int expRequired = calculateExpForNextLvl();
            totalCoins += expRequired / 100;
            if(getLevel() > 0 && getOrbs() >= 0){
                addLevel(-1); // Restamos el nivel
                addOrb(-1);
                addExp(-getExp()); // Reiniciamos la experiencia actual del nivel
            }
        }

        addCoins(totalCoins);
        save();
        update();

        var player = getPlayer();

        if(player == null) return;

        player.sendMessage(ChatColor.GOLD + "Has intercambiado " + levelsToExchange + " niveles y orbes por " + totalCoins + " monedas.");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Se han extraido los orbes de las habilidades por el intercambio");
    }

    public void animateAndApplyLevelExchange(int amount){

        Enderman enderman = spawnEndermanNoAI(getPlayer());

        GameMode previousGameMode = getPlayer().getGameMode();

        // Cambia al jugador a modo espectador y lo hace ver desde el Enderman
        getPlayer().setGameMode(GameMode.SPECTATOR);
        getPlayer().setSpectatorTarget(enderman);

        new BukkitRunnable(){

            @Override
            public void run() {
                if (getPlayer().isOnline() && getPlayer().getGameMode() == GameMode.SPECTATOR) {
                    getPlayer().setGameMode(previousGameMode);
                    exchangeLevelsForCoins(amount);
                    getPlayer().teleport(enderman);
                    enderman.remove();
                }
            }
        }.runTaskLater(plugin,60);
    }

    public Enderman spawnEndermanNoAI(Player player) {
        Location location = player.getLocation(); // Obtiene la ubicación del jugador
        Enderman enderman = (Enderman) player.getWorld().spawnEntity(location, EntityType.ENDERMAN); // Crea el Enderman
        // Desactiva la IA del Enderman
        enderman.setAI(false);
        enderman.setSilent(true);
        enderman.setInvulnerable(true);
        enderman.setInvisible(true);

        return enderman;
    }

    public void showExpBossBar(Player player) {
        if (activeBossBars.containsKey(player)) {
            activeBossBars.get(player).removeAll();
        }

        RPGPlayer rpgPlayer = plugin.rpgManager.getPlayer(player);
        int currentExp = rpgPlayer.getExp();
        int requiredExp = rpgPlayer.calculateExpForNextLvl();

        // Calcula el progreso y lo limita a un máximo de 1.0
        double progress = (double) currentExp / requiredExp;
        progress = Math.max(0.0, Math.min(progress, 1.0));

        // Crear la BossBar
        BossBar bossBar = plugin.getServer().createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_20);

        // Elimina BossBars previas para evitar acumulaciones
        for (net.kyori.adventure.bossbar.BossBar bar : player.activeBossBars()) {
            bar.removeViewer(player);
        }

        // Establecer el título con el formato "exp/expRequerida"
        String title = currentExp + "/" + requiredExp;
        bossBar.setTitle(title);

        // Establecer el progreso en la barra (valor entre 0.0 y 1.0)
        bossBar.setProgress(progress);

        // Añadir al jugador
        bossBar.addPlayer(player);

        // Guardar la nueva BossBar en el mapa
        activeBossBars.put(player, bossBar);
    }


    public void removeExpBossBar(Player player) {
        if (activeBossBars.containsKey(player)) {
            activeBossBars.get(player).removeAll();
            activeBossBars.remove(player);
        }
    }

    public void changeClass(String key) {
        getPlayer().setMaxHealth(20.0);
        getPlayer().setWalkSpeed(0.2f);

        doubleJumping.remove(getPlayer());
        explosiveAmmo.remove(getPlayer());
        magicShieldPlayers.remove(getPlayer());
        playersSavingAmmo.remove(getPlayer());
        lifeStealingPlayers.remove(getPlayer());
        silencedPlayers.remove(getPlayer());
        removeGolemPlayer(getPlayer());
        removeFarmingPlayer(getPlayer());
        removeTamingPlayer(getPlayer());

        ClassRPG classToSet = getClassFromKey(key);
        if (classToSet == null) return;

        this.classKey = key;
        save(); // IMPORTANTE: Guardar después de cambiar la clase
        update();
    }


    private ClassRPG getClassFromKey(String classKey){
        return switch (classKey) {
            case "ArcherClass" -> new Archer(this,plugin);
            case "TankClass" -> new Tank(this,plugin);
            case "AssassinClass" -> new Assasin(this,plugin);
            case "FarmerClass" -> new Farmer(this,plugin);
            case "MageClass" -> new Mage(this,plugin);
            case null, default -> null;
        };
    }

    public int getOrbProgress() {
        return orbProgress;
    }

    public int getOrbs() {
        return orbs;
    }

    public void setOrbs(int orbs) {
        this.orbs = orbs;
        save();
        update();
    }

    public String getEmojiForLevel() {
        return switch (level / 25) { // Divide el nivel entre 20 para agrupar en rangos
            case 0 -> "🔰 " + ChatColor.GREEN + ChatColor.BOLD + level + " ";
            case 1 -> "💩 " + ChatColor.YELLOW + ChatColor.BOLD + level + " ";
            case 2 -> "🔥 " + ChatColor.RED + ChatColor.BOLD + level + " ";
            default -> "⚡ " + ChatColor.AQUA + ChatColor.BOLD + level + " ";
        };
    }

    public String getEmojiForLevel(int lvl) {
        return switch (lvl / 25) { // Divide el nivel entre 20 para agrupar en rangos
            case 0 -> "🔰 " + ChatColor.GREEN + ChatColor.BOLD + lvl + " ";
            case 1 -> "💩 " + ChatColor.YELLOW + ChatColor.BOLD + lvl + " ";
            case 2 -> "🔥 " + ChatColor.RED + ChatColor.BOLD + lvl + " ";
            default -> "⚡ " + ChatColor.AQUA + ChatColor.BOLD + lvl + " ";
        };
    }



    public void setPlayerTag(Player player) {
        // Obtén el displayName actual
        String currentName = player.getDisplayName();

        // Elimina el prefijo si ya existe
        currentName = currentName.replaceFirst(getEmojiForLevel(), "");

        int currentLvl = level;

        int checkBackwards = level;
        while (50 + level >= checkBackwards){
            currentName = currentName.replaceFirst(getEmojiForLevel(checkBackwards),"");
            checkBackwards++;
        }

        while (currentLvl >= 1){
            currentName = currentName.replaceFirst(getEmojiForLevel(currentLvl),"");
            currentLvl--;
        }

        // Construye el nuevo prefijo con la lógica actual (getEmojiForLevel() ya incluye el emoji, los códigos de color y el nivel)
        String newPrefix = ChatColor.WHITE + getEmojiForLevel() + ChatColor.RESET;

        // Nuevo nombre: nuevo prefijo + espacio + nombre base (sin prefijo anterior)
        String newName = newPrefix + currentName;

        // Aplica el nuevo nombre
        player.setCustomName(newName);
        player.setDisplayName(newName);
    }



    private void update() {
        Player player = getPlayer();

        if (player == null || !player.isOnline()) {
            return;
        }

        // Si ya existe un objeto asociado, cancelar su runnable
        if (plugin.playerAndClassAssosiation.containsKey(player) && plugin.playerAndClassAssosiation.get(player) != null) {
            plugin.playerAndClassAssosiation.get(player).cancel();
            plugin.playerAndClassAssosiation.remove(player);
        }

        // Crear la nueva instancia a partir del classKey
        var updatedClass = getClassFromKey(classKey);

        if(updatedClass == null) return;

        // Actualizar el mapa y ejecutar triggerSkills
        setPlayerTag(player);
        plugin.playerAndClassAssosiation.put(player, updatedClass);
        plugin.playerAndClassAssosiation.get(player).triggerSkills();
    }

    public void handleTexturePack(Player player) {
        if (!hasChosenTextures) {
            if (isChoosing) {
                return; // Evita llamar de nuevo si ya está en proceso
            }

            isChoosing = true;
            player.openInventory(createChoosMenu());
            save();

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.getOpenInventory().getTitle().contains("texturepack")) {
                        return;
                    }

                    if(plugin.rpgManager.getPlayer(player).hasChosenTextures){
                        this.cancel();
                        return;
                    }

                    isChoosing = true;
                    player.openInventory(createChoosMenu());
                    isChoosing = false;
                    save();
                    update();
                }
            }.runTaskTimer(plugin, 0,40); // Pequeño retraso para evitar recursión inmediata
        }
    }

    public void resetOrbs() {
        int skillLvls = (firstSkilLvl + secondSkilLvl + thirdSkillLvl) - 3; // Restamos los niveles base (1-1-1)

        int orbsSpent = skillLvls * 4; // Calculamos los orbes gastados en total

        // Reiniciar habilidades a nivel 1
        firstSkilLvl = 1;
        secondSkilLvl = 1;
        thirdSkillLvl = 1;

        // Devolver solo los orbes gastados
        orbs += orbsSpent;

        save();
        update();
    }



    public void setHasChosenTextures(boolean hasChosenTextures) {
        this.hasChosenTextures = hasChosenTextures;
        save();
        update();
    }

    // Método que crea el menú
    public Inventory createChoosMenu(){
        String menuTitle;


        Inventory menu = Bukkit.createInventory(null, 9, ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "¿Deseas el texturepack?");

        ItemStack head1 = new ItemStack(Material.GREEN_CONCRETE);
        ItemStack head2 = new ItemStack(Material.RED_CONCRETE);



        ItemMeta meta = head2.getItemMeta();

        meta.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD  + "NO");

        meta.setLore(List.of(ChatColor.RED + "No afecta el gameplay", ChatColor.DARK_RED + "Tendrás problemas al ver entidades custom y items custom",ChatColor.GREEN + "Podrás activarlo en cualquier momento con: " + ChatColor.GOLD + "/texturas mantener"));

        ItemMeta meta2 = head1.getItemMeta();

        meta2.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD  + "SI");

        meta2.setLore(List.of(ChatColor.GREEN + "No afecta el gameplay", ChatColor.GOLD.toString() + ChatColor.BOLD + "Podrás tener la experiencia que se quiere brindar", ChatColor.RED + "Podrás eliminarlo en cualquier momento con: " + ChatColor.GOLD + "/texturas eliminar"));


        head1.setItemMeta(meta2);
        head2.setItemMeta(meta);

        // Asignar las cabezas a las posiciones 2, 4 y 6
        menu.setItem(2, head1);
        menu.setItem(6, head2);

        return menu;
    }

    public String getMinerPetName() {
        return minerPetName;
    }

    public String getRacoonPetName() {
        return racoonPetName;
    }

    public String getSakuraPetName() {
        return sakuraPetName;
    }

    public void setMinerPetName(String minerPetName) {
        this.minerPetName = minerPetName;
        save();
        update();
    }

    public void setRacoonPetName(String racoonPetName) {
        this.racoonPetName = racoonPetName;
        save();
        update();
    }

    public void setSakuraPetName(String sakuraPetName) {
        this.sakuraPetName = sakuraPetName;
        save();
        update();
    }

    public String getClassKey() {

        return classKey;
    }

    public String getJojoPetName() {
        return jojoPetName;
    }

    public void setJojoPetName(String jojoPetName) {
        this.jojoPetName = jojoPetName;
        save();
        update();
    }

    public void setClassKey(String classKey) {
        this.classKey = classKey;
        changeClass(classKey);
        save();
    }

    public void removeCoins(Long toRemove){
        coins -= toRemove;
        coins = Math.max(coins,0);
        save();
        update();
    }

    public ClassRPG getClassRpg(){
        return getClassFromKey(classKey);
    }

    public int getLevel() {

        return level;
    }

    public void setLevel(int level) {

        this.level = level;
        save();
    }

    public int getExp() {

        return exp;
    }

    public void setExp(int exp) {

        this.exp = exp;
        save();
    }

    public int getCampsDefeated() {

        return campsDefeated;
    }

    public void setCampsDefeated(int campsDefeated) {

        this.campsDefeated = campsDefeated;
        save();
    }

    public Long getCoins() {

        return coins;
    }

    public void setCoins(Long coins) {

        this.coins = coins;
        save();
    }

    public UUID getPlayerUUID() {
        return player;
    }

    public void setPlayerUUID(UUID player) {

        this.player = player;
        save();
    }

    public void setPlayer(Player player) {

        this.player = player.getUniqueId();
        save();
    }

    @Nullable
    public Player getPlayer() {
        if(player != null)return Bukkit.getPlayer(player);
        return null;
    }

    public int getFirstSkilLvl() {

        return firstSkilLvl;
    }

    public void setFirstSkilLvl(int firstSkilLvl) {

        this.firstSkilLvl = firstSkilLvl;
        save();
    }

    public int getSecondSkilLvl() {

        return secondSkilLvl;
    }

    public void setSecondSkilLvl(int secondSkilLvl) {

        this.secondSkilLvl = secondSkilLvl;
        save();
    }

    public int getThirdSkillLvl() {

        return thirdSkillLvl;
    }

    public void addCamp(int toAdd){
        campsDefeated += toAdd;
        save();
        update();
    }

    public void setThirdSkillLvl(int thirdSkillLvl) {

        this.thirdSkillLvl = thirdSkillLvl;
        save();
    }
    public String toDecoratedString(String player) {
        ClassRPG classRPG = getClassRpg();
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + player + ChatColor.DARK_PURPLE + " Info§r\n" +
                "§eLevel: §b" + level + "§r\n" +
                "§eExp: §b" + exp + "§r\n" +
                "☃: " + ChatColor.GOLD + coins + "§r\n" +
                "§eOrbes de mejora: §b" + orbs + "§r\n" +
                "§e" + classRPG.getFirstSkill().getName() + ": §b" + firstSkilLvl + "§r\n" +
                "§e"  + classRPG.getSecondSkill().getName() +  ": §b" + secondSkilLvl + "§r\n" +
                "§e"  + classRPG.getThirdSkill().getName() +  ": §b" + thirdSkillLvl + "§r\n" +
                "§eClass Key: §b" + classRPG.getName() + "§r\n";
    }


    public String toDecoratedString(Player player) {
        ClassRPG classRPG = getClassRpg();
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + player.getName() + ChatColor.DARK_PURPLE + " Info§r\n" +
                "§eLevel: §b" + level + "§r\n" +
                "§eExp: §b" + exp + "§r\n" +
                "☃: " + ChatColor.GOLD + coins + "§r\n" +
                "§eOrbes de mejora: §b" + orbs + "§r\n" +
                "§e" + classRPG.getFirstSkill().getName() + ": §b" + firstSkilLvl + "§r\n" +
                "§e"  + classRPG.getSecondSkill().getName() +  ": §b" + secondSkilLvl + "§r\n" +
                "§e"  + classRPG.getThirdSkill().getName() +  ": §b" + thirdSkillLvl + "§r\n" +
                "§eClass Key: §b" + classRPG.getName() + "§r\n";
    }

    public void save(){
        plugin.rpgPlayerDataManager.save(this);
    }
}
