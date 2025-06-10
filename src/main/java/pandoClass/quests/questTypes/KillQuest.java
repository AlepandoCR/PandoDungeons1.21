package pandoClass.quests.questTypes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pandoClass.quests.Mission;
import pandodungeons.PandoDungeons;

import java.util.List;
import java.util.Random;

public class KillQuest extends Mission<EntityDeathEvent> {

    private final EntityType target;

    /**
     * Constructor para crear una misión de eliminar entidades.
     *
     * @param missionName Nombre de la misión.
     * @param player Jugador que recibe la misión.
     * @param plugin Instancia del plugin.
     */
    public KillQuest(String missionName, Player player, PandoDungeons plugin) {
        super(missionName, player, plugin);
        this.target = getRandomTarget(); // Selecciona un enemigo aleatorio de la lista.
        sendMissionMessage();
    }

    @Override
    public void listener(EntityDeathEvent event) {
        if(getPlayer() == null  || getPlayer().isOnline()){
            if (isCompleted()) {
                rewardPlayer();
                plugin.missionManager.removeMission(this);
                startNewQuest();
                return;
            }

            Entity entity = event.getEntity();
            if (event.getEntity().getKiller() != null && event.getEntity().getKiller().equals(getPlayer())) {
                if (entity.getType().equals(target)) {
                    addAmount();
                    getPlayer().sendMessage(getProgressMessage()); // Envía progreso al jugador
                }
            }
        }
    }

    private void startNewQuest(){
        new BukkitRunnable(){

            @Override
            public void run() {
                KillQuest killQuest = new KillQuest("matar mobs", getPlayer() ,plugin);
                plugin.missionManager.registerMission(killQuest);
            }
        }.runTaskLater(plugin,100);
    }

    @Override
    public void rewardPlayer() {
        rpgPlayer = plugin.rpgManager.getPlayer(getPlayer());
        long coins = calculateReward();
        long exp = coins * 2;
        rpgPlayer.addCoins(coins);
        rpgPlayer.addExp((int)exp);

        if (isFirstMissionOfInstance(getPlayer())) {
            getPlayer().sendMessage(ChatColor.GOLD + "§6¡Has completado tu primera misión del dia!");
        } else {
            getPlayer().sendMessage(ChatColor.GOLD + "§6¡Has completado la misión!");
        }
        getPlayer().sendMessage(ChatColor.DARK_GRAY + "Se ha agregado " + ChatColor.LIGHT_PURPLE + exp
                + ChatColor.DARK_GRAY + " de experiencia y " + ChatColor.YELLOW + coins
                + ChatColor.DARK_GRAY + " monedas");
    }

    /**
     * Envía el mensaje de inicio de la misión.
     */
    @Override
    public void sendMissionMessage() {
        getPlayer().sendMessage("§c⚔ ¡Nueva misión recibida! ⚔");
        getPlayer().sendMessage("§7Un cazador te ha encomendado eliminar §6" + amountTo + "§7 criaturas de tipo §c" + target.name() + "§7.");
        getPlayer().sendMessage("§7La seguridad del servidor está en tus manos. ¡Sal y caza!");
    }

    /**
     * Obtiene un mensaje con el progreso actual de la misión.
     *
     * @return Mensaje de progreso.
     */
    private String getProgressMessage() {
        return "§7Progreso: §c" + amount + "§7 / §6" + amountTo + "§7 criaturas §c" + target.name() + "§7 derrotadas.";
    }

    /**
     * Devuelve una entidad enemiga aleatoria de una lista predefinida.
     *
     * @return EntityType del enemigo objetivo.
     */
    private EntityType getRandomTarget() {
        List<EntityType> enemies = List.of(
                // Overworld - Hostiles
                EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.SKELETON, EntityType.STRAY,
                EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.SLIME,
                EntityType.WITCH, EntityType.PILLAGER, EntityType.VINDICATOR,
                EntityType.PHANTOM,

                // Nether - Hostiles
                EntityType.BLAZE, EntityType.WITHER_SKELETON, EntityType.MAGMA_CUBE,
                EntityType.ZOMBIFIED_PIGLIN, EntityType.HOGLIN,

                // End - Hostiles
                EntityType.ENDERMAN,

                // Overworld - Neutrales que pueden volverse hostiles
                EntityType.SILVERFISH,

                // Nether - Neutrales que pueden volverse hostiles
                EntityType.PIGLIN,

                // Otras criaturas desafiantes
                EntityType.GUARDIAN);
        return enemies.get(new Random().nextInt(enemies.size()));
    }
}
