package pandodungeons.bossfights.bossEntities.vex.attacks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.bossfights.bossEntities.vex.entities.VexBoss;

public class ShadowStrike {
    private final VexBoss vexBoss;

    public ShadowStrike(VexBoss vexBoss) {
        this.vexBoss = vexBoss;
    }

    public void execute() {
        Player target = (Player) vexBoss.getVex().getTarget();
        if (target != null) {
            Location targetLocation = target.getLocation();
            target.getWorld().strikeLightning(targetLocation);
            PotionEffect darknessEffect = new PotionEffect(PotionEffectType.BLINDNESS, 200, 1);
            target.addPotionEffect(darknessEffect);
            target.sendMessage(ChatColor.DARK_PURPLE + "¡El Vex te ataca con un Golpe de Sombra!");
        }
    }
}