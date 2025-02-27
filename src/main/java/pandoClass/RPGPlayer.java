package pandoClass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.classes.ClassCommand;
import pandoClass.classes.archer.Archer;
import pandoClass.classes.assasin.Assasin;
import pandoClass.files.RPGPlayerDataManager;
import pandoClass.classes.tank.Tank;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static pandoClass.RPGListener.magicShieldPlayers;
import static pandoClass.classes.archer.skills.ArrowExplotionSkill.explosiveAmmo;
import static pandoClass.classes.archer.skills.DoubleJumSkill.doubleJumping;
import static pandoClass.classes.archer.skills.SaveAmmoSkill.playersSavingAmmo;
import static pandoClass.classes.assasin.skills.LifeStealSkill.lifeStealingPlayers;
import static pandoClass.classes.assasin.skills.SilentStepSkill.silencedPlayers;
import static pandoClass.files.RPGPlayerDataManager.save;

public class RPGPlayer {
    private int level;
    private int exp;
    private int campsDefeated;
    private int coins;
    private int orbs;
    private UUID player;
    private int firstSkilLvl;
    private int secondSkilLvl;
    private int thirdSkillLvl;
    private String classKey = null;
    private int orbProgress;
    private String playerName = "";

    private static final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    public static Map<Player, BossBar> activeBossBars = new HashMap<>();

    private static final Map<Player, BukkitRunnable> expBarTasks = new HashMap<>();

    public RPGPlayer(Player player) {
        this.player = player.getUniqueId();
        RPGPlayer loaded = RPGPlayerDataManager.load(getPlayer());
        if (loaded != null) {
            copyFrom(loaded);
        }
        else{
            defaults();
        }
        update();
    }

    public void addCoins(int toAdd){
        coins += toAdd;
        save(this);
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

        save(this);
        update();
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

    public void showExpBossBar(Player player) {
        if (activeBossBars.containsKey(player)) {
            activeBossBars.get(player).removeAll();
        }

        RPGPlayer rpgPlayer = new RPGPlayer(player);
        int currentExp = rpgPlayer.getExp();
        int requiredExp = rpgPlayer.calculateExpForNextLvl();

        // Calcula el progreso entre 0 y 1, ajustado a los 20 segmentos
        double progress = (double) currentExp / requiredExp;

        // Crear la BossBar
        BossBar bossBar = plugin.getServer().createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_20);

        // Elimina BossBars previas para evitar acumulaciones
        for (net.kyori.adventure.bossbar.BossBar bar : player.activeBossBars()) {
            bar.removeViewer(player);
        }

        // Establecer el título con el formato "exp/expRequerida"
        String title = currentExp + "/" + requiredExp;
        bossBar.setTitle(title);

        // Establecer el progreso en la barra
        bossBar.setProgress(progress);

        // Añadir al jugador
        bossBar.addPlayer(player);

        // Guardar la nueva bossbar en el mapa
        activeBossBars.put(player, bossBar);
    }

    public void removeExpBossBar(Player player) {
        if (activeBossBars.containsKey(player)) {
            activeBossBars.get(player).removeAll();
            activeBossBars.remove(player);
        }
    }

    public RPGPlayer(UUID player) {
        this.player = player;
        RPGPlayer loaded = RPGPlayerDataManager.load(getPlayer());
        if (loaded != null) copyFrom(loaded);
    }

    public RPGPlayer(String player) {
        this.player = UUID.fromString(player);
        RPGPlayer loaded = RPGPlayerDataManager.load(getPlayer());
        if (loaded != null) copyFrom(loaded);
    }

    public void changeClass(String key) {
        getPlayer().setMaxHealth(20.0);
        doubleJumping.remove(getPlayer());
        explosiveAmmo.remove(getPlayer());
        magicShieldPlayers.remove(getPlayer());
        playersSavingAmmo.remove(getPlayer());
        lifeStealingPlayers.remove(getPlayer());
        silencedPlayers.remove(getPlayer());
        getPlayer().setWalkSpeed(0.2f);
        ClassRPG classToSet = getClassFromKey(key);

        if(classToSet == null) return;

        update();
    }

