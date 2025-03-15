package pandoClass.classes.mage.skills;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.Skill;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.*;

import static pandoClass.RPGListener.isPlayerOnPvP;

public class TimeRewindSkill extends Skill {

    private final PandoDungeons plugin;
    private static final Map<UUID, LinkedList<Location>> locationHistory = new HashMap<>();
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final List<Player> mages = new ArrayList<>();
    private static final int MAX_HISTORY = 10; // Máximo de segundos registrados
    private static final int BASE_COOLDOWN = 40; // Cooldown base en segundos
    private boolean sentCdMsg = false;

    public TimeRewindSkill(int lvl, Player player, PandoDungeons plugin) {
        super(lvl, player);
        mages.add(player);
        this.plugin = plugin;
        description = "Retrocede en el tiempo y vuelve a la posición que tenías hace unos segundos.";
        displayValue = "d4c23b5c23e5d6a789a34b87f18a5cb5f4e12ab1e8d9a60dfde7a6803b4a1779"; // Ícono personalizado

        // Iniciar historial si no existe
        locationHistory.putIfAbsent(player.getUniqueId(), new LinkedList<>());
    }

    @Override
    public String getName() {
        return "Retroceso Temporal";
    }

    @Override
    protected boolean canActivate() {
        return !isPlayerOnPvP(getPlayer());
    }

    @Override
    protected void doAction() {
        UUID uuid = owner.getUniqueId();


        //  Verificar si está corriendo y agachado
        if (owner.getCurrentInput().isSprint() && owner.getCurrentInput().isSneak()) {
            //  Verificar cooldown
            if (cooldowns.containsKey(uuid)) {
                long timeLeft = (cooldowns.get(uuid) - System.currentTimeMillis()) / 1000;
                if (timeLeft > 0) {
                    if(!sentCdMsg){
                        owner.sendMessage("§cRetroceso Temporal en cooldown. Espera " + timeLeft + "s.");
                        sentCdMsg = true;
                    }
                    return;
                }
            }
            LinkedList<Location> history = locationHistory.get(uuid);

            // Calcular tiempo a retroceder basado en nivel
            int rewindSeconds = Math.min(getLvl(), MAX_HISTORY);

            if (history.size() < rewindSeconds) {
                owner.sendMessage("§cNo hay suficiente historial para retroceder.");
                return;
            }

            //  Obtener ubicación pasada y teletransportar
            Location rewindLocation = history.get(history.size() - rewindSeconds);
            owner.teleport(rewindLocation);
            sentCdMsg = false;
            owner.sendMessage("§aHas retrocedido " + rewindSeconds + " segundos en el tiempo.");

            // Aplicar cooldown
            int cooldownTime = Math.max(BASE_COOLDOWN - (getLvl() * 2), 2); // Reduce cooldown con nivel (mínimo 2s)
            cooldowns.put(uuid, System.currentTimeMillis() + (cooldownTime * 1000));
        }
    }

    @Override
    public void reset() {
        locationHistory.remove(owner.getUniqueId());
        cooldowns.remove(owner.getUniqueId());
    }

    //  Rastreo de ubicaciones cada segundo
    public static void startTracking(PandoDungeons plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(mages.contains(player)){
                        UUID uuid = player.getUniqueId();
                        locationHistory.putIfAbsent(uuid, new LinkedList<>());

                        LinkedList<Location> history = locationHistory.get(uuid);
                        history.add(player.getLocation());

                        // Mantener solo el historial máximo
                        if (history.size() > MAX_HISTORY) {
                            history.removeFirst();
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20, 20); // Ejecutar cada segundo
    }
}
