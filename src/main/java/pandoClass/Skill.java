package pandoClass;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class Skill {
    protected int lvl;
    protected Player owner;
    protected String description;
    protected String displayValue;

    protected Skill(int lvl, UUID player){
        this.owner = Bukkit.getPlayer(player);
        this.lvl = lvl;
        this.description = "";
        this.displayValue = "";
    }

    protected Skill(int lvl, Player player){
        this.owner = player;
        this.lvl = lvl;
        this.description = "";
        this.displayValue = "";
    }

    protected String getDescription(){
        return description;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public abstract String getName();

    protected abstract boolean canActivate();

    protected abstract void doAction();

    public abstract void reset();

    public final void action() {
        if (canActivate()) {
            doAction();
        }else{
            reset();
        }
    }

    protected Player getPlayer(){
        return owner;
    }

    public int getLvl() {
        return lvl;
    }
}
