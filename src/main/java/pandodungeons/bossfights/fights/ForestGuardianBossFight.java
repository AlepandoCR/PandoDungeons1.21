package pandodungeons.bossfights.fights;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.PandoDungeons;
import pandodungeons.bossfights.bossEntities.forestGuardian.attacks.*;
import pandodungeons.bossfights.bossEntities.forestGuardian.entities.ForestGuardian;
import pandodungeons.Game.Stats;
import pandodungeons.Utils.LocationUtils;

public class ForestGuardianBossFight {
    private boolean stopFight;
    private final JavaPlugin plugin;
    private BossBar forestGuardianHealthBar;
    private final Location location;

    public ForestGuardianBossFight(Location location) {
        this.location = location;
        plugin = JavaPlugin.getPlugin(PandoDungeons.class);
        if (forestGuardianHealthBar == null) {
            forestGuardianHealthBar = Bukkit.createBossBar(
                    ChatColor.DARK_GREEN + "Guardián del Bosque",
                    BarColor.GREEN,
                    BarStyle.SOLID
            );
        }
        for (Player player : location.getWorld().getPlayers()) {
            forestGuardianHealthBar.addPlayer(player);
        }
    }

    public void startForestGuardianBossFight() {
        BukkitRunnable fight = new BukkitRunnable() {
            final ForestGuardian forestGuardian = new ForestGuardian(plugin, location);
            int vinesStrikeTicks = 0;
            int earthquakeTicks = 0;
            int naturesWrathTicks = 0;
            int healed = 0;
            final int healChances = 2 + Stats.fromPlayer(LocationUtils.findNearestPlayer(location.getWorld(), location)).prestige();
            final VinesStrike vinesStrike = new VinesStrike(forestGuardian, plugin);
            final Earthquake earthquake = new Earthquake(forestGuardian, plugin);
            final NaturesWrath naturesWrath = new NaturesWrath(forestGuardian, plugin);

            @Override
            public void run() {
                if(location.getWorld() == null){
                    StopFight();
                    forestGuardianHealthBar.removeAll();
                    cancel();
                    return;
                }
                if (forestGuardian.getEvoker() == null) {
                    forestGuardianHealthBar.removeAll();
                    cancel();
                    return; // Espera hasta que el Guardián esté inicializado
                }
                Location guardianLocation = forestGuardian.getEvoker().getLocation();
                if (forestGuardian.getEvoker().isDead()) {
                    sendCongrats(forestGuardian);
                    StopFight();
                    forestGuardianHealthBar.removeAll();
                    cancel(); // Cancela la tarea
                    forestGuardian.getEvoker().remove();
                    return;
                }

                vinesStrikeTicks++;
                earthquakeTicks++;
                naturesWrathTicks++;

                if(healed < healChances){
                    if(manageGuardianLife(forestGuardian)){
                        healed++;
                    }
                }

                if(forestGuardian.getEvoker().getTarget() instanceof Player){
                    if(forestGuardian.getEvoker().getTarget().isDead()){
                        forestGuardianHealthBar.removePlayer((Player)forestGuardian.getEvoker().getTarget());
                    }
                }

                if (vinesStrikeTicks >= 400) { // Ejecutar cada 20 segundos (400 ticks)
                    vinesStrike.execute();
                    vinesStrikeTicks = 0;
                }

                if (earthquakeTicks >= 600) { // Ejecutar cada 30 segundos (600 ticks)
                    earthquake.execute();
                    earthquakeTicks = 0;
                }

                if (naturesWrathTicks >= 800) { // Ejecutar cada 40 segundos (800 ticks)
                    naturesWrath.execute();
                    naturesWrathTicks = 0;
                }
                // Establecer target del forestGuardian
                forestGuardian.setEvokerTarget(guardianLocation);
                // Actualizar BossBar con la vida del Guardián
                bossBarManagement(forestGuardian);
            }
        };

        if (!stopFight) {
            fight.runTaskTimer(plugin, 0, 1);
        }
    }

    private void bossBarManagement(ForestGuardian forestGuardian) {
        double health = forestGuardian.getEvoker().getHealth();
        double maxHealth = forestGuardian.getEvoker().getMaxHealth();

        double progress = health / maxHealth;

        progress = Math.max(0.0, Math.min(1.0, progress));

        forestGuardianHealthBar.setProgress(progress);
    }

    public void StopFight() {
        stopFight = true;
        forestGuardianHealthBar.removeAll();
    }

    public void sendCongrats(ForestGuardian forestGuardian) {
        for (Player player : forestGuardian.getEvoker().getWorld().getPlayers()) {
            player.sendMessage(ChatColor.DARK_GREEN + "¡Has derrotado al Guardián del Bosque!");
        }
    }

    public boolean manageGuardianLife(ForestGuardian guardian){
        double health = guardian.getEvoker().getHealth();
        Player target = (Player) guardian.getEvoker().getTarget();
        if(health <= (guardian.getEvoker().getMaxHealth() / 2)){
            guardian.getEvoker().setHealth(health + health / 2);
            assert target != null;
            target.sendMessage(ChatColor.RED + "El guardian del bosque se cura");
            return true;
        }
        return false;
    }
}
