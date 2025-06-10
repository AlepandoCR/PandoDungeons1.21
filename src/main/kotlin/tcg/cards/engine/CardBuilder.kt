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
    private var cooldown: Int = dummyCard.cooldown


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

    fun setCooldown(int: Int) {
        this.cooldown = int
    }
    fun build(): Card {
        val r = Card(
            item!!,
            rarity,
            skill,
            id,
            cooldown
        )

        return r
    }

}