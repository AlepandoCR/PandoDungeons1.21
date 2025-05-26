package gambling.core

import java.util.*

data class Bet(
    val playerId: UUID,
    val playerName: String,
    val choice: Any,
    val amount: Int
)
