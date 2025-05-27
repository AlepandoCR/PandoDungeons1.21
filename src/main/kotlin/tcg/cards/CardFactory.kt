package tcg.cards

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import pandodungeons.PandoDungeons
import tcg.cards.skills.CardSkill
import tcg.cards.skills.SkillType
import tcg.util.item.ItemDataAccess

class CardFactory(
    private val plugin: PandoDungeons
) {
    private val tag = NamespacedKey(plugin,"mapacho_card")
    private val rarityNamespace  = NamespacedKey(plugin,"card_rarity")
    private val skillNamespace = NamespacedKey(plugin,"card_skill")
    private val idNamespace = NamespacedKey(plugin,"card_id")

    private fun tagCard(itemStack: ItemStack){
        ItemDataAccess.insertData(itemStack, tag , "2025", PersistentDataType.STRING)
    }

    private fun putRarity(itemStack: ItemStack, rarity: CardRarity){
        ItemDataAccess.insertData(itemStack, rarityNamespace, rarity.name ,PersistentDataType.STRING)
    }

    private fun putSkill(itemStack: ItemStack, skillType: SkillType){
        ItemDataAccess.insertData(itemStack, skillNamespace, skillType.get(), PersistentDataType.STRING)
    }

    private fun putId(itemStack: ItemStack, id: String){
        ItemDataAccess.insertData(itemStack, idNamespace, id, PersistentDataType.STRING)
    }

    fun build(skill: CardSkill, rarity: CardRarity, id: String): Card {
        val item = createPhysical(id, skill, rarity)

        return CardBuilder(plugin).apply {
            setItem(item)
            setSkill(skill)
            setRarity(rarity)
            setId(id)
        }.build()
    }

    private fun createPhysical(
        id: String,
        skill: CardSkill,
        rarity: CardRarity
    ): ItemStack {
        val item = buildItem(id)

        putId(item, id)

        putSkill(item, skill.getType())

        putRarity(item, rarity)

        tagCard(item)

        return item
    }

    private fun buildItem(id: String): ItemStack{
        val dummy = ItemStack(Material.PAPER)
        val meta = dummy.itemMeta
        val modelData = meta.customModelDataComponent

        modelData.apply {
            strings = listOf(id)
        }

        meta.setCustomModelDataComponent(modelData)

        dummy.setItemMeta(meta)

        return dummy
    }
}