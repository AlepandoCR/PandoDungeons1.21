package controlledEntities.modeled.pets;

import com.ticxo.modelengine.api.generator.assets.ItemModel;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import controlledEntities.modeled.ModeledControlled;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import pandodungeons.pandodungeons.PandoDungeons;

import javax.swing.text.LabelView;
import java.util.Map;

import static controlledEntities.modeled.pets.PetFactory.getPetName;

public abstract class Pet extends ModeledControlled {
    protected final Player owner;
    protected final String permission;
    protected final PetType type;
    protected PetNameTagHandler nameHandler;

    public Pet(Player owner, PandoDungeons plugin, boolean applygoals, String modelName){
        super(plugin, owner.getLocation(),applygoals, modelName);
        this.mob.setInvulnerable(true);
        this.mob.setInvisible(true);
        this.owner = owner;
        this.permission = setPermission();
        this.type = setType();
        this.nameHandler = null;
        String customName = getPetName(type,owner,plugin);

        if(customName != null){
            if(!customName.isEmpty()){
                setCustomName(customName);
            }
        }

        plugin.petsManager.addPet(this);
    }

    public abstract PetType setType();

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

        if(nameHandler != null){
            nameHandler.getNameTag().remove();
        }
    }

    public void respawnPet(){
        respawn(owner.getLocation());

        if(!plugin.petsManager.getPets().contains(this)){
            plugin.petsManager.addPet(this);
        }
    }

    public abstract String setPermission();

    public abstract String getDisplayValue();

    public void setCustomName(String s){
        nameHandler = new PetNameTagHandler(plugin,this, s);
    }

}
