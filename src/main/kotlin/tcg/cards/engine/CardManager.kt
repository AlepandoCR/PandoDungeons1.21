package tcg.cards.engine

import pandodungeons.PandoDungeons
import tcg.cards.types.*
import tcg.util.task.TaskUtils

class CardManager(
    private val plugin: PandoDungeons
) {
    private val registry: MutableList<Card> = mutableListOf()

    init {
        TaskUtils.doLater(
            {registerCards()},
            20L,
            plugin
        )
    }

    private fun registerCards(){
        ZombieCard.build(plugin)
        BlazeCard.build(plugin)
        GoatCard.build(plugin)
        LightningCard.build(plugin)
        BoulderCard.build(plugin)
        CrystalBarrageCard.build(plugin)
        BlackHoleCard.build(plugin)
    }

    fun registerCard(card: Card){
        registry.add(card)
    }

    fun getCard(id: String): Card?{
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