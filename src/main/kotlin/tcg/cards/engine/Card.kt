package tcg.cards.engine

import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import tcg.cards.skills.engine.CardSkill

class Card(
    private val item: ItemStack,
    rarity: CardRarity,
    skill: CardSkill,
    id: String,
    cooldown: Int
): AbstractCard(rarity,skill,id, cooldown) {

    fun getItem(): ItemStack{
        return item
    }

}

enum class CardRarity {
    COMMON, RARE, EPIC, LEGENDARY, MYTHIC, DUMMY;

    fun toItemRarityFallback(): ItemRarity {
        return when (this) {
            COMMON -> ItemRarity.COMMON
            RARE -> ItemRarity.RARE
            EPIC, LEGENDARY -> ItemRarity.EPIC
            MYTHIC, DUMMY -> ItemRarity.UNCOMMON
        }
    }

}
