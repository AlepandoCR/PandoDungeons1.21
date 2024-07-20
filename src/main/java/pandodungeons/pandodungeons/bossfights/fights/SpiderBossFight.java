package pandodungeons.pandodungeons.bossfights.fights;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.bossfights.bossEntities.spider.attacks.*;
import pandodungeons.pandodungeons.bossfights.bossEntities.spider.entities.SpiderBoss;

import java.util.List;

public class SpiderBossFight {
    private boolean stopFight;
    private final JavaPlugin plugin;
    private BossBar spiderBossHealthBar;
    private final Location location;

    public SpiderBossFight(Location location) {
        this.location = location;
        plugin = JavaPlugin.getPlugin(PandoDungeons.class);
        if (spiderBossHealthBar == null) {
            spiderBossHealthBar = Bukkit.createBossBar(
                    ChatColor.DARK_GREEN + "Araña Gigante",
                    BarColor.GREEN,
                    BarStyle.SOLID
            );
        }
        for (Player player : location.getWorld().getPlayers()) {
            spiderBossHealthBar.addPlayer(player);
        }
    }

    public void startSpiderBossFight() {
        BukkitRunnable fight = new BukkitRunnable() {
            final SpiderBoss spiderBoss = new SpiderBoss(plugin, location);
            int webTrapTicks = 0;
            int venomSprayTicks = 0;
            int spiderSummonTicks = 0;

            final WebTrap webTrap = new WebTrap(spiderBoss);
            final VenomSpray venomSpray = new VenomSpray(spiderBoss);
            final SpiderSummon spiderSummon = new SpiderSummon(spiderBoss);

            @Override
            public void run() {
                if(location.getWorld() == null){
                    spiderBossHealthBar.removeAll();
                    StopFight();
                    cancel();
                    return;
                }
                if (spiderBoss.getSpider() == null) {
                    spiderBossHealthBar.removeAll();
                    cancel();
                    return; // Espera hasta que la araña esté inicializada
                }
                Location spiderLocation = spiderBoss.getSpider().getLocation();
                if (spiderBoss.getSpider().isDead()) {
                    sendCongrats(spiderBoss);
                    StopFight();
                    cancel(); // Cancela la tarea
                    spiderBoss.getSpider().remove();
                    return;
                }

                webTrapTicks++;
                venomSprayTicks++;

                if(spiderBoss.getSpider().getTarget() instanceof Player){
                    if(spiderBoss.getSpider().getTarget().isDead()){
                        spiderBossHealthBar.removePlayer((Player)spiderBoss.getSpider().getTarget());
                    }
                }
                if (webTrapTicks >= 600) { // Ejecutar cada 30 segundos (600 ticks)
                    webTrap.execute();
                    webTrapTicks = 0;
                }

                if (venomSprayTicks >= 400) { // Ejecutar cada 20 segundos (400 ticks)
                    venomSpray.execute();
                    venomSprayTicks = 0;
                }

                if (spiderSummonTicks >= 800) { // Ejecutar cada 40 segundos (800 ticks)
                    spiderSummon.execute();
                    spiderSummonTicks = 0;
                }

                if(!spiderSummon.areMinionsAlive()){
                    spiderSummonTicks++;
                    spiderBoss.getSpider().setInvulnerable(false);
                }else{
                    spiderBoss.getSpider().setInvulnerable(true);
                }
                spiderBossMinions(spiderBoss, spiderSummon);


                spiderBoss.setSpiderTarget(spiderLocation);
                // Actualizar BossBar con la vida de la Araña
                bossBarManagement(spiderBoss);
            }
        };

        if (!stopFight) {
            fight.runTaskTimer(plugin, 0, 1);
        }
    }

    private void bossBarManagement(SpiderBoss spiderBoss) {
        double health = spiderBoss.getSpider().getHealth();
        double maxHealth = spiderBoss.getSpider().getMaxHealth();

        double progress = health / maxHealth;

        progress = Math.max(0.0, Math.min(1.0, progress));

        spiderBossHealthBar.setProgress(progress);
    }

    public void spiderBossMinions(SpiderBoss spiderBoss, SpiderSummon spiderSummon ){
        for(CaveSpider spider : spiderSummon.getMinions())
        if(spiderBoss.getSpider().getLocation().distance(spider.getLocation()) <= 0.5){
            spider.damage(100000);
            spider.remove();
        }

    }

    public void StopFight() {
        stopFight = true;
        spiderBossHealthBar.removeAll();
    }

    public void sendCongrats(SpiderBoss spiderBoss) {
        for (Player player : spiderBoss.getSpider().getWorld().getPlayers()) {
            player.sendMessage(ChatColor.DARK_GREEN + "¡Has derrotado a la Araña Gigante!");
        }
    }
}
