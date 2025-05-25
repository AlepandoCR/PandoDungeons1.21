package gambling.core

import org.bukkit.entity.Player

interface GamblingGame {
    val gameId: String // Unique identifier for the game type
    val config: GameConfig

    fun startGame(initiator: Player? = null)
    fun addBet(player: Player, choice: Any, amount: Double): Boolean
    fun endGame(forced: Boolean = false)
    fun isActive(): Boolean
    fun getGameStatus(): String // Provides a string representation of the current game state
}
