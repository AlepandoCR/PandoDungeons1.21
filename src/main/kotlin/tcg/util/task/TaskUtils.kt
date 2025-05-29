package tcg.util.task

import org.bukkit.scheduler.BukkitRunnable
import pandodungeons.PandoDungeons

object TaskUtils {

    fun doLater(action: () -> Unit, time: Long, plugin: PandoDungeons){
        object : BukkitRunnable(){
            override fun run() {
                action.invoke()
            }

        }.runTaskLater(plugin,time)
    }
}