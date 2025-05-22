package pandoClass.quests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pandoClass.RPGPlayer;
import pandodungeons.PandoDungeons;

import java.util.*;

public abstract class Mission<T> {
    protected final String missionName;
    protected int amountTo;
    protected int amount;
    protected UUID player;
    protected int level;
    protected RPGPlayer rpgPlayer;
    protected PandoDungeons plugin;

    private static final Set<UUID> firstMissionCompleted = new HashSet<>();

    /**
     * Constructor para crear una misión.
     *
     * @param missionName Nombre de la misión.
     */
    public Mission(String missionName, Player player, PandoDungeons plugin) {
        this.missionName = missionName;
        this.player = player.getUniqueId();
        this.plugin = plugin;

        rpgPlayer = plugin.rpgManager.getPlayer(player);
        level = rpgPlayer.getLevel();

        // Fórmula dinámica para calcular la cantidad a completar
        int baseAmount = 5; // Cantidad mínima base
        double scalingFactor = 0.5; // Aumento por nivel
        this.amountTo = Math.max(1 ,(int) (baseAmount + (level * scalingFactor)));
    }

    public abstract void sendMissionMessage();

    public String getMissionName() {
        return missionName;
    }

    public int getLevel() {
        return level;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }

    public void addAmount() {
        amount++;
        this.rpgPlayer = plugin.rpgManager.getPlayer(getPlayer());
    }

    public boolean isCompleted() {
        return amount >= amountTo;
    }

    public int calculateReward() {
        int baseReward = 5;
        int scalingFactor = 10;
        return (amountTo * baseReward) + (level * scalingFactor);
    }

    public abstract void listener(T event);

    public abstract void rewardPlayer();

    /**
     * Verifica si es la primera misión completada del día para el jugador.
     *
     * Utiliza un mapa estático donde la clave es el UUID del jugador y el valor es la fecha (formato yyyyMMdd)
     * de la última misión completada.
     *
     * @param player El jugador a verificar.
     * @return true si es la primera misión completada hoy; false de lo contrario.
     */
    protected boolean isFirstMissionOfInstance(Player player) {
        if (player == null) {
            return false;
        }
        UUID uuid = player.getUniqueId();
        // Si el jugador aún no ha completado ninguna misión en esta instancia, es la primera.
        if (!firstMissionCompleted.contains(uuid)) {
            firstMissionCompleted.add(uuid);
            return true;
        }
        return false;
    }
}
