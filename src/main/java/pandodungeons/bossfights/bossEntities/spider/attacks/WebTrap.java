package pandodungeons.bossfights.bossEntities.spider.attacks;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandodungeons.bossfights.bossEntities.spider.entities.SpiderBoss;

import java.util.ArrayList;
import java.util.List;

public class WebTrap {

    private final SpiderBoss spiderBoss;
    private final List<Silverfish> webEntities = new ArrayList<>();

    public WebTrap(SpiderBoss spiderBoss) {
        this.spiderBoss = spiderBoss;
    }

    public void execute() {
        Player target = (Player) spiderBoss.getSpider().getTarget();
        if (target == null) return;

        Location targetLocation = target.getLocation();
        Location baseLocation = targetLocation.clone().add(0, 1, 0);

        // Crear entidades y partículas para simular la telaraña
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Location webLocation = baseLocation.clone().add(i, 0, j);

                // Spawn de partículas de telaraña
                target.getWorld().spawnParticle(Particle.SNOWFLAKE, webLocation, 10, 0.5, 0.5, 0.5, 0);

                // Crear entidad invisible para simular la telaraña
                Silverfish webEntity = target.getWorld().spawn(webLocation, Silverfish.class);
                webEntity.setInvulnerable(true);
                webEntity.setSilent(true);
                webEntity.setAI(false);
                webEntities.add(webEntity);
            }
        }

        // Simular efecto de atrapamiento durante 5 segundos
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 5));
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));

        // Eliminar las entidades de telaraña después de 5 segundos
        spiderBoss.getPlugin().getServer().getScheduler().runTaskLater(spiderBoss.getPlugin(), this::removeWebEntities, 100);
    }

    private void removeWebEntities() {
        for (Silverfish webEntity : webEntities) {
            if (webEntity != null && !webEntity.isDead()) {
                webEntity.remove();
            }
        }
        webEntities.clear();
    }
}
