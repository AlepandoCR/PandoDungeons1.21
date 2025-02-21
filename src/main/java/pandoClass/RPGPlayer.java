package pandoClass;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pandoClass.archer.Archer;
import pandoClass.assasin.Assasin;
import pandoClass.files.RPGPlayerDataManager;
import pandoClass.tank.Tank;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.UUID;

public class RPGPlayer {
    private int level;
    private int exp;
    private int campsDefeated;
    private int coins;
    private UUID player;
    private int firstSkilLvl;
    private int secondSkilLvl;
    private int thirdSkillLvl;
    private String classKey = null;

    private static final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    public RPGPlayer(Player player) {
        this.player = player.getUniqueId();
        RPGPlayer loaded = RPGPlayerDataManager.load(getPlayer());
        if (loaded != null) {
            copyFrom(loaded);
        }
        else{
            defaults();
        }
    }

    public RPGPlayer(UUID player) {
        this.player = player;
        RPGPlayer loaded = RPGPlayerDataManager.load(getPlayer());
        if (loaded != null) copyFrom(loaded);
    }

    public void changeClass(String key){
        ClassRPG classToSet = null;

        switch (key){
            case "ArcherClass":
                classToSet = new Archer(this);
                break;
            case "TankClass":
                classToSet = new Tank(this);
                break;
            case "AssassinClass":
                classToSet = new Assasin(this);
                break;
            default:
                return;
        }

        if(plugin.rpgPlayersList.containsKey(this)){
            plugin.rpgPlayersList.replace(this,classToSet);
        }else {
            plugin.rpgPlayersList.put(this,classToSet);
        }
    }

    private void copyFrom(RPGPlayer other) {
        this.level = other.level;
        this.exp = other.exp;
        this.campsDefeated = other.campsDefeated;
        this.coins = other.coins;
        this.firstSkilLvl = other.firstSkilLvl;
        this.secondSkilLvl = other.secondSkilLvl;
        this.thirdSkillLvl = other.thirdSkillLvl;
        this.classKey = other.classKey;
    }

    private void defaults() {
        this.level = 1;
        this.exp = 0;
        this.campsDefeated = 0;
        this.coins = 0;
        this.firstSkilLvl = 1;
        this.secondSkilLvl = 1;
        this.thirdSkillLvl = 1;
        this.classKey = null;
    }

    public String getClassKey() {

        return classKey;
    }

    public void setClassKey(String classKey) {

        this.classKey = classKey;
        changeClass(classKey);
        RPGPlayerDataManager.save(this);
    }

    public int getLevel() {

        return level;
    }

    public void setLevel(int level) {

        this.level = level;
        RPGPlayerDataManager.save(this);
    }

    public int getExp() {

        return exp;
    }

    public void setExp(int exp) {

        this.exp = exp;
        RPGPlayerDataManager.save(this);
    }

    public int getCampsDefeated() {

        return campsDefeated;
    }

    public void setCampsDefeated(int campsDefeated) {

        this.campsDefeated = campsDefeated;
        RPGPlayerDataManager.save(this);
    }

    public int getCoins() {

        return coins;
    }

    public void setCoins(int coins) {

        this.coins = coins;
        RPGPlayerDataManager.save(this);
    }

    public UUID getPlayerUUID() {
        return player;
    }

    public void setPlayerUUID(UUID player) {

        this.player = player;
        RPGPlayerDataManager.save(this);
    }

    public void setPlayer(Player player) {

        this.player = player.getUniqueId();
        RPGPlayerDataManager.save(this);
    }

    public Player getPlayer() {

        return Bukkit.getPlayer(player);
    }

    public int getFirstSkilLvl() {

        return firstSkilLvl;
    }

    public void setFirstSkilLvl(int firstSkilLvl) {

        this.firstSkilLvl = firstSkilLvl;
        RPGPlayerDataManager.save(this);
    }

    public int getSecondSkilLvl() {

        return secondSkilLvl;
    }

    public void setSecondSkilLvl(int secondSkilLvl) {

        this.secondSkilLvl = secondSkilLvl;
        RPGPlayerDataManager.save(this);
    }

    public int getThirdSkillLvl() {

        return thirdSkillLvl;
    }

    public void setThirdSkillLvl(int thirdSkillLvl) {

        this.thirdSkillLvl = thirdSkillLvl;
        RPGPlayerDataManager.save(this);
    }

    public void load(){
        RPGPlayer loaded = RPGPlayerDataManager.load(getPlayer());
        if(loaded != null){
            copyFrom(loaded);
        }
    }
}
