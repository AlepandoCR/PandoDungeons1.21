package tcg.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.ItemStack
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardReader
import java.util.*

class CardListener(plugin: PandoDungeons): Listener {

    private val cooldowns = mutableMapOf<UUID, MutableMap<ItemStack, Long>>()

    private val reader: CardReader = plugin.cardReader

    @EventHandler
    fun useCard(event: PlayerInteractEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val item = player.inventory.itemInMainHand

        val card = reader.getCardFromManager(item) ?: return

        val playerCooldowns = cooldowns.computeIfAbsent(uuid) { mutableMapOf() }

        val now = System.currentTimeMillis()
        val cooldownEnd = playerCooldowns[item] ?: 0L

        if (cooldownEnd > now) {
            return
        }

        card.triggerSkill(player)

        val cooldownMillis = card.cooldown * 50L
        playerCooldowns[item] = now + cooldownMillis

        player.setCooldown(item.type, card.cooldown)
    }

    @EventHandler
    fun reapplyCooldown(event: PlayerItemHeldEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val item = player.inventory.getItem(event.newSlot) ?: return

        reader.getCardFromManager(item) ?: return

        val now = System.currentTimeMillis()
        val playerCooldowns = cooldowns[uuid] ?: return
        val cooldownEnd = playerCooldowns[item] ?: return

        if (cooldownEnd > now) {
            val remaining = cooldownEnd - now
            val ticks = (remaining / 50L).toInt()
            if (ticks > 0) {
                player.setCooldown(item.type, ticks)
            }
        } else {
            player.setCooldown(item.type, 0)
        }
    }
}
