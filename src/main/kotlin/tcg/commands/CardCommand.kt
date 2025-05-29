package tcg.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import pandodungeons.PandoDungeons

class CardCommand(
    private val plugin: PandoDungeons
): CommandExecutor, TabExecutor {


    init {
        plugin.getCommand("card")?.setExecutor(this)
    }

    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<out String>
    ): MutableList<String> {
        return plugin.cardManager.getAllIds()
    }

    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if(p0 is Player) {

            if(!p0.isOp) return false

            val id = p3[p3.lastIndex]

            val inventory = p0.inventory

            val card = plugin.cardManager.getCard(id) ?: return false

            val item = card.getItem()

            inventory.addItem(item)

            return true
        }

        return false
    }
}