package tcg.cards.skills

import pandodungeons.PandoDungeons

object SkillManager {

    private val registry: MutableList<CardSkill> = mutableListOf()

    fun registerSkill(skill: CardSkill) {
        registry.add(skill)
    }

    fun getSkillFromType(type: SkillType, plugin: PandoDungeons): CardSkill? {
        return registry.firstOrNull { it.getType().get().equals(type.get(), ignoreCase = true) }
    }
}
