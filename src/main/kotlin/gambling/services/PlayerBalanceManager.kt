package gambling.services

import org.bukkit.entity.Player
import pandodungeons.PandoDungeons
import java.util.*

object PlayerBalanceManager : PlayerBalanceService {

    override fun getBalance(player: Player, plugin: PandoDungeons): Int {
        return plugin.rpgManager.getPlayer(player).coins
    }

    override fun getBalance(playerId: UUID, plugin: PandoDungeons): Int {
        return plugin.rpgManager.getPlayer(playerId).coins
    }

    override fun addBalance(player: Player, amount: Int, plugin: PandoDungeons): Boolean {
        val playerData = plugin.rpgManager.getPlayer(player)
        playerData.coins += amount
        return true
    }

    override fun addBalance(player: UUID, amount: Int, plugin: PandoDungeons): Boolean {
        val playerData = plugin.rpgManager.getPlayer(player)
        playerData.coins += amount
        return true
    }

    override fun removeBalance(player: Player, amount: Int, plugin: PandoDungeons): Boolean {
        val playerData = plugin.rpgManager.getPlayer(player)
        if (playerData.coins < amount) return false
        playerData.coins -= amount
        return true
    }

    override fun removeBalance(player: UUID, amount: Int, plugin: PandoDungeons): Boolean {
        val playerData = plugin.rpgManager.getPlayer(player)
        if (playerData.coins < amount) return false
        playerData.coins -= amount
        return true
    }
}