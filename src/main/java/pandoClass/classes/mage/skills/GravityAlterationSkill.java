package pandoClass.classes.mage.skills;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pandoClass.Skill;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.List;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class GravityAlterationSkill extends Skill {

    private final PandoDungeons plugin;
    private static final double BASE_RADIUS = 5.0; // Radio base
    private static final int BASE_DURATION = 40; // Duración base (2s)
    private static final int BASE_COOLDOWN = 40; // Cooldown base en segundos

    public GravityAlterationSkill(int lvl, Player player, PandoDungeons plugin) {
        super(lvl, player);
        this.plugin = plugin;
        description = "Modifica la gravedad de los enemigos cercanos, haciéndolos flotar.";
        displayValue = "fbc12b5c89e5d6a789a34b87f18a5cb5f4e12ab1e8d9a60dfde7a6803b4a8888"; // Ícono personalizado
    }

    @Override
    public String getName() {
        return "Alteración Gravitacional";
    }

    @Override
    protected boolean canActivate() {
        // Asegúrate de que el jugador no esté en PvP y que tenga la entrada correcta
        return !isPlayerOnPvP(getPlayer()) && getPlayer().getCurrentInput().isSneak() && getPlayer().getCurrentInput().isJump();
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
                .filter(e -> e instanceof LivingEntity && e != getPlayer())
                .toList();

        for (Entity entity : nearbyEntities) {
            LivingEntity target = (LivingEntity) entity;
            target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, duration, 1));
        }

        getPlayer().sendMessage("§d¡Has alterado la gravedad! Enemigos flotando por " + (duration / 20) + "s.");
        startCooldown(); // Iniciar cooldown
    }

    @Override
    public void reset() {
        // No se necesita reset específico
    }

    // Método para iniciar el cooldown
    public void startCooldown() {
        // Calcular el cooldown en base al nivel
        int cooldownTime = BASE_COOLDOWN - getLvl(); // Disminuye el cooldown según el nivel
        cooldownTime = Math.max(cooldownTime, 5); // Asegúrate de que el cooldown no sea menor a 5 segundos (o cualquier valor mínimo)

        // Ejecutar el código después del cooldown
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // Lógica para terminar el cooldown
            // Por ejemplo, habilitar la habilidad nuevamente
        }, cooldownTime * 20L); // Convertir el tiempo a ticks (1 segundo = 20 ticks)
    }

}
