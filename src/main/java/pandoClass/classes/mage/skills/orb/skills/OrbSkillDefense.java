package pandoClass.classes.mage.skills.orb.skills;

import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pandoClass.classes.mage.skills.orb.Orb;
import pandoClass.classes.mage.skills.orb.OrbEmotion;
import pandodungeons.PandoDungeons;

public class OrbSkillDefense extends OrbSkill {
    public OrbSkillDefense(PandoDungeons plugin, Orb orb) {
        super(plugin, orb);
    }

    @Override
    public void start(int level) {
        stop();

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!owner.isOnline() || owner.isDead() || !plugin.orbsManager.hasOrb(owner)) {
                    stop();
                    return;
                }

                double blockChance = 0.1 + (0.025 * level); // Base 10%, sube 2.5% por nivel

                owner.getWorld().getNearbyEntities(owner.getLocation(), 3, 3, 3)
                        .stream()
                        .filter(e -> e instanceof Projectile)
                        .map(e -> (Projectile) e)
                        .forEach(proj -> {
                            if(proj.getShooter() != null){
                                if(proj.getShooter().equals(owner)){
                                    return;
                                }
                            }
                            if (Math.random() < blockChance) {
                                proj.setVelocity(new Vector(0,0,0)); // El orbe bloquea el proyectil
                                if(!orb.getCurrentEmotion().equals(OrbEmotion.CURIOUS)){
                                    orb.changeEmotion(OrbEmotion.CURIOUS);
                                }
                            }
                        });
            }
        };
        task.runTaskTimer(plugin, 0, 5);
    }
}
