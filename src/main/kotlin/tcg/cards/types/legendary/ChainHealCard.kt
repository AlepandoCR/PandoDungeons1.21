package tcg.cards.types.legendary

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardInstance
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.VitalityLinkSkill

object ChainHealCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.EPIC
        val skill = VitalityLinkSkill(plugin, rarity)

        CardFactory(plugin).build(
            skill = skill,
            rarity = rarity,
            id = "chain_heal_card",
            cooldown = 70
        )
    }
}
