package gambling.core

interface GameConfig {
    val minBet: Int
    val maxBet: Int?
    val minPlayers: Int
    val maxPlayers: Int?
    val gameDurationSeconds: Int?
}


class HorseRaceConfig(
    override val minBet: Int = 100,
    override val maxBet: Int? = 1000,
    override val minPlayers: Int = 1,
    override val maxPlayers: Int? = null,
    override val gameDurationSeconds: Int? = 120,
    val numberOfHorses: Int = 3,
    val startLineLocation: String,
    val finishLineLocation: String,
    val horseSpawnOffset: Double = 1.5
) : GameConfig
