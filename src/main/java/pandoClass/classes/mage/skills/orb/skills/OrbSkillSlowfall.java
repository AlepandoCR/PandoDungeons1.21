package pandoClass.classes.mage.skills.orb.skills;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.classes.mage.skills.orb.Orb;
import pandoClass.classes.mage.skills.orb.OrbEmotion;
import pandodungeons.pandodungeons.PandoDungeons;

public class OrbSkillSlowfall extends OrbSkill {
    public OrbSkillSlowfall(PandoDungeons plugin, Orb orb) {
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

                owner.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 10, 1, false, false, false));
                if(!orb.getCurrentEmotion().equals(OrbEmotion.SCARED)){
                    orb.changeEmotion(OrbEmotion.SCARED);
                }
            }
        };
        task.runTaskTimer(plugin, 0, 20);
    }
}
