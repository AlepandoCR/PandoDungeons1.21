package gambling.core

import gambling.games.horserace.HorseRaceGame
import gambling.services.PlayerBalanceService
import org.bukkit.entity.Player
import pandodungeons.PandoDungeons

class GamblingManager(
    private val plugin: PandoDungeons,
    private val balanceService: PlayerBalanceService
) {
    private var activeGame: GamblingGame? = null

    private val registeredGameFactories: MutableMap<String, () -> GamblingGame> = mutableMapOf()

    init {
        registerGameType("horserace") {
            HorseRaceGame(plugin, HorseRaceConfig(
                minBet = 100,
                maxBet = 25000000,
                minPlayers = 3,
                maxPlayers = 10,
                gameDurationSeconds = 60,
                numberOfHorses = 3,
                startLineLocation = "spawn,43.5,73,275",
                finishLineLocation = "SPAWN,37.5,73,275",
                horseSpawnOffset = 1.33
            ), balanceService, this)
        }

    }

    /**
     * Registers a game type with its factory that includes necessary configuration.
     * The factory function is responsible for creating the game instance with its correct config.
     */
    private fun registerGameType(gameId: String, gameFactory: () -> GamblingGame) {
        if (registeredGameFactories.containsKey(gameId)) {
            plugin.logger.warning("Game type '$gameId' is already registered. Overwriting factory.")
        }
        registeredGameFactories[gameId] = gameFactory
        plugin.logger.info("Registered gambling game type: $gameId")
    }

    fun startGame(gameId: String, initiator: Player? = null): Boolean {
        if (activeGame != null && activeGame!!.isActive()) {
            initiator?.sendMessage("§cYa hay un $gameId activo, usa §7/apostar")
            plugin.logger.warning("Attempted to start game '$gameId' while another game is active.")
            return false
        }

        val gameFactory = registeredGameFactories[gameId]
        if (gameFactory == null) {
            initiator?.sendMessage("§cUnknown game type: $gameId. Or it has not been configured.")
            plugin.logger.warning("Attempted to start unknown/unconfigured game type: $gameId")
            return false
        }
        
        return try {
            activeGame = gameFactory()
            activeGame?.startGame(initiator)
            plugin.logger.info("Started game: $gameId")
            true
        } catch (e: Exception) {
            initiator?.sendMessage("§cError starting game $gameId: ${e.message}")
            plugin.logger.severe("Error starting game $gameId:")
            e.printStackTrace()
            activeGame = null
            false
        }
    }


    fun endGame(forcedBy: Player? = null): Boolean {
        if (activeGame == null || !activeGame!!.isActive()) {
            forcedBy?.sendMessage("§cThere is no active game to end.")
            return false
        }
        activeGame?.endGame(forced = forcedBy != null)
        plugin.logger.info("Game ${activeGame?.gameId} ended. ${if (forcedBy != null) "Forced by ${forcedBy.name}." else ""}")
        // activeGame = null // This is now handled by notifyGameFinished or if endGame directly sets it
        return true
    }

    fun addBet(player: Player, choice: Any, amount: Int): Boolean {
        val currentActiveGame = activeGame
        if (currentActiveGame == null || !currentActiveGame.isActive()) {
            player.sendMessage("§cThere is no active game to bet on.")
            return false
        }
        return currentActiveGame.addBet(player, choice, amount)
    }

    fun getActiveGameStatus(): String? {
        return activeGame?.getGameStatus()
    }

    fun getActiveGameId(): String? {
        return activeGame?.gameId
    }
    
    fun notifyGameFinished(game: GamblingGame) {
        if (activeGame == game) {
            plugin.logger.info("Game ${game.gameId} reported it has finished. Clearing active game.")
            activeGame = null
        } else {
            plugin.logger.warning("A game (${game.gameId}) reported finishing, but it wasn't the active one (${activeGame?.gameId}). This might indicate a state issue.")
        }
    }
    
    fun getRegisteredGameIds(): Set<String> {
        return registeredGameFactories.keys.toSet()
    }
}
