package gambling.games.horserace

import gambling.core.Bet
import gambling.core.GamblingGame
import gambling.core.GamblingManager
import gambling.core.HorseRaceConfig
import gambling.services.PlayerBalanceService
import gambling.utils.LocationParser
import org.bukkit.*
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import pandodungeons.PandoDungeons
import java.util.*
import kotlin.math.atan2

class HorseRaceGame (
    private val plugin: PandoDungeons,
    override val config: HorseRaceConfig, // Specific config for this game
    private val balanceService: PlayerBalanceService,
    private val gamblingManager: GamblingManager // To notify when game is done
) : GamblingGame {

    override val gameId: String = "horserace"
    private var currentBets = mutableMapOf<Int, MutableList<Bet>>() // Horse ID to list of bets
    private var horses = mutableMapOf<Int, ArmorStand>()
    private var horseVelocities = mutableMapOf<Int, Double>()
    
    private var raceTask: BukkitTask? = null
    private var _isActive: Boolean = false

    private val startLine: Location by lazy { LocationParser.parseLocation(config.startLineLocation, plugin.server) ?: throw IllegalArgumentException("Invalid startLine location format: ${config.startLineLocation}") }
    private val finishLine: Location by lazy { LocationParser.parseLocation(config.finishLineLocation, plugin.server) ?: throw IllegalArgumentException("Invalid finishLine location format: ${config.finishLineLocation}") }
    private val raceDirection: Vector by lazy { finishLine.toVector().subtract(startLine.toVector()).normalize() }
    private val rightVector: Vector by lazy { raceDirection.clone().crossProduct(Vector(0, 1, 0)).normalize() }

    private val armorStandKey = NamespacedKey(plugin, "horse_id")

    init {
        for (i in 1..config.numberOfHorses) {
            currentBets[i] = mutableListOf()
        }
    }

    override fun startGame(initiator: Player?) {
        if (_isActive) {
            initiator?.sendMessage("§cThe Horse Race game is already active.")
            return
        }

        // Reset state from any previous game
        resetGame()

        // Spawn horses
        for (horseId in 1..config.numberOfHorses) {
            val horseEntity = spawnHorseEntity(horseId)
            if (horseEntity == null) {
                Bukkit.broadcastMessage("§cError: Could not spawn horse $horseId. Cancelling game.")
                plugin.logger.severe("[HorseRaceGame] Failed to spawn horse $horseId at ${calculateHorseStartLocation(horseId)}")
                endGame(true) // Force end due to error
                return
            }
            horses[horseId] = horseEntity
        }
        
        _isActive = true
        Bukkit.broadcastMessage("§aUna carrera de caballos va a comenzar! Usa §e/apostar horserace <numero_caballo> <dinero> §apara poner tus apuestas!")
        plugin.logger.info("[HorseRaceGame] Started. Waiting for bets. Start line: $startLine, Finish line: $finishLine")

        // TODO: Implement a timeout for betting or a command to manually start the race if needed.
        // For now, race starts when conditions are met via addBet or a manual start command (to be added).
    }

    private fun calculateHorseStartLocation(horseId: Int): Location {
        // OffsetIndex should ensure symmetric distribution around the center of the start line if possible
        // For N horses, offsets could be -(N-1)/2 * spacing, ..., 0, ..., (N-1)/2 * spacing
        val centralOffsetIndex = horseId - ((config.numberOfHorses + 1) / 2.0)
        val offset = rightVector.clone().multiply(config.horseSpawnOffset * centralOffsetIndex)
        return startLine.clone().add(offset)
    }

    private fun spawnHorseEntity(horseId: Int): ArmorStand? {
        val spawnLoc = calculateHorseStartLocation(horseId)
        if (spawnLoc.world == null) {
            plugin.logger.severe("[HorseRaceGame] World is null for horse $horseId at $spawnLoc")
            return null
        }

        val headItemStack = ItemStack(when (horseId % 5) { // Cycle through a few head types
            1 -> Material.PLAYER_HEAD // Can keep one as player head, will use default skin or a configured one
            2 -> Material.ZOMBIE_HEAD
            3 -> Material.SKELETON_SKULL
            4 -> Material.CREEPER_HEAD
            0 -> Material.PIGLIN_HEAD // Or Material.DRAGON_HEAD if available and desired
            else -> Material.PLAYER_HEAD // Default
        })

        if (headItemStack.type == Material.PLAYER_HEAD) {
            val skullMeta = headItemStack.itemMeta as SkullMeta
            // You could set a specific offline player here if you have a UUID/name for a desired default skin
            // For example: skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Chicken"))
            // If not set, it will be a default Steve/Alex skin.
            // The config could later specify a player name or texture value for this.
            skullMeta.setDisplayName("§6Horse $horseId")
            headItemStack.itemMeta = skullMeta
        } else {
             val itemMeta = headItemStack.itemMeta
             if (itemMeta != null) {
                itemMeta.setDisplayName("§6Horse $horseId")
                headItemStack.itemMeta = itemMeta
             }
        }


        return spawnLoc.world?.spawn(spawnLoc, ArmorStand::class.java) { armorStand ->
            armorStand.setGravity(false)
            armorStand.isVisible = false
            armorStand.isInvulnerable = true
            armorStand.removeWhenFarAway = false
            armorStand.isCollidable = false
            armorStand.isMarker = true
            armorStand.customName = "§6Horse $horseId"
            armorStand.isCustomNameVisible = true
            armorStand.equipment.helmet = headItemStack
            armorStand.persistentDataContainer.set(armorStandKey, PersistentDataType.INTEGER, horseId)
        }
    }

    override fun addBet(player: Player, choice: Any, amount: Int): Boolean {
        if (!_isActive) {
            player.sendMessage("§cThe race has not started or is already over.")
            return false
        }

        val horseId = choice as? Int ?: run {
            player.sendMessage("§cInvalid horse number.")
            return false
        }

        if (horseId !in 1..config.numberOfHorses) {
            player.sendMessage("§cInvalid horse number. Choose between 1 and ${config.numberOfHorses}.")
            return false
        }
        
        if (amount < config.minBet) {
            player.sendMessage("§cYour bet must be at least ${config.minBet}.")
            return false
        }
        config.maxBet?.let {
            if (amount > it) {
                player.sendMessage("§cYour bet cannot exceed $it.")
                return false
            }
        }

        // Check if player has already bet
        if (currentBets.values.flatten().any { it.playerId == player.uniqueId }) {
            player.sendMessage("§cYou have already placed a bet in this race.")
            return false
        }
        
        // Check player balance
        if (balanceService.getBalance(player,plugin) < amount) {
            player.sendMessage("§cYou do not have enough money to place this bet.")
            return false
        }

        // Deduct balance
        if (!balanceService.removeBalance(player, amount, plugin)) {
            player.sendMessage("§cCould not deduct funds for the bet. Please try again.")
            plugin.logger.warning("[HorseRaceGame] Failed to remove balance for player ${player.name} for bet amount $amount")
            return false
        }

        val bet = Bet(player.uniqueId, player.name, horseId, amount)
        currentBets[horseId]?.add(bet)
        player.sendMessage("§aYou placed a bet of $amount on Horse $horseId.")
        Bukkit.broadcastMessage("§e${player.name} bet $amount on Horse $horseId!")

        // Condition to start the race (e.g., enough total bets, or bets on all horses)
        // This is an improvement over the old "allHorsesHaveBet" which could stall the game.
        // Let's say race starts if at least config.minPlayers have bet in total.
        val totalBets = currentBets.values.sumOf { it.size }
        if (totalBets >= config.minPlayers && raceTask == null) { // Ensure race doesn't start multiple times
             // Optional: Check if each horse has at least one bet if that's desired
            // val allHorsesHaveAtLeastOneBet = currentBets.all { it.value.isNotEmpty() }
            // if(allHorsesHaveAtLeastOneBet) startRaceMechanics()
            startRaceMechanics()
        }
        return true
    }

    private fun startRaceMechanics() {
        if (raceTask != null) {
            plugin.logger.warning("[HorseRaceGame] Attempted to start race mechanics while already running.")
            return
        }
        Bukkit.broadcastMessage("§bThe race has begun!")
        assignUniqueVelocities()

        raceTask = object : BukkitRunnable() {
            var ticksElapsed = 0
            override fun run() {
                if (!_isActive) { // Game might have been force-stopped
                    cancel()
                    return
                }

                // Re-assign velocities periodically for more dynamic race (optional)
                if (ticksElapsed > 0 && ticksElapsed % (20 * 10) == 0) { // Every 10 seconds
                     assignUniqueVelocities()
                     Bukkit.broadcastMessage("§7The horses are adjusting their pace!")
                }


                var winner: Int? = null
                horses.forEach { (id, horse) ->
                    val speed = horseVelocities[id] ?: 0.05 // Default speed if not found
                    
                    val currentLoc = horse.location
                    val nextLoc = currentLoc.clone().add(raceDirection.clone().multiply(speed))
                    nextLoc.yaw = Location.normalizeYaw((Math.toDegrees(atan2(-raceDirection.x, raceDirection.z))).toFloat()) // Make horse face direction
                    
                    horse.teleport(nextLoc)
                    horse.world.spawnParticle(Particle.SMOKE, horse.location.add(0.0, 0.5, 0.0), 3, 0.1, 0.1, 0.1, 0.01)

                    val horseSpecificFinishLine = calculateHorseFinishLine(id)
                    if (hasCrossedFinishLine(horse.location, horseSpecificFinishLine)) {
                        winner = id
                        cancel() // Stop this runnable
                        return@forEach // Exit loop early
                    }
                }

                if (winner != null) {
                    announceWinnerAndPayout(winner!!)
                    endGame()
                } else if (config.gameDurationSeconds != null && ticksElapsed >= config.gameDurationSeconds * 20) {
                    Bukkit.broadcastMessage("§cThe race timed out! No clear winner.")
                    endGame(true) // Force end due to timeout
                }
                ticksElapsed += 5 // Assuming task runs every 5 ticks
            }
        }.runTaskTimer(plugin, 0L, 5L) // Run every 5 ticks
    }
    
    private fun calculateHorseFinishLine(horseId: Int): Location {
        val centralOffsetIndex = horseId - ((config.numberOfHorses + 1) / 2.0)
        val offset = rightVector.clone().multiply(config.horseSpawnOffset * centralOffsetIndex)
        return finishLine.clone().add(offset)
    }


    private fun assignUniqueVelocities() {
        val assignedSpeeds = HashSet<Double>()
        val random = Random()
        for (id in 1..config.numberOfHorses) {
            var speed: Double
            do {
                speed = 0.05 + (0.15 - 0.05) * random.nextDouble() // Speed between 0.05 and 0.15
                speed = String.format("%.4f", speed).toDouble() // Limit precision to avoid floating point comparison issues
            } while (assignedSpeeds.contains(speed))
            assignedSpeeds.add(speed)
            horseVelocities[id] = speed
        }
        // plugin.logger.info("[HorseRaceGame] Assigned velocities: $horseVelocities")
    }

    private fun hasCrossedFinishLine(horseLoc: Location, specificFinishLine: Location): Boolean {
        // Project horse's current position and finish line onto the race direction vector relative to the start
        val startToHorse = horseLoc.toVector().subtract(startLine.toVector())
        val startToFinish = specificFinishLine.toVector().subtract(startLine.toVector())
        return startToHorse.dot(raceDirection) >= startToFinish.dot(raceDirection)
    }

    private fun announceWinnerAndPayout(winningHorseId: Int) {
        Bukkit.broadcastMessage("§6§lHorse $winningHorseId has won the race!")

        val winningBets = currentBets[winningHorseId] ?: emptyList()
        val allBets = currentBets.values.flatten()
        
        val totalAmountBetOnWinner = winningBets.sumOf { it.amount }
        val totalAmountBetByLosers = allBets.filter { it.choice as Int != winningHorseId }.sumOf { it.amount }

        if (winningBets.isEmpty()) {
            Bukkit.broadcastMessage("§eUnfortunately, no one bet on Horse $winningHorseId. The house keeps the bets from other horses!")
            // Money already deducted, so nothing more to do other than log if needed.
            plugin.logger.info("[HorseRaceGame] No winning bets for horse $winningHorseId. Losers' money ($totalAmountBetByLosers) is effectively kept.")
            return
        }

        winningBets.forEach { bet ->
            val player = Bukkit.getPlayer(bet.playerId)
            // Payout: Bet amount back + proportional share of losers' pool
            // Proportional share is (player's winning bet / total winning bets) * total losers' pool
            val proportionalShareOfLosersPool = if (totalAmountBetOnWinner > 0) {
                (bet.amount / totalAmountBetOnWinner) * totalAmountBetByLosers
            } else 0
            
            val payout = bet.amount + proportionalShareOfLosersPool
            
            if (player != null && player.isOnline) {
                balanceService.addBalance(player, payout, plugin)
                player.sendMessage("§aCongratulations! You won ${String.format("%.2f", payout)} coins from your bet on Horse $winningHorseId.")
            } else {
                // Handle offline player payout if necessary (e.g., queueing, or direct DB update via balanceService)
                plugin.logger.info("[HorseRaceGame] Player ${bet.playerName} (UUID: ${bet.playerId}) won $payout but is offline. Balance service should handle offline updates if configured.")
                // Assuming balanceService can handle offline UUIDs if player not found.
                 balanceService.addBalance(bet.playerId, payout, plugin)
            }
        }
        
        allBets.filter { it.choice as Int != winningHorseId }.forEach { bet ->
             val player = Bukkit.getPlayer(bet.playerId)
             if (player != null && player.isOnline) {
                 player.sendMessage("§cSorry, your bet on Horse ${bet.choice} did not win.")
             }
        }
    }
    
    private fun resetGame() {
        raceTask?.cancel()
        raceTask = null
        
        horses.values.forEach { it.remove() }
        horses.clear()
        
        currentBets.forEach { (_, betsList) -> betsList.clear() }
        // Reinitialize currentBets for all horses
        for (i in 1..config.numberOfHorses) {
            currentBets[i] = mutableListOf()
        }
        
        horseVelocities.clear()
        _isActive = false
        plugin.logger.info("[HorseRaceGame] Game has been reset.")
    }

    override fun endGame(forced: Boolean) {
        if (!_isActive) return

        if (forced) {
            Bukkit.broadcastMessage("§cThe Horse Race game has been forcibly ended.")
            plugin.logger.info("[HorseRaceGame] Forcibly ended.")
        } else {
             plugin.logger.info("[HorseRaceGame] Naturally ended.")
        }
        
        resetGame()
        gamblingManager.notifyGameFinished(this) // Notify manager
    }

    override fun isActive(): Boolean = _isActive

    override fun getGameStatus(): String {
        if (!_isActive) return "Horse Race is not active."
        if (raceTask == null) return "Horse Race is active and waiting for bets. Horses: ${config.numberOfHorses}. Min Bet: ${config.minBet}."
        
        val horsePositions = horses.map { (id, horse) ->
            val dist = horse.location.distance(startLine) // Simple distance, could be projection
            "Horse $id: ${String.format("%.2f", dist)}m"
        }.joinToString(", ")
        return "Horse Race in progress! Horses: $horsePositions"
    }
}
