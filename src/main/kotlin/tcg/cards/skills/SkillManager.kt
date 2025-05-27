package tcg.cards.skills

import pandodungeons.PandoDungeons
import tcg.cards.skills.types.FoodCardSkill

class SkillManager(
    private val plugin: PandoDungeons
) {

    private val registry: MutableList<CardSkill> = mutableListOf()

    init {
        registerSkill(FoodCardSkill(plugin))
    }

    private fun registerSkill(skill: CardSkill) {
        registry.add(skill)
    }

    fun getSkillFromType(type: SkillType): CardSkill? {
        return registry.firstOrNull { it.getType().get().equals(type.get(), ignoreCase = true) }
    }
}
