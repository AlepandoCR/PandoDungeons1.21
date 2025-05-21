package pandoClass.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pandodungeons.pandodungeons.PandoDungeons;
import pandoClass.RPGPlayer;
import pandoClass.Skill;
import pandoClass.classes.mage.Mage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MageSkillCommand implements CommandExecutor {

    private final PandoDungeons plugin;

    public MageSkillCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        RPGPlayer rpgPlayer = plugin.rpgManager.getPlayer(player);

        if (rpgPlayer == null) {
            player.sendMessage(ChatColor.RED + "Could not retrieve your player data.");
            return true;
        }

        if (!"MageClass".equalsIgnoreCase(rpgPlayer.getClassKey())) {
            player.sendMessage(ChatColor.RED + "You must be a Mage to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if ("list".equals(subCommand)) {
            listAvailableSkills(player);
            return true;
        }

        if ("select".equals(subCommand)) {
            if (args.length != 4) {
                player.sendMessage(ChatColor.RED + "Usage: /mageskills select <skill1_name_or_num> <skill2_name_or_num> <skill3_name_or_num>");
                listAvailableSkills(player);
                return true;
            }
            selectSkills(player, rpgPlayer, args[1], args[2], args[3]);
            return true;
        }

        sendUsage(player);
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.GOLD + "Mage Skill Command Usage:");
        player.sendMessage(ChatColor.AQUA + "/mageskills list" + ChatColor.WHITE + " - Lists available Mage skills.");
        player.sendMessage(ChatColor.AQUA + "/mageskills select <skill1> <skill2> <skill3>" + ChatColor.WHITE + " - Selects three skills.");
        player.sendMessage(ChatColor.GRAY + "Use skill names or numbers from the list for <skillN>.");
    }

    private void listAvailableSkills(Player player) {
        player.sendMessage(ChatColor.GOLD + "Available Mage Skills:");
        for (int i = 0; i < Mage.AVAILABLE_MAGE_SKILLS.size(); i++) {
            Class<? extends Skill> skillClass = Mage.AVAILABLE_MAGE_SKILLS.get(i);
            player.sendMessage(ChatColor.AQUA + "" + (i + 1) + ". " + skillClass.getSimpleName().replace("Skill", ""));
        }
    }

    private void selectSkills(Player player, RPGPlayer rpgPlayer, String skillArg1, String skillArg2, String skillArg3) {
        List<String> chosenSkillClassNames = new ArrayList<>();
        Set<String> chosenSimpleNames = new HashSet<>(); // To check for duplicates

        String[] skillArgs = {skillArg1, skillArg2, skillArg3};

        for (String skillArg : skillArgs) {
            Class<? extends Skill> selectedSkillClass = null;
            try {
                int skillNum = Integer.parseInt(skillArg);
                if (skillNum > 0 && skillNum <= Mage.AVAILABLE_MAGE_SKILLS.size()) {
                    selectedSkillClass = Mage.AVAILABLE_MAGE_SKILLS.get(skillNum - 1);
                }
            } catch (NumberFormatException e) {
                // Not a number, try matching by name (case-insensitive, ignore "Skill" suffix)
                String lowerArg = skillArg.toLowerCase();
                for (Class<? extends Skill> availableSkill : Mage.AVAILABLE_MAGE_SKILLS) {
                    String simpleNameLower = availableSkill.getSimpleName().toLowerCase();
                    if (simpleNameLower.startsWith(lowerArg) || simpleNameLower.replace("skill", "").startsWith(lowerArg)) {
                        selectedSkillClass = availableSkill;
                        break;
                    }
                }
            }

            if (selectedSkillClass == null) {
                player.sendMessage(ChatColor.RED + "Invalid skill: " + skillArg + ". Use '/mageskills list' to see available skills.");
                return;
            }

            if (!chosenSimpleNames.add(selectedSkillClass.getSimpleName())) {
                player.sendMessage(ChatColor.RED + "You must select three *distinct* skills. Skill '" + selectedSkillClass.getSimpleName().replace("Skill","") + "' was chosen more than once.");
                return;
            }
            chosenSkillClassNames.add(selectedSkillClass.getName());
        }

        if (chosenSkillClassNames.size() != 3) {
            player.sendMessage(ChatColor.RED + "You must select exactly three skills.");
            return;
        }

        rpgPlayer.setMageSkill1(chosenSkillClassNames.get(0));
        rpgPlayer.setMageSkill1(chosenSkillClassNames.get(0));
        rpgPlayer.setMageSkill2(chosenSkillClassNames.get(1));
        rpgPlayer.setMageSkill3(chosenSkillClassNames.get(2));

        // Call RPGPlayer.update() to refresh the player's class and skills
        // RPGPlayer.update() will internally handle re-creating the Mage class instance,
        // which in turn calls the modified setSkills() method.
        // rpgPlayer.save() is called within setMageSkillN methods and update() calls save() via other methods too.
        // However, an explicit update() call ensures the class association and skill triggers are refreshed.
        
        // The RPGPlayer instance 'rpgPlayer' already has the new skill names set.
        // Calling update() on this instance will use these new names when Mage class is re-instantiated.
        // Need to ensure that the 'rpgPlayer' object itself is the one managed by RpgManager.
        // Since RpgManager.getPlayer() returns a managed instance or creates a new one and adds it,
        // any modifications to 'rpgPlayer' here are on the managed instance.
        
        // The setMageSkillN methods in RPGPlayer already call save() and update().
        // So, the last call to setMageSkill3 would have already triggered an update.
        // Let's ensure the update happens *after* all three are set, if setMageSkillN's update isn't sufficient.
        // The current RPGPlayer.setMageSkillN calls update() each time. The last one would trigger the refresh.
        // To be absolutely sure the update uses all three new skills, we can call it once explicitly after all sets.
        // However, this might lead to multiple updates if each setter calls update().
        // Let's assume the last setMageSkill3() call's update() is sufficient.

        player.sendMessage(ChatColor.GREEN + "Mage skills updated successfully!");
        player.sendMessage(ChatColor.GRAY + "Selected skills: " +
                chosenSkillClassNames.stream()
                        .map(s -> s.substring(s.lastIndexOf('.') + 1).replace("Skill", ""))
                        .collect(Collectors.joining(", ")));
        player.sendMessage(ChatColor.YELLOW + "Your skills have been updated.");

        // Optional: If direct re-initialization is preferred over relying on setters' update()
        // RPGPlayer potentially needs a method like rpgPlayer.refreshSkills() which internally calls update()
        // or directly triggers the ClassRPG re-initialization logic.
        // For now, relying on the update() call within the last setMageSkillN method.
    }
}
