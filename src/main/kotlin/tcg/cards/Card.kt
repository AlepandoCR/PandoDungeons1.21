package tcg.cards

import org.bukkit.inventory.ItemStack
import tcg.CardRarity
import tcg.cards.skills.CardSkill

class Card(
    private val item: ItemStack,
    private val rarity: CardRarity,
    private val skill: CardSkill
) {
}