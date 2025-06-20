package tcg.cards.types.common

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.ApplySalveSkill
import tcg.cards.engine.CardInstance

object HealingHerbCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.COMMON
        val skill = ApplySalveSkill(plugin, rarity)

        CardFactory(plugin).build(
            skill = skill,
            rarity = rarity,
            id = "healing_herb_card",
            cooldown = 20
        )
    }
}
