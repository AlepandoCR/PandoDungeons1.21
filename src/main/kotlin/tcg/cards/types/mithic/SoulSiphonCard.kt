package tcg.cards.types.mithic

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardInstance
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.EssenceDrainSkill

object SoulSiphonCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.MYTHIC
        val skill = EssenceDrainSkill(plugin, rarity)

        CardFactory(plugin).build(
            skill = skill,
            rarity = rarity,
            id = "soul_siphon_card",
            cooldown = 60
        )
    }
}
