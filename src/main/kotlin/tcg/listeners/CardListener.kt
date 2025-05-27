package tcg.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import pandodungeons.PandoDungeons

class CardListener(private val plugin: PandoDungeons): Listener {

    @EventHandler
    fun useCard(event: PlayerInteractEvent){
        val player = event.player
        val item = player.inventory.itemInMainHand

        val reader = plugin.cardReader

        val card = reader.getCardFromManager(item) ?: return

        card.triggerSkill(player)
    }
}