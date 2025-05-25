package gambling.core

import org.bukkit.entity.Player
import java.util.UUID

sealed class GameResult {
    data class WinnerAnnounced(
        val winnerInfo: Any, // Could be a player, a horse ID, a team, etc.
        val payouts: Map<UUID, Double> // Player UUID to payout amount
    ) : GameResult()

    data class Draw(val message: String) : GameResult()
    data class Cancelled(val reason: String) : GameResult()
    object NoResult : GameResult() // For games that might end without a specific winner/draw
}
