package pandoClass;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.Map;

public abstract class ClassRPG {
    protected Skill firstSkill;
    protected Skill secondSkill;
    protected Skill thirdSkill;
    protected String key;
    int lvl;
    protected RPGPlayer rpgPlayer;
    protected Player player;
    protected boolean canceled = false;

    protected final PandoDungeons plugin;

    // Mapa para almacenar el runnable activo por jugador.
    private static final Map<Player, BukkitRunnable> triggeredSkills = new HashMap<>();

    public ClassRPG(String key, RPGPlayer player, PandoDungeons plugin) {
        this.rpgPlayer = player;
        this.plugin = plugin;
        this.lvl = player.getLevel();
        this.key = key;
        this.player = rpgPlayer.getPlayer();
        setSkills();
    }

    protected abstract String getName();

    protected abstract void setSkills();

    protected abstract void skillsToTrigger();

    protected abstract void toReset();

    protected void triggerSkills(){
        // Si ya existe un runnable para este jugador, se cancela y se elimina.
        if(triggeredSkills.containsKey(player)) {
            BukkitRunnable previousTask = triggeredSkills.get(player);
            previousTask.cancel();
            triggeredSkills.remove(player);
        }
        // Crear y almacenar el nuevo runnable.
        BukkitRunnable task = new BukkitRunnable(){
            @Override
            public void run() {
                if(rpgPlayer.getPlayer() == null || !rpgPlayer.getPlayer().isOnline() || canceled){
                    plugin.getLogger().warning("ended loop");
                    cancel(); // Cancela este runnable
                    triggeredSkills.remove(player); // Remueve del mapa
                    return;
                }
                skillsToTrigger();
            }
        };
        task.runTaskTimer(plugin, 0, 1);
        triggeredSkills.put(player, task);
    }

    protected void cancel() {
        toReset();
        canceled = true;
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
