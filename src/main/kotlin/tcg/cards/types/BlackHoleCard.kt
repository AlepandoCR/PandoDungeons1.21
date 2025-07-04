package tcg.cards.types

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.GravitonPullCardSkill

object BlackHoleCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.LEGENDARY

        val skill = GravitonPullCardSkill(plugin, rarity)

        val factory = CardFactory(plugin)

        factory.build(
            skill,
            rarity,
            "black_hole",
            120

        )
    }
}
