package pandodungeons.bossfights.bossEntities.spider.attacks;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.bossfights.bossEntities.spider.entities.SpiderBoss;

import java.util.Random;

public class VenomSpray {

    private final SpiderBoss spiderBoss;

    public VenomSpray(SpiderBoss spiderBoss) {
        this.spiderBoss = spiderBoss;
    }

    public void execute() {
        Player target = (Player) spiderBoss.getSpider().getTarget();
        if (target == null) return;

        Location targetLocation = target.getLocation();

        // Reproducir sonido de veneno
        target.getWorld().playSound(targetLocation, Sound.ENTITY_WITCH_THROW, SoundCategory.HOSTILE, 1, 1);

        // Generar partículas de veneno
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 2;
            double offsetY = (random.nextDouble() - 0.5) * 2;
            double offsetZ = (random.nextDouble() - 0.5) * 2;
            target.getWorld().spawnParticle(Particle.ITEM_SLIME, targetLocation.add(offsetX, offsetY, offsetZ), 1);
        }

        // Aplicar efecto de veneno al jugador
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 2));
    }
}
