package tcg.cards.skills.types

import pandodungeons.PandoDungeons
import tcg.cards.skills.CardSkill

class FoodCardSkill(
    plugin: PandoDungeons
): CardSkill(plugin) {
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
}