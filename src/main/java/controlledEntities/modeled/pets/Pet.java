package controlledEntities.modeled.pets;

import com.ticxo.modelengine.api.entity.Hitbox;
import controlledEntities.modeled.ModeledControlled;
import controlledEntities.modeled.pets.goals.FollowOwnerGoal;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import pandodungeons.pandodungeons.PandoDungeons;

public abstract class Pet extends ModeledControlled {
    protected final Player owner;
    protected String permission;

    public Pet(Player owner, PandoDungeons plugin, boolean applygoals, String modelName){
        super(plugin, owner.getLocation(),applygoals, modelName);
        this.mob.setInvulnerable(true);
        this.mob.setInvisible(true);
        this.owner = owner;
    }

    protected boolean hasPermission(){
        return owner.hasPermission(permission);
    }

    public Player getOwner() {
        return owner;
    }

    public String getPermission() {
        return permission;
    }

}
