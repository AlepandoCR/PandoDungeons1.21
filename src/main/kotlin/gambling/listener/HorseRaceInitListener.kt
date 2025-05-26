package gambling.listener

import gambling.core.GamblingManager
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import pandodungeons.PandoDungeons

class HorseRaceInitListener(private val plugin: PandoDungeons) : Listener {

    @EventHandler
    fun startGame(event: PlayerInteractEntityEvent){
        val player = event.player
        val entity = event.rightClicked

        val gamblingManager = plugin.gamblingManager

        if (filterVillager(entity)) return

        start(gamblingManager, player)
    }

    private fun filterVillager(entity: Entity): Boolean {
        return !(entity is Villager && entity.scoreboardTags.contains("horserace"))
    }

    private fun start(gamblingManager: GamblingManager, player: Player) {
        if (gamblingManager.startGame("horserace", player)) {
            player.sendMessage("§c[§6Casino§c] §7Has iniciado una carrera de caballos, espera las apuestas")
        }
    }
}