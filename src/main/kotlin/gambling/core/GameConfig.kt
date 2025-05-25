package gambling.core

interface GameConfig {
    val minBet: Double
    val maxBet: Double? // Optional max bet
    val minPlayers: Int
    val maxPlayers: Int? // Optional max players
    val gameDurationSeconds: Int? // Optional game duration
}

// Example specific config for a game like HorseRace
data class HorseRaceConfig(
    override val minBet: Double = 100.0,
    override val maxBet: Double? = 1000.0,
    override val minPlayers: Int = 1, // Minimum players to bet on a single horse for it to run
    override val maxPlayers: Int? = null, // No overall player limit for betting
    override val gameDurationSeconds: Int? = 120, // e.g., timeout for the race
    val numberOfHorses: Int = 3,
    val startLineLocation: String, // Placeholder for serialized location
    val finishLineLocation: String, // Placeholder for serialized location
    val horseSpawnOffset: Double = 1.5
) : GameConfig
