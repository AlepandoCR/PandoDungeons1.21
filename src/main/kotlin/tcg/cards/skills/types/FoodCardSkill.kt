package tcg.cards.skills.types

import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.text.Description
import tcg.util.text.SkillDescription

class FoodCardSkill(
    plugin: PandoDungeons,
    rarity: CardRarity
): CardSkill(plugin,rarity) {
    override fun startCondition(): Boolean {
        return true
    }

    override fun task() {
        if(hasPlayer()){
            getDummyPlayer().foodLevel += 10
        }
    }

    override fun setSkillType(): String {
        return "foodSkill"
    }

    override fun shouldWaitForCondition(): Boolean {
        return false
    }

    override fun setDescription(): Description {
       val r = SkillDescription("Aumenta en 10 el nivel de tu comida", rarity)

        return r
    }
}