    private ClassRPG getClassFromKey(String classKey){
        return switch (classKey) {
            case "ArcherClass" -> new Archer(this);
            case "TankClass" -> new Tank(this);
            case "AssassinClass" -> new Assasin(this);
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
        save(this);
        update();
    }

    private void update() {
        Player player = getPlayer();

        if (player == null || !player.isOnline()) {
            return;
        }

        // Si ya existe un objeto asociado, cancelar su runnable
        if (plugin.rpgPlayersList.containsKey(player) && plugin.rpgPlayersList.get(player) != null) {
            plugin.rpgPlayersList.get(player).cancel();
            plugin.rpgPlayersList.remove(player);
        }

        // Crear la nueva instancia a partir del classKey
        var updatedClass = getClassFromKey(classKey);

        if(updatedClass == null) return;

        // Actualizar el mapa y ejecutar triggerSkills
        plugin.rpgPlayersList.put(player, updatedClass);
        plugin.rpgPlayersList.get(player).triggerSkills();
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
    }

    private void defaults() {
        this.orbProgress = 0;
        this.orbs = 0;
        this.level = 1;
        this.exp = 0;
        this.campsDefeated = 0;
        this.coins = 0;
        this.firstSkilLvl = 1;
        this.secondSkilLvl = 1;
        this.thirdSkillLvl = 1;
        this.classKey = "";
        this.playerName = "";
    }

    public String getClassKey() {

        return classKey;
    }

    public void setClassKey(String classKey) {
        this.classKey = classKey;
        changeClass(classKey);
        save(this);
    }

    public void removeCoins(int toRemove){
        coins -= toRemove;
    }

    public ClassRPG getClassRpg(){
        return getClassFromKey(classKey);
    }

    public int getLevel() {

        return level;
    }

    public void setLevel(int level) {

        this.level = level;
        save(this);
    }

    public int getExp() {

        return exp;
    }

    public void setExp(int exp) {

        this.exp = exp;
        save(this);
    }

    public int getCampsDefeated() {

        return campsDefeated;
    }

    public void setCampsDefeated(int campsDefeated) {

        this.campsDefeated = campsDefeated;
        save(this);
    }

    public int getCoins() {

        return coins;
    }

    public void setCoins(int coins) {

        this.coins = coins;
        save(this);
    }

    public UUID getPlayerUUID() {
        return player;
    }

    public void setPlayerUUID(UUID player) {

        this.player = player;
        save(this);
    }

    public void setPlayer(Player player) {

        this.player = player.getUniqueId();
        save(this);
    }

    public Player getPlayer() {

        return Bukkit.getPlayer(player);
    }

    public int getFirstSkilLvl() {

        return firstSkilLvl;
    }

    public void setFirstSkilLvl(int firstSkilLvl) {

        this.firstSkilLvl = firstSkilLvl;
        save(this);
    }

    public int getSecondSkilLvl() {

        return secondSkilLvl;
    }

    public void setSecondSkilLvl(int secondSkilLvl) {

        this.secondSkilLvl = secondSkilLvl;
        save(this);
    }

    public int getThirdSkillLvl() {

        return thirdSkillLvl;
    }

    public void setThirdSkillLvl(int thirdSkillLvl) {

        this.thirdSkillLvl = thirdSkillLvl;
        save(this);
    }
    public String toDecoratedString(String player) {
        ClassRPG classRPG = getClassRpg();
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + player + ChatColor.DARK_PURPLE + " Info§r\n" +
                "§eLevel: §b" + level + "§r\n" +
                "§eExp: §b" + exp + "§r\n" +
                //"§eCamps Defeated: §b" + campsDefeated + "§r\n" +
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
                //"§eCamps Defeated: §b" + campsDefeated + "§r\n" +
                "☃: " + ChatColor.GOLD + coins + "§r\n" +
                "§eOrbes de mejora: §b" + orbs + "§r\n" +
                "§e" + classRPG.getFirstSkill().getName() + ": §b" + firstSkilLvl + "§r\n" +
                "§e"  + classRPG.getSecondSkill().getName() +  ": §b" + secondSkilLvl + "§r\n" +
                "§e"  + classRPG.getThirdSkill().getName() +  ": §b" + thirdSkillLvl + "§r\n" +
                "§eClass Key: §b" + classRPG.getName() + "§r\n";
    }


    public void load(){
        RPGPlayer loaded = RPGPlayerDataManager.load(getPlayer());
        if(loaded != null){
            copyFrom(loaded);
        }
    }
}
