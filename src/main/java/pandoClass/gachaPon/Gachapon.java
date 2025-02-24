package pandoClass.gachaPon;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.*;


public class Gachapon {

    public static List<Player> owedTokenPlayers = new ArrayList<>();

    public static Gachapon activeGachapon = null;
    // Mapas y listas para las listas de premios (ya definidos en tu clase)
    private final Map<Quality, List<ItemStack>> prizeLists = new EnumMap<>(Quality.class);
    private final Map<Quality, Double> qualityProbabilities = new EnumMap<>(Quality.class);

    // Para la animación
    private final List<ArmorStand> animationArmorStands = new ArrayList<>();
    private final Map<ArmorStand, Vector> animationVelocities = new HashMap<>();
    private BukkitRunnable animationTask;

    private final Random random = new Random();
    private final PandoDungeons plugin;
    private final Player player;

    public Gachapon(PandoDungeons plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        // Inicializar listas de premios para cada calidad (se pueden cargar de un config)
        for (Quality q : Quality.values()) {
            prizeLists.put(q, new ArrayList<>());
        }
        // Ejemplo de probabilidades: la suma debe ser 100
        qualityProbabilities.put(Quality.BASURA,30.0);
        qualityProbabilities.put(Quality.RARO, 40.0);
        qualityProbabilities.put(Quality.EPICO, 20.0);
        qualityProbabilities.put(Quality.LEGENDARIO, 8.0);
        qualityProbabilities.put(Quality.MITICO, 2.0);

        // Setea todos los premios escogidos en esta instancia
        getServerPrices();
        if(activeGachapon == null){
            activeGachapon = this;
        }
    }

    public void getServerPrices(){
        for(PrizeItem prizeItem : plugin.prizeManager.getPrizeItems()){
            addPrize(prizeItem.getQuality(), prizeItem.getItem());
        }
    }

    public void addPrize(Quality quality, ItemStack item) {
        prizeLists.get(quality).add(item);
    }

    /**
     * Método que triggerea el gachapon: inicia la animación, elige la calidad y el premio.
     * Aquí se espera que se llame startAnimation() pasando el centro de la animación.
     */
    public void trigger(Location center) {
        startAnimation(center);

        // Seleccionamos la calidad basada en los porcentajes
        Quality selectedQuality = chooseQuality();
        List<ItemStack> items = prizeLists.get(selectedQuality);
        ItemStack prize = null;
        if (!items.isEmpty()) {
            prize = items.get(random.nextInt(items.size()));
        }

        // Para efectos de ejemplo, simulamos una animación de 5 segundos (100 ticks) y luego finalizamos
        ItemStack finalPrize = prize;
        new BukkitRunnable() {
            @Override
            public void run() {
                if(finalPrize != null){
                    finishAnimation(center, finalPrize);
                    player.getInventory().addItem(finalPrize);
                    activeGachapon = null;
                }

            }
        }.runTaskLater(plugin, 100L);
    }

    private Quality chooseQuality() {
        double roll = random.nextDouble() * 100;
        double cumulative = 0.0;
        for (Quality q : Quality.values()) {
            cumulative += qualityProbabilities.get(q);
            if (roll <= cumulative) {
                return q;
            }
        }
        return Quality.BASURA;
    }

