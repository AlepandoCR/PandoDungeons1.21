package tcg.cards.skills.engine

import pandodungeons.PandoDungeons

class SkillManager(
    private val plugin: PandoDungeons
) {

    private val registry: MutableList<CardSkill> = mutableListOf()

    fun registerSkill(skill: CardSkill) {
        registry.add(skill)
    }

    fun getSkillFromType(type: SkillType): CardSkill? {
        return registry.firstOrNull { it.getType().get().equals(type.get(), ignoreCase = true) }
    }
}
