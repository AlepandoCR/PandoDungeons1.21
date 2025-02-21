package pandoClass;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;

public abstract class ClassRPG {
    protected Skill firstSkill;
    protected Skill secondSkill;
    protected Skill thirdSkill;
    protected String key;
    int lvl;
    protected RPGPlayer player;

    private static final PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    public ClassRPG(String key, RPGPlayer player) {
        this.player = player;
        this.lvl = player.getLevel();
        this.key = key;
        setSkills();
    }

    protected abstract void setSkills();

    protected abstract void skillsToTrigger();

    protected abstract void toReset();

    protected void triggerSkills(){
        new BukkitRunnable(){

            @Override
            public void run() {
                if(player.getPlayer() == null || !player.getPlayer().isOnline()){
                    plugin.getLogger().warning("ended loop");
                    cancel();
                    return;
                }
                skillsToTrigger();
            }
        }.runTaskTimer(plugin,0,1);
    }

    public Skill getFirstSkill() {
        return firstSkill;
    }

    public void setFirstSkill(Skill firstSkill) {
        this.firstSkill = firstSkill;
    }

    public Skill getSecondSkill() {
        return secondSkill;
    }

    public void setSecondSkill(Skill secondSkill) {
        this.secondSkill = secondSkill;
    }

    public Skill getThirdSkill() {
        return thirdSkill;
    }

    public void setThirdSkill(Skill thirdSkill) {
        this.thirdSkill = thirdSkill;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }
}
