package tcg.cards.types

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.ChargeSkill

object GoatCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.RARE

        val skill = ChargeSkill(plugin, rarity)

        val factory = CardFactory(plugin)

        factory.build(
            skill,
            rarity,
            "goat_card",
            80
        )
    }
}
