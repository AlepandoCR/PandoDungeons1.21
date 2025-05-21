package pandoClass.classes.mage;

import org.bukkit.ChatColor;
import pandoClass.ClassRPG;
import pandoClass.RPGPlayer;
import pandoClass.Skill;
import pandoClass.classes.mage.skills.FrostNovaSkill;
import pandoClass.classes.mage.skills.GravityAlterationSkill;
import pandoClass.classes.mage.skills.OrbFriendSkill;
import pandoClass.classes.mage.skills.TimeRewindSkill;
import pandodungeons.pandodungeons.PandoDungeons;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class Mage extends ClassRPG {

    public static final List<Class<? extends Skill>> AVAILABLE_MAGE_SKILLS = Arrays.asList(
            OrbFriendSkill.class,
            TimeRewindSkill.class,
            GravityAlterationSkill.class,
            FrostNovaSkill.class
            // Add other Mage skills here as they are created
    );

    public Mage(RPGPlayer player, PandoDungeons plugin) {
        super("MageClass", player, plugin);
    }

    @Override
    protected String getName() {
        return "Mago";
    }

    private Skill instantiateSkill(String className, int skillLevel) {
        try {
            Class<?> skillClass = Class.forName(className);
            if (Skill.class.isAssignableFrom(skillClass)) {
                Constructor<? extends Skill> constructor = skillClass.asSubclass(Skill.class)
                        .getDeclaredConstructor(int.class, org.bukkit.entity.Player.class, PandoDungeons.class);
                return constructor.newInstance(skillLevel, player, plugin);
            } else {
                player.sendMessage(ChatColor.RED + "Error: " + className + " is not a valid skill type.");
            }
        } catch (ClassNotFoundException e) {
            player.sendMessage(ChatColor.RED + "Error: Skill class " + className + " not found.");
            plugin.getLogger().severe("Mage Skill class not found: " + className + " for player " + player.getName());
        } catch (NoSuchMethodException e) {
            player.sendMessage(ChatColor.RED + "Error: Could not find constructor for skill " + className);
            plugin.getLogger().severe("Mage Skill constructor not found for: " + className + " (int, Player, PandoDungeons)");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Error instantiating skill " + className + ": " + e.getMessage());
            plugin.getLogger().severe("Error instantiating Mage skill " + className + " for player " + player.getName() + ": " + e.toString());
            e.printStackTrace();
        }
        return null; // Return null if instantiation fails
    }


    @Override
    protected void setSkills() {
        String skill1Name = rpgPlayer.getMageSkill1();
        String skill2Name = rpgPlayer.getMageSkill2();
        String skill3Name = rpgPlayer.getMageSkill3();

        Skill s1 = instantiateSkill(skill1Name, rpgPlayer.getFirstSkilLvl());
        Skill s2 = instantiateSkill(skill2Name, rpgPlayer.getSecondSkilLvl());
        Skill s3 = instantiateSkill(skill3Name, rpgPlayer.getThirdSkillLvl());

        // Fallback to defaults if any skill failed to load or is null
        if (s1 == null) s1 = new OrbFriendSkill(rpgPlayer.getFirstSkilLvl(), player, plugin);
        if (s2 == null) s2 = new TimeRewindSkill(rpgPlayer.getSecondSkilLvl(), player, plugin);
        if (s3 == null) s3 = new GravityAlterationSkill(rpgPlayer.getThirdSkillLvl(), player, plugin);

        setFirstSkill(s1);
        setSecondSkill(s2);
        setThirdSkill(s3);
    }

    @Override
    protected void skillsToTrigger() {
        firstSkill.action();
        secondSkill.action();
        thirdSkill.action();
    }

    @Override
    protected void toReset() {

    }
}
