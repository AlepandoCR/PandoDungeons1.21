package gambling.services

import org.bukkit.entity.Player
import java.util.UUID

/**
 * Interface for interacting with the player balance system.
 * This allows the gambling module to be independent of the specific economy implementation.
 */
interface PlayerBalanceService {

    /**
     * Gets the current balance of the specified player.
     *
     * @param player The player whose balance is to be checked.
     * @return The player's current balance, or 0.0 if an error occurs or player not found.
     */
    fun getBalance(player: Player): Double

    /**
     * Gets the current balance of the player identified by their UUID.
     * Useful for offline players or when the Player object is not available.
     *
     * @param playerId The UUID of the player.
     * @return The player's current balance, or 0.0 if an error occurs or player not found.
     */
    fun getBalance(playerId: UUID): Double

    /**
     * Adds the specified amount to the player's balance.
     *
     * @param player The player to whom the balance should be added.
     * @param amount The amount to add. Must be positive.
     * @return True if the balance was successfully added, false otherwise.
     */
    fun addBalance(player: Player, amount: Double): Boolean

    /**
     * Adds the specified amount to the balance of the player identified by their UUID.
     * Useful for offline players or when the Player object is not available.
     *
     * @param playerId The UUID of the player.
     * @param amount The amount to add. Must be positive.
     * @return True if the balance was successfully added, false otherwise.
     */
    fun addBalance(playerId: UUID, amount: Double): Boolean

    /**
     * Removes the specified amount from the player's balance.
     *
     * @param player The player from whom the balance should be removed.
     * @param amount The amount to remove. Must be positive.
     * @return True if the balance was successfully removed (e.g., player had sufficient funds), false otherwise.
     */
    fun removeBalance(player: Player, amount: Double): Boolean

    /**
     * Removes the specified amount from the balance of the player identified by their UUID.
     * Useful for offline players or when the Player object is not available.
     *
     * @param playerId The UUID of the player.
     * @param amount The amount to remove. Must be positive.
     * @return True if the balance was successfully removed, false otherwise.
     */
    fun removeBalance(playerId: UUID, amount: Double): Boolean
}
