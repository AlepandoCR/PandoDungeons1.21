package tcg.cards.engine

import pandodungeons.PandoDungeons

interface CardInstance {

    fun build(plugin: PandoDungeons)
}