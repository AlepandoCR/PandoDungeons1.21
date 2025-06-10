package tcg.cards.skills.types

import net.kyori.adventure.text.format.TextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.text.Description

class DummySkill(
    plugin: PandoDungeons
) : CardSkill(plugin, CardRarity.DUMMY) {

    override fun startCondition(): Boolean {
        return true
    }

    override fun task() {
        getDummyPlayer().sendMessage("§aDummySkill activado con éxito.")
        plugin.logger.info("DummySkill ejecutado para ${getDummyPlayer().name}")
    }

    override fun setSkillType(): String {
        return "dummy"
    }

    override fun shouldWaitForCondition(): Boolean {
        return false
    }

    override fun setDescription(): Description {
        val r = Description().apply {
            addStyledText("Habilidad:", TextColor.fromHexString("c9b6a9"))
            addNewLine()
            addStyledText("- DummySkill")
        }

        return r
    }

    override fun listener(): Listener {
        return object : Listener {
            @EventHandler
            fun dummyListener(event: PlayerMoveEvent){
                val player = getDummyPlayer()
                if(event.player == player){
                    player.sendMessage("Dummy Test")
                }
            }
        }
    }

    override fun listenerPeriod(): Long {
        return 40L
    }
}
