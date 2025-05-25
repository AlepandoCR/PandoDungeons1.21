package gambling.core

import org.bukkit.entity.Player
import java.util.UUID

data class Bet(
    val playerId: UUID, // Store player UUID to avoid issues with Player object lifecycle
    val playerName: String, // For display purposes
    val choice: Any, // Game-specific choice (e.g., horse number, card, etc.)
    val amount: Double
)
