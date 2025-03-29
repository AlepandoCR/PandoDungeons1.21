package controlledEntities.modeled.pets;

import controlledEntities.modeled.ModeledControlled;
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
        plugin.petsManager.addPet(this);
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

    public void destroy(){
        if(mob.isDead() || !mob.isValid()) return;

        modeledEntity.destroy();

        mob.remove();
    }

    public void respawnPet(){
        respawn(owner.getLocation());

        if(!plugin.petsManager.getPets().contains(this)){
            plugin.petsManager.addPet(this);
        }
    }

}
