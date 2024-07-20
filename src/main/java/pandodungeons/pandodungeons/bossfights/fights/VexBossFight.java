package pandodungeons.pandodungeons.bossfights.fights;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.bossfights.bossEntities.vex.attacks.*;
import pandodungeons.pandodungeons.bossfights.bossEntities.vex.entities.MinionVex;
import pandodungeons.pandodungeons.bossfights.bossEntities.vex.entities.VexBoss;

import java.util.List;

public class VexBossFight {
    private boolean stopFight;
    private final JavaPlugin plugin;
    private BossBar vexBossHealthBar;
    private final Location location;

    public VexBossFight(Location location) {
        this.location = location;
        plugin = JavaPlugin.getPlugin(PandoDungeons.class);
        if (vexBossHealthBar == null) {
            vexBossHealthBar = Bukkit.createBossBar(
                    ChatColor.AQUA + "Vex",
                    BarColor.BLUE,
                    BarStyle.SOLID
            );
        }
        for (Player player : location.getWorld().getPlayers()) {
            vexBossHealthBar.addPlayer(player);
        }
    }

    public void startVexBossFight() {
        BukkitRunnable fight = new BukkitRunnable() {
            final VexBoss vexBoss = new VexBoss(plugin, location);
            int shadowStrikeTicks = 0;
            int phantomBladeTicks = 0;
            int vexSwarmTicks = 0;

            final ShadowStrike shadowStrike = new ShadowStrike(vexBoss);
            final PhantomBlade phantomBlade = new PhantomBlade(vexBoss, plugin);
            final VexSwarm vexSwarm = new VexSwarm(vexBoss, plugin);

            @Override
            public void run() {
                if(location.getWorld() == null){
                    vexBossHealthBar.removeAll();
                    StopFight();
                    cancel();
                    return;
                }
                if (vexBoss.getVex() == null) {
                    vexBossHealthBar.removeAll();
                    cancel();
                    return; // Espera hasta que el Vex esté inicializado
                }
                Location vexLocation = vexBoss.getVex().getLocation();
                if (vexBoss.getVex().isDead()) {
                    sendCongrats(vexBoss);
                    StopFight();
                    vexBossHealthBar.removeAll();
                    cancel(); // Cancela la tarea
                    vexBoss.getVex().remove();
                    return;
                }

                shadowStrikeTicks++;
                phantomBladeTicks++;

                if(vexBoss.getVex().getTarget() instanceof Player){
                    if(vexBoss.getVex().getTarget().isDead()){
                        vexBossHealthBar.removePlayer((Player)vexBoss.getVex().getTarget());
                    }
                }

                if (shadowStrikeTicks >= 600) { // Ejecutar cada 20 segundos (400 ticks)
                    shadowStrike.execute();
                    shadowStrikeTicks = 0;
                }

                if (phantomBladeTicks >= 300 && vexBoss.getVex().isCharging()) {
                    phantomBlade.execute();
                    phantomBladeTicks = 0;
                }

                if (vexSwarmTicks >= 800) { // Ejecutar cada 40 segundos (800 ticks)
                    vexSwarm.execute();
                    vexSwarmTicks = 0;
                }

                // Establecer invulnerabilidad según la presencia de minions
                List<MinionVex> minions = vexSwarm.getMinions();
                manageMinions(minions, vexLocation);
                boolean hasMinions = minions.stream().anyMatch(minion -> minion.getMinionVex() != null && !minion.getMinionVex().isDead());
                vexBoss.getVex().setInvulnerable(hasMinions);

                if(!hasMinions){
                    vexSwarmTicks++;
                }

                // Establecer target del vexBoss
                vexBoss.setVexTarget(vexLocation);

                // Actualizar BossBar con la vida del Vex
                bossBarManagement(vexBoss);
            }
        };

        if (!stopFight) {
            fight.runTaskTimer(plugin, 0, 1);
        }
    }

    private void manageMinions(List<MinionVex> minions, Location vexLocation){
        for(MinionVex vex : minions){
            if(vex.getMinionVex().getLocation().distance(vexLocation) > 10){
                vex.getMinionVex().damage(1000);
                vex.getMinionVex().remove();
            }
        }
    }

    private void bossBarManagement(VexBoss vexBoss) {
        double health = vexBoss.getVex().getHealth();
        double maxHealth = vexBoss.getVex().getMaxHealth();

        double progress = health / maxHealth;

        progress = Math.max(0.0, Math.min(1.0, progress));

        vexBossHealthBar.setProgress(progress);
    }

    public void StopFight() {
        stopFight = true;
        vexBossHealthBar.removeAll();
    }

    public void sendCongrats(VexBoss vexBoss) {
        for (Player player : vexBoss.getVex().getWorld().getPlayers()) {
            player.sendMessage(ChatColor.DARK_AQUA + "¡Has derrotado al Vex!");
        }
    }
}
