package gambling.services

import org.bukkit.entity.Player
import pandodungeons.PandoDungeons
import java.util.UUID

interface PlayerBalanceService {
    fun getBalance(player: Player, plugin: PandoDungeons): Long

    fun getBalance(playerId: UUID, plugin: PandoDungeons): Long

    fun addBalance(player: Player, amount: Int, plugin: PandoDungeons): Boolean

    fun addBalance(player: UUID, amount: Int, plugin: PandoDungeons): Boolean

    fun removeBalance(player: Player, amount: Int, plugin: PandoDungeons): Boolean

    fun removeBalance(player: UUID, amount: Int, plugin: PandoDungeons): Boolean
}