    /**
     * Inicia la animación del gachapon.
     * Se obtienen todos los premios de todas las calidades y se crea un ArmorStand para cada uno,
     * posicionándolos aleatoriamente dentro de una esfera virtual de radio 'radius' centrada en 'center'.
     * A cada ArmorStand se le asigna un vector de velocidad aleatorio para moverse dentro de la esfera.
     *
     * @param center La ubicación central de la animación.
     */
    private void startAnimation(Location center) {
        World world = center.getWorld();
        double radius = 1.5; // Radio de la esfera virtual

        // Recopila todos los premios disponibles
        List<ItemStack> animationItems = new ArrayList<>();
        for (List<ItemStack> list : prizeLists.values()) {
            animationItems.addAll(list);
        }
        int count = animationItems.size();
        if (count == 0) return; // No hay premios, aborta

        // Crea un ArmorStand para cada ItemStack en una posición aleatoria dentro de la esfera
        for (int i = 0; i < count; i++) {
            // Genera posición aleatoria dentro de la esfera
            double u = random.nextDouble();
            double v = random.nextDouble();
            double theta = 2 * Math.PI * u;
            double phi = Math.acos(2 * v - 1);
            double r = radius * Math.cbrt(random.nextDouble()); // Distribución uniforme en el volumen
            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);
            Location spawnLoc = center.clone().add(x, y, z);

            int finalI = i;
            ArmorStand stand = world.spawn(spawnLoc, ArmorStand.class, a -> {
                a.setGravity(false);
                a.setVisible(false);
                a.setMarker(true);
                a.getEquipment().setHelmet(animationItems.get(finalI));
            });
            animationArmorStands.add(stand);

            // Asigna un vector de velocidad aleatorio con una magnitud fija
            double speed = 0.05;
            double u2 = random.nextDouble();
            double v2 = random.nextDouble();
            double theta2 = 2 * Math.PI * u2;
            double phi2 = Math.acos(2 * v2 - 1);
            double vx = speed * Math.sin(phi2) * Math.cos(theta2);
            double vy = speed * Math.sin(phi2) * Math.sin(theta2);
            double vz = speed * Math.cos(phi2);
            Vector velocity = new Vector(vx, vy, vz);
            animationVelocities.put(stand, velocity);
        }

        // Inicia una tarea que actualiza la posición de cada ArmorStand para simular el movimiento dentro de la esfera
        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(animationArmorStands.isEmpty() || !player.isOnline()){
                    owedTokenPlayers.add(player);
                    this.cancel();
                    return;
                }
                for (ArmorStand stand : animationArmorStands) {
                    if(!stand.isValid() || stand.isDead()){
                        animationArmorStands.remove(stand);
                        stand.remove();
                    }
                    Vector velocity = animationVelocities.get(stand);
                    if (velocity == null) continue;
                    Location current = stand.getLocation();
                    Location next = current.clone().add(velocity);
                    // Si la nueva posición sale de la esfera, rebota reflejando el vector
                    if (next.distance(center) > radius) {
                        Vector toNext = next.toVector().subtract(center.toVector());
                        Vector normal = toNext.clone().normalize();
                        // Reflexión: v = v - 2*(v·n)*n
                        velocity = velocity.subtract(normal.multiply(2 * velocity.dot(normal)));
                        animationVelocities.put(stand, velocity);
                        next = current.clone().add(velocity);
                    }
                    stand.teleport(next);
                }
            }
        };
        // Actualiza cada tick (puedes ajustar el periodo)
        animationTask.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Finaliza la animación: se cancela la tarea, se eliminan los ArmorStands y se muestra el premio final.
     *
     * @param center La ubicación central de la animación.
     * @param prize  El ItemStack premiado (puede ser null).
     */
    private void finishAnimation(Location center, ItemStack prize) {
        if (animationTask != null) {
            animationTask.cancel();
            animationTask = null;
        }
        // Elimina todos los ArmorStands de la animación
        for (ArmorStand stand : animationArmorStands) {
            stand.remove();
        }
        animationArmorStands.clear();
        animationVelocities.clear();

        // Muestra el premio final en el centro (por ejemplo, con un ArmorStand brillante)
        if (prize != null) {
            ArmorStand finalStand = center.getWorld().spawn(center, ArmorStand.class, a -> {
                a.setGravity(false);
                a.setVisible(false);
                a.setMarker(true);
                a.getEquipment().setHelmet(prize);
                a.setGlowing(true);
            });
            // Después de 2 segundos se elimina el stand final
            new BukkitRunnable() {
                @Override
                public void run() {
                    finalStand.remove();
                }
            }.runTaskLater(plugin, 40L);
        }
    }
}
