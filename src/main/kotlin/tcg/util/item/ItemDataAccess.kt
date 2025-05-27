package tcg.util.item

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object ItemDataAccess {
    fun <T, Z : Any> insertData(itemStack: ItemStack, namespacedKey: NamespacedKey, data: Z, persistentDataType: PersistentDataType<T, Z>){
        val meta = itemStack.itemMeta
        meta.persistentDataContainer.set(namespacedKey, persistentDataType, data)
    }

    fun getData(itemStack: ItemStack, namespacedKey: NamespacedKey): String?{
        val r = itemStack.itemMeta.persistentDataContainer.get(namespacedKey, PersistentDataType.STRING)
        // data might be null
        return r
    }
}