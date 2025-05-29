package tcg.cards.engine

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import pandodungeons.PandoDungeons
import tcg.cards.skills.engine.CardSkill
import tcg.cards.skills.engine.SkillType

class CardReader(
    private val plugin: PandoDungeons
) {
    private val tag = NamespacedKey(plugin, "mapacho_card")
    private val rarityNamespace = NamespacedKey(plugin, "card_rarity")
    private val skillNamespace = NamespacedKey(plugin, "card_skill")
    private val idNamespace = NamespacedKey(plugin, "card_id")

    fun getCardFromItem(itemStack: ItemStack): Card? {
        val manager = plugin.skillManager
        val meta = itemStack.itemMeta ?: return null
        val container = meta.persistentDataContainer

        if (!container.has(tag)) return null

        val rarityString = container.get(rarityNamespace, PersistentDataType.STRING) ?: return null
        val skillString = container.get(skillNamespace, PersistentDataType.STRING) ?: return null
        val idString = container.get(idNamespace, PersistentDataType.STRING) ?: return null

        val rarity = runCatching { CardRarity.valueOf(rarityString) }.getOrNull() ?: return null
        val skillType = SkillType { skillString }

        val aux = manager.getSkillFromType(skillType)

        val skill: CardSkill = aux ?: throw NullPointerException("Null Skill")

        return CardBuilder(plugin).apply {
            setItem(itemStack)
            setSkill(skill)
            setRarity(rarity)
            setId(idString)
        }.build()
    }

    fun getCardFromManager(itemStack: ItemStack): Card?{
        val meta = itemStack.itemMeta ?: return null
        val container = meta.persistentDataContainer

        if (!container.has(tag)) return null

        val idString = container.get(idNamespace, PersistentDataType.STRING) ?: return null

        return plugin.cardManager.getCard(idString)
    }

    fun isCard(itemStack: ItemStack): Boolean{
        return itemStack.itemMeta.persistentDataContainer.has(tag)
    }
}
