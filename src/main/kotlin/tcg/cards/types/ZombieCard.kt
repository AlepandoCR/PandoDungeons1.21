package tcg.cards.types

import pandodungeons.PandoDungeons
import tcg.cards.CardFactory
import tcg.cards.CardRarity
import tcg.cards.Card
import tcg.cards.skills.types.FoodCardSkill

class ZombieCard : CardInstance {
    override fun build(plugin: PandoDungeons): Card {
        return CardFactory(plugin).build(
            FoodCardSkill(plugin),
            CardRarity.COMMON,
            "zombie_card"
        )
    }
}
