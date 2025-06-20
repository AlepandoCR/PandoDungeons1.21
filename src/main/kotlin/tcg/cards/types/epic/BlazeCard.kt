package tcg.cards.types.epic

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.FireballCardSkill
import tcg.cards.engine.CardInstance

object BlazeCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.EPIC

        val skill = FireballCardSkill(plugin, rarity)

        val factory = CardFactory(plugin)

        factory.build(
            skill,
            rarity,
            "blaze_card",
            40

        )
    }
}
