package tcg.cards

import tcg.CardRarity
import tcg.cards.skills.CardSkill

abstract class AbstractCard(internal val rarity: CardRarity, internal val skill: CardSkill, internal val id: String) {

    fun triggerSkill(){
        skill.trigger()
    }

}