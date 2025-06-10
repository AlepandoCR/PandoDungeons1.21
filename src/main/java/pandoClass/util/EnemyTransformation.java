package pandoClass.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import pandoClass.RPGPlayer;
import pandodungeons.PandoDungeons;

import java.util.List;

public class EnemyTransformation {

    public static void transformDungeon(LivingEntity enemy, PandoDungeons plugin, int lvl) {
        int exp = calculateExpFromLvl(lvl, enemy);
        calculateHpFromLvlAndApply(lvl, enemy);
        addKey(getExpKey(plugin), PersistentDataType.INTEGER, enemy, exp);
        addKey(getLvlKey(plugin),PersistentDataType.INTEGER,enemy, lvl);
        addKey(getCoinsKey(plugin), PersistentDataType.INTEGER, enemy, Math.max(1, (int) (lvl /2.5)));
    }

    private static int getAvrgLevel(List<Entity> entities, PandoDungeons plugin) {
        int totalLevel = 0;
        int count = 0;
        for (Entity entity : entities) {
            if (entity instanceof Player player) {
                RPGPlayer rpgPlayer = plugin.rpgManager.getPlayer(player);
                totalLevel += rpgPlayer.getLevel();
                count++;
            }
        }
        return (count > 0) ? totalLevel / count : 0;
    }

    private static int calculateExpFromLvl(int finalHp, LivingEntity entity){
        if(entity instanceof Enderman){
            return finalHp /10;
        }
        return finalHp;
    }

    private static void calculateHpFromLvlAndApply(int lvl, LivingEntity entity){
        double baseHP = entity.getMaxHealth();

        double multiply = lvl * 0.025;

        double finalHp = baseHP + (baseHP * multiply);

        entity.setMaxHealth(finalHp);

        entity.heal(baseHP * multiply);

    }

    static <P, C> void addKey(NamespacedKey key, PersistentDataType<P, C> type, LivingEntity entity, @NotNull C value){
        if(entity.getPersistentDataContainer().has(key)){
            entity.getPersistentDataContainer().remove(key);
        }
        entity.getPersistentDataContainer().set(key, type, value);
    }

    private static NamespacedKey getExpKey(PandoDungeons plugin){
        return new NamespacedKey(plugin,"exp");
    }

    private static NamespacedKey getLvlKey(PandoDungeons plugin){
        return new NamespacedKey(plugin,"lvl");
    }

    private static NamespacedKey getCoinsKey(PandoDungeons plugin){
        return new NamespacedKey(plugin,"coins");
    }
}
