package tcg.util.text

import net.kyori.adventure.text.format.NamedTextColor
import tcg.cards.engine.CardRarity

class SkillDescription(
    descriptionLine: String,
    rarity: CardRarity
) : Description() {

    init {
        val rarityColor = when (val itemRarity = rarity.toItemRarityFallback().color()) {
            NamedTextColor.WHITE -> NamedTextColor.DARK_GRAY
            else -> itemRarity
        }

        addStyledText("Habilidad:", NamedTextColor.GRAY)
        addNewLine()
        addStyledText("- ", NamedTextColor.GOLD)
        addStyledText(descriptionLine, NamedTextColor.DARK_GRAY)
        addNewLine()
        addStyledText("Rareza", NamedTextColor.GRAY)
        addNewLine()
        addStyledText("- ", NamedTextColor.GOLD)
        addStyledText(rarity.name, rarityColor)
    }
}
