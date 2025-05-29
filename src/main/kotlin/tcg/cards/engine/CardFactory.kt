package tcg.cards.engine

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import pandodungeons.PandoDungeons
import tcg.cards.skills.engine.CardSkill
import tcg.cards.skills.engine.SkillType
import tcg.util.item.ItemDataAccess
import tcg.util.text.TextUtils.reFactorizeCardId

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
        val item = buildItem(id, skill, rarity)

        putId(item, id)

        putSkill(item, skill.getType())

        putRarity(item, rarity)

        tagCard(item)

        return item
    }

    private fun buildItem(id: String, skill: CardSkill, rarity: CardRarity): ItemStack{
        val dummy = ItemStack(Material.PAPER)
        val meta = dummy.itemMeta
        val modelData = meta.customModelDataComponent

        meta.displayName(Component.text(reFactorizeCardId(id)))

        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)

        meta.lore(skill.description.asLore())

        meta.setRarity(rarity.toItemRarityFallback())

        modelData.apply {
            strings = listOf(id)
        }

        meta.setCustomModelDataComponent(modelData)

        dummy.setItemMeta(meta)

        return dummy
    }

}