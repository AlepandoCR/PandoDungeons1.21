package tcg.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import pandodungeons.PandoDungeons
import java.util.*

class CardListener(private val plugin: PandoDungeons): Listener {


    private val cooldowns = mutableSetOf<UUID>()

    @EventHandler
    fun useCard(event: PlayerInteractEvent){
        val player = event.player
        val uuid = player.uniqueId

        val item = player.inventory.itemInMainHand
        val reader = plugin.cardReader
        val card = reader.getCardFromManager(item) ?: return

        card.triggerSkill(player)


        cooldowns.add(uuid)
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            cooldowns.remove(uuid)
        }, 20L)
    }
}
