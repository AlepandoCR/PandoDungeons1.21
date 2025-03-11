package pandoClass.classes.archer.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import pandoClass.Skill;

import java.util.ArrayList;
import java.util.List;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class DoubleJumSkill extends Skill {

    public static List<Player> doubleJumping = new ArrayList<>();

    public DoubleJumSkill(int lvl, Player player) {
        super(lvl, player);
        description = "Tienes un salto doble, además cada mejora empuja más a los entidades cercanas cuando lo usas";
        displayValue = "466b10bf6ee2cd7e3ac96d9749ea616aa9c73030bdcaeffaed249e55c84994ac";
    }

    @Override
    public String getName() {
        return "Salto doble";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        if(!doubleJumping.contains(owner)){
            doubleJumping.add(owner);
        }
    }

    @Override
    public void reset() {
        doubleJumping.remove(owner);
    }
}
