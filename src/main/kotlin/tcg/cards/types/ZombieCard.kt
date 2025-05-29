package tcg.cards.types

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.FoodCardSkill

object ZombieCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.COMMON

        val skill = FoodCardSkill(plugin, rarity)

        plugin.skillManager.registerSkill(skill)

        val card = CardFactory(plugin).build(
            skill,
            rarity,
            "zombie_card"
        )

        plugin.cardManager.registerCard(card)
    }
}
