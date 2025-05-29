package tcg.cards.types

import pandodungeons.PandoDungeons

interface CardInstance {

    fun build(plugin: PandoDungeons)
}