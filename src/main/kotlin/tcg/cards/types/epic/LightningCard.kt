package tcg.cards.types.epic

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.ChainLightningCardSkill
import tcg.cards.engine.CardInstance


object LightningCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.EPIC

        val skill = ChainLightningCardSkill(plugin, rarity)

        val factory = CardFactory(plugin)

        factory.build(
            skill,
            rarity,
            "lightning_card",
            30

        )
    }
}
