package tcg.cards

import org.bukkit.scheduler.BukkitRunnable
import pandodungeons.PandoDungeons
import tcg.cards.types.ZombieCard

class CardManager(
    private val plugin: PandoDungeons
) {
    private val registry: MutableList<Card> = mutableListOf()

    private val factory = plugin.cardFactory

    init {
        registerCards()
    }

    private fun registerCards(){
        registerCard(ZombieCard().build(plugin))
    }

    private fun registerCard(card: Card){
        registry.add(card)
    }

    fun getCard(id: String):Card?{
        for (card in registry) {
            if(card.id.contentEquals(id)) return card
        }

        return null
    }

    fun getAllIds(): MutableList<String>{
        val list: MutableList<String> = mutableListOf()

        for(card in registry){
            list.add(card.id)
        }

        return list
    }

}