package tcg.cards

import org.bukkit.inventory.ItemStack
import tcg.CardRarity
import tcg.cards.skills.CardSkill

class Card(
    private val item: ItemStack,
    rarity: CardRarity,
    skill: CardSkill,
    id: String
): AbstractCard(rarity,skill,id) {

    fun getItem(): ItemStack{
        return item
    }

}