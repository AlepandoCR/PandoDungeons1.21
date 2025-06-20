package tcg.cards.types.rare

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.BoulderTossCardSkill
import tcg.cards.engine.CardInstance

object BoulderCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.RARE

        val skill = BoulderTossCardSkill(plugin, rarity)

        val factory = CardFactory(plugin)

        factory.build(
            skill,
            rarity,
            "boulder_card",
            60

        )
    }
}
