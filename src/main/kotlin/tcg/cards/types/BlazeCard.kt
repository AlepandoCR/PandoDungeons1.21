package tcg.cards.types

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.FireballCardSkill

object BlazeCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.EPIC

        val skill = FireballCardSkill(plugin, rarity)

        plugin.skillManager.registerSkill(skill)

        val card = CardFactory(plugin).build(
            skill,
            rarity,
            "blaze_card"
        )

        plugin.cardManager.registerCard(card)
    }
}
