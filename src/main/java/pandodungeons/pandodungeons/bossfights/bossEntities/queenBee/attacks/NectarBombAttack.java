package pandodungeons.pandodungeons.bossfights.bossEntities.queenBee.attacks;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.bossfights.bossEntities.queenBee.entities.QueenBee;

public class NectarBombAttack {

    private final QueenBee queen;
    private final JavaPlugin plugin;

    public NectarBombAttack(QueenBee queen, JavaPlugin plugin) {
        this.queen = queen;
        this.plugin = plugin;
    }

    public void execute(Location targetLocation) {
        dropNectarBomb(targetLocation);
    }

    private void dropNectarBomb(Location location) {
        ArmorStand nectarBomb = location.getWorld().spawn(location, ArmorStand.class, stand -> {
            stand.setVisible(false);
            stand.setInvulnerable(true);
            stand.setGravity(true);
            stand.setHelmet(new ItemStack(Material.HONEY_BLOCK));
            stand.customName(Component.text(ChatColor.GOLD + "¡Bomba de Néctar!"));
            stand.setCustomNameVisible(true);
        });

        new BukkitRunnable() {
            int timer = 120; // 6sg
            @Override
            public void run() {
                if (timer <= 0) {
                    nectarBomb.getWorld().createExplosion(nectarBomb.getLocation(), 1F, false, false);
                    nectarBomb.remove();

                    // Aplicar efectos a los jugadores dentro de un radio
                    double radius = 6.0;
                    nectarBomb.getWorld().getNearbyEntities(nectarBomb.getLocation(), radius, radius, radius).stream()
                            .filter(entity -> entity instanceof Player)
                            .forEach(entity -> {
                                Player player = (Player) entity;
                                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                            });
                    cancel();
                    return;
                }

                // Mostrar partículas y actualizar el nombre visible
                if (timer <= 20) {
                    nectarBomb.getWorld().spawnParticle(Particle.INSTANT_EFFECT, nectarBomb.getLocation().add(0, 1, 0), 10);
                    nectarBomb.getWorld().playSound(nectarBomb.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    nectarBomb.customName(Component.text(ChatColor.GOLD + "¡Bomba de Néctar! - " + timer / 20 + "s"));
                }

                timer--;
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
