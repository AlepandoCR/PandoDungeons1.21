package pandoClass.classes.mage.skills.orb.skills;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pandoClass.classes.mage.skills.orb.Orb;
import pandoClass.classes.mage.skills.orb.OrbEmotion;
import pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class OrbSkillAttack extends OrbSkill {

    public OrbSkillAttack(PandoDungeons plugin, Orb orb) {
        super(plugin, orb);
    }

    public void start(int level) {
        stop(); // Detener la habilidad anterior si existe
        AtomicBoolean shot = new AtomicBoolean(false);

        int fireballInterval = Math.max(4, 80 - (level * 5)); // Ajuste más claro por nivel

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!owner.isOnline() || owner.isDead() || !plugin.orbsManager.hasOrb(owner)) {
                    stop();
                    return;
                }

                shot.set(false);
                // Buscar un enemigo cercano
                owner.getWorld().getNearbyEntities(owner.getLocation(), 10, 10, 10)
                        .stream()
                        .filter(e -> e instanceof Enemy && !(e instanceof Player))
                        .map(e -> (Enemy) e)
                        .findFirst()
                        .ifPresent(enemy -> {
                            Location fireballLoc = orb.getLocation();

                            if (fireballLoc.getWorld() == null || enemy.getWorld() == null || !enemy.getWorld().equals(fireballLoc.getWorld())) {
                                return;
                            }

                            Vector direction = enemy.getLocation().subtract(fireballLoc).toVector();

                            if (direction.lengthSquared() == 0) {
                                return;
                            }

                            direction.normalize();
                            double speed = 0.5 + (0.1 * level);

                            Fireball fireball = fireballLoc.getWorld().spawn(fireballLoc, Fireball.class);
                            fireball.setDirection(direction);
                            fireball.setIsIncendiary(false);
                            fireball.setYield(0);
                            fireball.setShooter(owner);
                            fireball.setVelocity(direction.clone().multiply(speed));
                            shot.set(true);

                            if (!orb.getCurrentEmotion().equals(OrbEmotion.ANGRY)) {
                                orb.changeEmotion(OrbEmotion.ANGRY);
                            }
                        });

                if (!shot.get()) {
                    if (!orb.getCurrentEmotion().equals(OrbEmotion.HAPPY)) {
                        orb.changeEmotion(OrbEmotion.HAPPY);
                    }
                    try {
                        orb.setOrbDisplay("46994d71b875f087e64dea9b4a0a5cb9f4eb9ab0e8d9060dfde7f6803baa1779");
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        // Corrección: El delay entre ejecuciones ahora se basa en `fireballInterval`
        task.runTaskTimer(plugin, fireballInterval, fireballInterval);
    }
}
