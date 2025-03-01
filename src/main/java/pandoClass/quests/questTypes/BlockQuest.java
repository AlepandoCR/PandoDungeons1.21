package pandoClass.quests.questTypes;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import pandoClass.quests.Mission;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.List;
import java.util.Random;

public class BlockQuest extends Mission<BlockBreakEvent> {

    private final Material target;

    /**
     * Constructor para crear una misión de romper bloques.
     *
     * @param missionName Nombre de la misión.
     * @param player Jugador que recibe la misión.
     * @param amountTo Cantidad de bloques a romper.
     * @param plugin Instancia del plugin.
     */
    public BlockQuest(String missionName, Player player, int amountTo, PandoDungeons plugin) {
        super(missionName, player, plugin);
        this.target = getRandomBlock(); // Selecciona un bloque aleatorio de la lista.
        sendMissionMessage();
    }

    @Override
    public void listener(BlockBreakEvent event) {
        if (isCompleted()) {
            rewardPlayer();
            plugin.missionManager.removeMission(this);
            return;
        }

        if (event.getPlayer().equals(getPlayer())) {
            Block block = event.getBlock();
            if (block.getType().equals(target)) {
                addAmount();
                getPlayer().sendMessage(getProgressMessage()); // Envía progreso al jugador
            }
        }
    }

    @Override
    public void rewardPlayer() {
        getPlayer().sendMessage("§6¡Has completado la misión!");
        // Aquí podrías añadir una recompensa real (dinero, ítems, experiencia, etc.)
    }

    /**
     * Envía el mensaje de inicio de la misión.
     */
    private void sendMissionMessage() {
        getPlayer().sendMessage("§e⚒ ¡Nueva misión recibida! ⚒");
        getPlayer().sendMessage("§7El viejo minero te ha dado una tarea: romper §6" + amountTo + "§7 bloques de §b" + target.name() + "§7.");
        getPlayer().sendMessage("§7Empieza a picar y descubre qué esconden estos bloques.");
    }

    /**
     * Obtiene un mensaje con el progreso actual de la misión.
     *
     * @return Mensaje de progreso.
     */
    private String getProgressMessage() {
        return "§7Progreso: §b" + amount + "§7 / §6" + amountTo + "§7 bloques de §b" + target.name() + "§7 rotos.";
    }

    /**
     * Devuelve un bloque aleatorio de una lista predefinida.
     *
     * @return Material del bloque objetivo.
     */
    private Material getRandomBlock() {
        List<Material> blocks = List.of(
                Material.STONE, Material.DIORITE, Material.ANDESITE, Material.GRANITE,
                Material.IRON_ORE, Material.GOLD_ORE, Material.DEEPSLATE, Material.REDSTONE_ORE,Material.DIAMOND_ORE
        );
        return blocks.get(new Random().nextInt(blocks.size()));
    }
}
