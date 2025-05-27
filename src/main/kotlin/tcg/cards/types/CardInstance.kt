package tcg.cards.types

import pandodungeons.PandoDungeons
import tcg.cards.Card

interface CardInstance {

    fun build(plugin: PandoDungeons): Card
}