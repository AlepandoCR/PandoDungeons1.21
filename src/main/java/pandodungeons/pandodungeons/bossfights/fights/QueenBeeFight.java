package pandodungeons.pandodungeons.bossfights.fights;

import net.kyori.adventure.util.TriState;

import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;
import pandodungeons.pandodungeons.bossfights.bossEntities.queenBee.attacks.NectarBombAttack;
import pandodungeons.pandodungeons.bossfights.bossEntities.queenBee.attacks.VenomAttack;
import pandodungeons.pandodungeons.bossfights.bossEntities.queenBee.entities.KamikazeBee;
import pandodungeons.pandodungeons.bossfights.bossEntities.queenBee.entities.QueenBee;
import pandodungeons.pandodungeons.Utils.LocationUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueenBeeFight {
    private boolean stopFight;
    private final JavaPlugin plugin;
    private BossBar queenBeeHealthBar;
    private final Location location;

    public QueenBeeFight(Location location) {
        this.location = location;
        plugin = JavaPlugin.getPlugin(PandoDungeons.class);
        if (queenBeeHealthBar == null) {
            queenBeeHealthBar = Bukkit.createBossBar(
                    ChatColor.GOLD + "Reina Abeja",
                    BarColor.YELLOW,
                    BarStyle.SOLID
            );
        }
        for (Player player : location.getWorld().getPlayers()) {
            queenBeeHealthBar.addPlayer(player);
        }
    }

    public void startQueenBeeFight() {
        BukkitRunnable fight = new BukkitRunnable() {
            final QueenBee queen = new QueenBee(plugin, location);
            List<KamikazeBee> kamikazeBees = new ArrayList<>();
            int atkPlayerTicks = 0;
            int kamikazeBeeTicks = 0;
            int kamikazeExplotionTimer = 30;
            int venomAttkTicks = 0;
            Location queenBeeLocation;
            final PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, 1, 3, false, false, false);
            final VenomAttack venomAttack = new VenomAttack(queen, 10, 3.0); // 8 flechas en un radio de 3 bloques
            final NectarBombAttack nectarBombAttack = new NectarBombAttack(queen, plugin);

            int nectarBombTicks = 0;
            @Override
            public void run() {
                if(location.getWorld() == null){
                    queenBeeHealthBar.removeAll();
                    StopFight();
                    cancel();
                    return;
                }
                if (queen.getBee() == null) {
                    queenBeeHealthBar.removeAll();
                    cancel();
                    return; // Espera hasta que la abeja esté inicializada
                }
                queenBeeLocation = queen.getBee().getLocation();
                if (queen.getBee().isDead()) {
                    sendCongrats(queen);
                    StopFight();
                    queenBeeHealthBar.removeAll();
                    cancel(); // Cancela la tarea
                    queen.getBee().remove();
                    queen.getBody().remove();
                    return;
                }

                if(queen.getBee().getTarget() instanceof Player){
                    if(queen.getBee().getTarget().isDead()){
                        queenBeeHealthBar.removePlayer((Player)queen.getBee().getTarget());
                    }
                }


                if(kamikazeBees.isEmpty()){
                    kamikazeBeeTicks++;
                }
                venomAttkTicks++;
                atkPlayerTicks++;

                nectarBombTicks++;
                if (nectarBombTicks >= 600) {
                    nectarBombAttack.execute(queenBeeLocation.add(0,4,0));
                    nectarBombTicks = 0;
                }

                if(venomAttkTicks >= 1000){
                    venomAttack.execute();
                    venomAttkTicks = 0;
                }

                queen.updateBodyRotation(); // Actualizar la rotación del BlockDisplay

                if(kamikazeBeeTicks >= 500){
                    if(kamikazeBees.isEmpty()){
                        kamikazeExplotionTimer = 30;
                        kamikazeBees = kamikazeBeeArmy(queenBeeLocation, queen);
                        kamikazeBeeTicks = 0;
                    }
                }

                if(queen.getBee().getTarget() instanceof Player){
                    Player target = (Player) queen.getBee().getTarget();
                    if(queen.getBee().getLocation().distance(target.getLocation()) <= 4){
                        target.addPotionEffect(slowness);
                    }else if(target.hasPotionEffect(PotionEffectType.SLOWNESS)){
                        target.removePotionEffect(PotionEffectType.SLOWNESS);
                    }
                }
                if(kamikazeBees != null && !kamikazeBees.isEmpty()){
                    if(MinecraftServer.currentTick % 20 == 0){
                        kamikazeExplotionTimer--;
                    }
                    manageKamikazeBees(kamikazeBees, kamikazeExplotionTimer);
                }
                if (atkPlayerTicks >= 100) {
                    queen.setBeeTarget(queenBeeLocation);
                    atkPlayerTicks = 0;
                }

                if (!(queen.getBee().getTarget() instanceof Player) && !queen.getBee().isInvulnerable()) {
                    queen.getBee().setInvulnerable(true);
                }

                if ((queen.getBee().getTarget() instanceof Player) && queen.getBee().isInvulnerable() && kamikazeBees.isEmpty()) {
                    queen.getBee().setInvulnerable(false);
                }
                // Actualizar BossBar con la vida de la abeja reina
                bossBarManagement(queen);
            }
        };

        if (!stopFight) {
            fight.runTaskTimer(plugin, 0, 1);
        }
    }

    private void bossBarManagement(QueenBee queen){
        double health = queen.getBee().getHealth();
        double maxHealth = queen.getBee().getMaxHealth();

        double progress = health / maxHealth;

        progress = Math.max(0.0, Math.min(1.0, progress));

        queenBeeHealthBar.setProgress(progress);
    }


    public void StopFight() {
        stopFight = true;
        queenBeeHealthBar.removeAll();
    }

    public void sendCongrats(QueenBee queen) {
        for (Player player : queen.getBee().getWorld().getPlayers()) {
            player.sendMessage(ChatColor.GOLD + "Has matado a la reina abeja");
        }
    }
    public List<KamikazeBee> kamikazeBeeArmy(Location location, QueenBee queenBee){
        List<KamikazeBee> kamikazeBees = new ArrayList<>();
        for(int i = 0; i <= 5 ; i++){
            kamikazeBees.add(new KamikazeBee(plugin, location, queenBee));
        }
        return kamikazeBees;
    }
    public void manageKamikazeBees(List<KamikazeBee> kamikazeBees, int explotionTimer){
        if(kamikazeBees != null){
            Iterator<KamikazeBee> iterator = kamikazeBees.iterator();
            while(iterator.hasNext()) {
                KamikazeBee kamikazeBee = iterator.next();
                if(kamikazeBee.getKamikazeBee().getHealth() < 1){
                    kamikazeBee.getKamikazeBee().remove();
                    iterator.remove();
                }
                Player player = LocationUtils.findNearestPlayer(kamikazeBee.getBeeLocation().getWorld(), kamikazeBee.getBeeLocation());
                kamikazeBee.getKamikazeBee().setTarget(player);
                if (explotionTimer <= 0) {
                    kamikazeBee.getBeeLocation().createExplosion(3.0f,  false, false);
                    kamikazeBee.getKamikazeBee().remove();
                    iterator.remove();
                }else if(MinecraftServer.currentTick % 10 == 0 && explotionTimer <= 3){
                    if(kamikazeBee.getKamikazeBee().getRollingOverride() == TriState.TRUE){
                        kamikazeBee.getKamikazeBee().setRollingOverride(TriState.FALSE);
                    }else{
                        kamikazeBee.getKamikazeBee().setRollingOverride(TriState.TRUE);
                    }
                }
                if(explotionTimer > 0){
                    explotionTimer--;
                    kamikazeBee.getKamikazeBee().setCustomName(ChatColor.RED.toString() + explotionTimer);
                }

            }
        }
    }
}
