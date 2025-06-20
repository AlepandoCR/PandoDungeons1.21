package tcg.cards.types.legendary

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardFactory
import tcg.cards.engine.CardInstance
import tcg.cards.engine.CardRarity
import tcg.cards.skills.types.RockBarrierSkill

object EarthenWallCard : CardInstance {
    override fun build(plugin: PandoDungeons) {
        val rarity = CardRarity.LEGENDARY
        val skill = RockBarrierSkill(plugin, rarity)

        CardFactory(plugin).build(
            skill = skill,
            rarity = rarity,
            id = "stone_wall_card",
            cooldown = 45
        )
    }
}
