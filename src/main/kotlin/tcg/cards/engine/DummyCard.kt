package tcg.cards.engine

import pandodungeons.PandoDungeons
import tcg.cards.skills.types.DummySkill

class DummyCard(
    plugin: PandoDungeons
): AbstractCard(CardRarity.DUMMY, DummySkill(plugin),"dummy")
