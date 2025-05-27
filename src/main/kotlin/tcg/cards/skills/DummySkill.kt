package tcg.cards.skills

import pandodungeons.PandoDungeons

class DummySkill(
    plugin: PandoDungeons,
) : CardSkill(plugin) {

    override fun startCondition(): Boolean {
        return true
    }

    override fun task() {
        getDummyPlayer()?.sendMessage("§aDummySkill activado con éxito.")
        plugin.logger.info("DummySkill ejecutado para ${getDummyPlayer()?.name}")
    }

    override fun setSkillType(): String {
        return "dummy"
    }

    override fun shouldWaitForCondition(): Boolean {
        return false
    }
}
