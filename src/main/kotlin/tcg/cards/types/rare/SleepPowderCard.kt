package tcg.cards.types.rare

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardInstance
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.SomnolentDustSkill

object SleepPowderCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.RARE
        val skill = SomnolentDustSkill(plugin, rarity)

        CardFactory(plugin).build(
            skill = skill,
            rarity = rarity,
            id = "sleep_powder_card",
            cooldown = 35
        )
    }
}
