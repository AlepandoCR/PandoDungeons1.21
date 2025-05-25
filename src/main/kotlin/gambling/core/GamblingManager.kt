package gambling.core

import gambling.games.horserace.HorseRaceGame // Will be created later
import gambling.services.PlayerBalanceService // Will be created later
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class GamblingManager(
    private val plugin: JavaPlugin,
    private val balanceService: PlayerBalanceService // To be implemented/provided later
) {
    private var activeGame: GamblingGame? = null
    
    // Stores a factory for the game AND its configuration.
    private val registeredGameFactories: MutableMap<String, () -> GamblingGame> = mutableMapOf()

    init {
        // Game types would be registered here by the main plugin, along with their specific configs
        // Example (would be called from outside, e.g. your plugin's onEnable):
        // val horseRaceConfig = loadHorseRaceConfigFromYaml() // Hypothetical function
        // registerGameType("horserace", horseRaceConfig) { config -> 
        // HorseRaceGame(plugin, config as HorseRaceConfig, balanceService, this) 
        // }
        // For now, the actual registration will be done in the main plugin part (Step 9)
    }

    /**
     * Registers a game type with its factory that includes necessary configuration.
     * The factory function is responsible for creating the game instance with its correct config.
     */
    fun registerGameType(gameId: String, gameFactory: () -> GamblingGame) {
        if (registeredGameFactories.containsKey(gameId)) {
            plugin.logger.warning("Game type '$gameId' is already registered. Overwriting factory.")
        }
        registeredGameFactories[gameId] = gameFactory
        plugin.logger.info("Registered gambling game type: $gameId")
    }

    fun startGame(gameId: String, initiator: Player? = null): Boolean {
        if (activeGame != null && activeGame!!.isActive()) {
            initiator?.sendMessage("§cThere is already an active gambling game.")
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
            activeGame = gameFactory() // The factory now provides the fully configured game
            activeGame?.startGame(initiator)
            plugin.logger.info("Started game: $gameId")
            true
        } catch (e: Exception) {
            initiator?.sendMessage("§cError starting game $gameId: ${e.message}")
            plugin.logger.severe("Error starting game $gameId:")
            e.printStackTrace()
            activeGame = null // Ensure activeGame is null if startup failed
            false
        }
    }

    // ... (endGame, addBet, getActiveGameStatus, getActiveGameId, notifyGameFinished remain largely the same)
    // Ensure `addBet` and other methods correctly handle `activeGame` potentially being null if startup fails.

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

    fun addBet(player: Player, choice: Any, amount: Double): Boolean {
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
