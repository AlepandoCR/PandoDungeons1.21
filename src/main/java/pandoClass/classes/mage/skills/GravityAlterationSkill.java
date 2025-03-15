package pandoClass.classes.mage.skills;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.Skill;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class GravityAlterationSkill extends Skill {

    private final PandoDungeons plugin;
    private static final double BASE_RADIUS = 5.0; // Radio base
    private static final int BASE_DURATION = 40; // Duración base (2s)
    private static final int BASE_COOLDOWN = 400; // Cooldown base en ticks (20 ticks = 1 segundo)
    private static Map<Player, Boolean> onCooldown = new HashMap<>(); // Estado del cooldown

    public GravityAlterationSkill(int lvl, Player player, PandoDungeons plugin) {
        super(lvl, player);
        this.plugin = plugin;
        description = "Modifica la gravedad de los enemigos cercanos, haciéndolos flotar.";
        displayValue = "fbc12b5c89e5d6a789a34b87f18a5cb5f4e12ab1e8d9a60dfde7a6803b4a8888"; // Ícono personalizado
        if(!onCooldown.containsKey(owner)){
            onCooldown.put(owner,false);
        }
    }

    @Override
    public String getName() {
        return "Alteración Gravitacional";
    }

    @Override
    protected boolean canActivate() {
        // Verifica que el jugador no esté en PvP, tenga la entrada correcta y no esté en cooldown
        return !isPlayerOnPvP(owner)
                && getPlayer().getCurrentInput().isSneak()
                && getPlayer().getCurrentInput().isJump()
                && !onCooldown.getOrDefault(owner,true);
    }

    @Override
    protected void doAction() {

        Location center = getPlayer().getLocation();
        double radius = BASE_RADIUS + (0.5 * getLvl()); // Escala con el nivel
        int duration = BASE_DURATION + (20 * getLvl()); // Escala con el nivel (cada 20 ticks = 1s)

        // 🎆 Efectos visuales y sonoros
        getPlayer().getWorld().spawnParticle(Particle.PORTAL, center, 100, radius, 1, radius, 0.2);
        getPlayer().getWorld().playSound(center, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);

        // 🚀 Aplicar levitación a enemigos cercanos
        List<Entity> nearbyEntities = getPlayer().getWorld().getNearbyEntities(center, radius, radius, radius)
                .stream()
                .filter(e -> e instanceof Enemy && e != getPlayer())
                .toList();

        for (Entity entity : nearbyEntities) {
            LivingEntity target = (LivingEntity) entity;
            target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, duration, 1));
        }

        getPlayer().sendMessage("§d¡Has alterado la gravedad! Enemigos flotando por " + (duration / 20) + "s.");

        // Iniciar cooldown
        startCooldown();
    }

    @Override
    public void reset() {
        // No se necesita reset específico
    }

    // Método para iniciar el cooldown
    private void startCooldown() {
        onCooldown.put(owner,true);

        // Calcular el cooldown en base al nivel
        int cooldownTime = BASE_COOLDOWN - (getLvl() * 20); // Disminuye el cooldown según el nivel
        cooldownTime = Math.max(cooldownTime, 100); // Asegúrate de que el cooldown no sea menor a 5 segundos (100 ticks)

        // Ejecutar el código después del cooldown
        new BukkitRunnable() {
            @Override
            public void run() {
                onCooldown.put(owner,false);
                getPlayer().sendMessage("§a¡La habilidad Alteración Gravitacional está lista de nuevo!");
            }
        }.runTaskLater(plugin, cooldownTime); // Ejecutar después de `cooldownTime` ticks
    }
}