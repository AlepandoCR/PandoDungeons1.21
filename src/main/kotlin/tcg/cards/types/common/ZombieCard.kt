package tcg.cards.types.common

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.FoodCardSkill
import tcg.cards.engine.CardInstance

object ZombieCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.COMMON

        val skill = FoodCardSkill(plugin, rarity)

        val factory = CardFactory(plugin)

        factory.build(
            skill,
            rarity,
            "zombie_card",
            200
        )
    }
}
