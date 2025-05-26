package gambling.core

import org.bukkit.entity.Player

interface GamblingGame {
    val gameId: String
    val config: GameConfig

    fun startGame(initiator: Player? = null)
    fun addBet(player: Player, choice: Any, amount: Int): Boolean
    fun endGame(forced: Boolean = false)
    fun isActive(): Boolean
    fun getGameStatus(): String
}
