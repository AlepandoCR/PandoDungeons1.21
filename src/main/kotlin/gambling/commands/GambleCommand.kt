package gambling.commands

import gambling.core.GamblingManager
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import pandodungeons.PandoDungeons

class GambleCommand(
    private val plugin: PandoDungeons,
    private val gamblingManager: GamblingManager
) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("apostar", ignoreCase = true)) {
            handleNewBetCommand(sender, args)
            return true
        } else if (command.name.equals("gamblingadmin", ignoreCase = true)) {
            handleGamblingAdminCommand(sender, args)
            return true
        }
        return false
    }

    private fun handleNewBetCommand(sender: CommandSender, args: Array<out String>) {
        if (sender !is Player) {
            sender.sendMessage("This command can only be used by players.")
            return
        }

        if (args.isEmpty()) {
            sender.sendMessage("§eUsage: /newbet <game_type> [options...]")
            // Potentially list available game types or current game status
            val activeGameId = gamblingManager.getActiveGameId()
            if (activeGameId != null) {
                sender.sendMessage("§aCurrent active game: $activeGameId")
                sender.sendMessage("§7Status: ${gamblingManager.getActiveGameStatus()}")
            } else {
                sender.sendMessage("§7No game is currently active.")
                val availableGames = gamblingManager.getRegisteredGameIds()
                if (availableGames.isNotEmpty()) {
                    sender.sendMessage("§7Available game types: ${availableGames.joinToString(", ")}")
                } else {
                    sender.sendMessage("§7No game types are currently registered.")
                }
            }
            return
        }

        val gameType = args[0]
        // For now, hardcoding for horserace. Later, this should be dynamic.
        if (gameType.equals("horserace", ignoreCase = true)) {
            if (args.size < 3) {
                sender.sendMessage("§cUsage: /newbet horserace <horse_number> <amount>")
                return
            }
            val horseNumber = args[1].toIntOrNull()
            val amount = args[2].toIntOrNull()

            if (horseNumber == null || amount == null) {
                sender.sendMessage("§cInvalid horse number or amount.")
                return
            }

            if (gamblingManager.addBet(sender, horseNumber, amount)) {
                // Message already sent by HorseRaceGame.addBet or GamblingManager
            } else {
                // Message indicating failure already sent by HorseRaceGame/GamblingManager
            }
        } else {
            sender.sendMessage("§cUnknown game type: $gameType. Currently only 'horserace' is available.")
        }
    }

    private fun handleGamblingAdminCommand(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission("gambling.admin")) {
            sender.sendMessage(ChatColor.RED.toString() + "You do not have permission to use this command.")
            return
        }

        if (args.isEmpty()) {
            sender.sendMessage("§eGambling Admin Commands:")
            sender.sendMessage("§7/gamblingadmin start <game_type> - Starts a new game.")
            sender.sendMessage("§7/gamblingadmin stop - Stops the current game.")
            sender.sendMessage("§7/gamblingadmin status - Shows the status of the current game.")
            // TODO: Add command to list game types, reload configs, etc.
            return
        }

        when (args[0].lowercase()) {
            "start" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /gamblingadmin start <game_type>")
                    val availableGames = gamblingManager.getRegisteredGameIds()
                    if (availableGames.isNotEmpty()) {
                        sender.sendMessage("§7Available game types: ${availableGames.joinToString(", ")}")
                    } else {
                        sender.sendMessage("§7No game types are currently registered to start.")
                    }
                    return
                }
                val gameType = args[1]
                if (gamblingManager.startGame(gameType, sender as? Player)) {
                    sender.sendMessage("§aSuccessfully initiated game type: $gameType.")
                } else {
                    sender.sendMessage("§cFailed to start game type: $gameType. Check console for errors.")
                }
            }
            "stop" -> {
                if (gamblingManager.endGame(sender as? Player)) {
                    sender.sendMessage("§aSuccessfully stopped the active game.")
                } else {
                    sender.sendMessage("§cNo active game to stop or could not stop.")
                }
            }
            "status" -> {
                val status = gamblingManager.getActiveGameStatus()
                if (status != null) {
                    sender.sendMessage("§eCurrent Game Status (${gamblingManager.getActiveGameId()}):")
                    sender.sendMessage("§7$status")
                } else {
                    sender.sendMessage("§7No game is currently active.")
                }
            }
            else -> {
                sender.sendMessage("§cUnknown admin sub-command. Use /gamblingadmin for help.")
            }
        }
    }


    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()
        if (command.name.equals("newbet", ignoreCase = true)) {
            if (args.size == 1) {
                completions.addAll(gamblingManager.getRegisteredGameIds())
            } else if (args.size == 2 && args[0].equals("horserace", ignoreCase = true)) { // Assuming 'horserace' remains a known type for options
                // TODO: Get number of horses from HorseRaceConfig if possible, or default
                completions.addAll(listOf("1", "2", "3", "<horse_number>")) // Placeholder specific to horserace
            } else if (args.size == 3 && args[0].equals("horserace", ignoreCase = true)) {
                completions.add("<amount>")
            }
        } else if (command.name.equals("gamblingadmin", ignoreCase = true)) {
            if (args.size == 1) {
                completions.addAll(listOf("start", "stop", "status"))
            } else if (args.size == 2 && args[0].equals("start", ignoreCase = true)) {
                completions.addAll(gamblingManager.getRegisteredGameIds())
            }
        }
        return completions.filter { it.lowercase().startsWith(args.last().lowercase()) }.toMutableList()
    }
}
