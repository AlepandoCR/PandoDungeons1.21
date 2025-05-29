package tcg.cards.engine

import org.bukkit.inventory.ItemStack
import pandodungeons.PandoDungeons
import tcg.cards.skills.engine.CardSkill

class CardBuilder(
    plugin: PandoDungeons
) {
    private val dummyCard: DummyCard = DummyCard(plugin)

    private var item: ItemStack? = null
    private var rarity: CardRarity = dummyCard.rarity
    private var skill: CardSkill = dummyCard.skill
    private var id: String = dummyCard.id


    fun setItem(item: ItemStack){
        this.item = item
    }

    fun setRarity(rarity: CardRarity){
        this.rarity = rarity
    }

    fun setSkill(skill: CardSkill){
        this.skill = skill
    }

    fun setId(id: String){
        this.id = id
    }

    fun build(): Card {
        item ?: throw NullPointerException("Item is null")

        // item can't be null

        val r = Card(
            item!!,
            rarity,
            skill,
            id
        )

        return r
    }

}