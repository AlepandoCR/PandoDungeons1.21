package tcg.cards

import pandodungeons.PandoDungeons
import tcg.CardRarity
import tcg.cards.skills.DummySkill

class DummyCard(
    plugin: PandoDungeons
): AbstractCard(CardRarity.DUMMY,DummySkill(plugin),"dummy")
