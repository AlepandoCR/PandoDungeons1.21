package pandodungeons.bossfights.fights;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.PandoDungeons;
import pandodungeons.bossfights.bossEntities.triton.attacks.WaterBlastAttack;
import pandodungeons.bossfights.bossEntities.triton.attacks.DrowningAura;
import pandodungeons.bossfights.bossEntities.triton.entities.MiniDrowned;
import pandodungeons.bossfights.bossEntities.triton.entities.Triton;

public class TritonFight {
    private boolean stopFight;
    private final JavaPlugin plugin;
    private BossBar tritonHealthBar;
    private final Location location;

    public TritonFight(Location location) {
        this.location = location;
        plugin = JavaPlugin.getPlugin(PandoDungeons.class);
        if (tritonHealthBar == null) {
            tritonHealthBar = Bukkit.createBossBar(
                    ChatColor.AQUA + "Tritón",
                    BarColor.BLUE,
                    BarStyle.SOLID
            );
        }
        for (Player player : location.getWorld().getPlayers()) {
            tritonHealthBar.addPlayer(player);
        }

    }

    public void startTritonFight() {
        BukkitRunnable fight = new BukkitRunnable() {
            final Triton triton = new Triton(plugin, location);
            int waterBlastTicks = 0;
            int drowningAuraTicks = 0;
            int miniCooldown = 0;
            final MiniDrowned miniDrowned = new MiniDrowned(triton);
            final WaterBlastAttack waterBlastAttack = new WaterBlastAttack(triton);
            final DrowningAura drowningAura = new DrowningAura(triton);

            @Override
            public void run() {
                if(location.getWorld() == null){
                    tritonHealthBar.removeAll();
                    StopFight();
                    cancel();
                    return;
                }

                if (triton.getDrowned() == null) {
                    tritonHealthBar.removeAll();
                    cancel();
                    return; // Espera hasta que el Tritón esté inicializado
                }
                Location tritonLocation = triton.getDrowned().getLocation();
                if (triton.getDrowned().isDead()) {
                    sendCongrats(triton);
                    StopFight();
                    tritonHealthBar.removeAll();
                    miniDrowned.removeMiniDrownedHorde();
                    cancel(); // Cancela la tarea
                    triton.getDrowned().remove();
                    return;
                }

                if (!miniDrowned.areMiniDrownedAlive()) {
                    miniCooldown++;
                    if(miniCooldown >= 200){
                        miniDrowned.summonMiniDrownedHorde();
                        miniCooldown = 0;
                    }
                    if(triton.getDrowned().isInvulnerable()){
                        triton.getDrowned().setInvulnerable(false);
                    }
                }else{
                    triton.getDrowned().setInvulnerable(true);
                }


                waterBlastTicks++;
                drowningAuraTicks++;

                if(triton.getDrowned().getTarget() instanceof Player){
                    if(triton.getDrowned().getTarget().isDead()){
                        tritonHealthBar.removePlayer((Player)triton.getDrowned().getTarget());
                    }
                }

                if (waterBlastTicks >= 400) { // Ejecutar cada 20 segundos (400 ticks)
                    waterBlastAttack.execute();
                    waterBlastTicks = 0;
                }

                if (drowningAuraTicks >= 600) { // Ejecutar cada 30 segundos (600 ticks)
                    drowningAura.execute();
                    drowningAuraTicks = 0;
                }
                // Establecer target del triton
                triton.setDrownedTarget(tritonLocation);
                // Actualizar BossBar con la vida del Tritón
                bossBarManagement(triton);
                if(triton.getDrowned().getTarget() != null){
                    // Manejar el cambio de armas con respecto a la distancia del target
                    weaponManagement(triton);
                }
            }
        };

        if (!stopFight) {
            fight.runTaskTimer(plugin, 0, 1);
        }
    }

    private void bossBarManagement(Triton triton) {
        double health = triton.getDrowned().getHealth();
        double maxHealth = triton.getDrowned().getMaxHealth();

        double progress = health / maxHealth;

        progress = Math.max(0.0, Math.min(1.0, progress));

        tritonHealthBar.setProgress(progress);
    }

    public void StopFight() {
        stopFight = true;
        tritonHealthBar.removeAll();
    }

    public void sendCongrats(Triton triton) {
        for (Player player : triton.getDrowned().getWorld().getPlayers()) {
            player.sendMessage(ChatColor.DARK_AQUA + "¡Has derrotado al Tritón!");
        }
    }

    public void weaponManagement(Triton triton){
        Player target = (Player) triton.getDrowned().getTarget();
        Location location = triton.getDrowned().getLocation();
        assert target != null;
        Location targetLocation = target.getLocation();
        ItemStack mainHand = triton.getDrowned().getEquipment().getItemInMainHand();
        if(location.distance(targetLocation) < 4 && mainHand != Triton.createIronSwordWithSharpness()){
            triton.getDrowned().getEquipment().setItemInMainHand(Triton.createIronSwordWithSharpness());
            triton.setDrownedTarget(triton.getDrowned().getLocation());
        }else if(mainHand != Triton.createTridentWithImpaling() && location.distance(targetLocation) > 4){
            triton.getDrowned().getEquipment().setItemInMainHand(Triton.createTridentWithImpaling());
            triton.setDrownedTarget(triton.getDrowned().getLocation());
        }
    }
}