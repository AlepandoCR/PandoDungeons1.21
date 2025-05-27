package tcg.cards

import org.bukkit.entity.Player
import tcg.cards.skills.CardSkill

abstract class AbstractCard(internal val rarity: CardRarity, internal val skill: CardSkill, internal val id: String) {

    fun triggerSkill(){
        skill.trigger()
    }

    fun triggerSkill(player: Player){
        skill.trigger(player)
    }

}