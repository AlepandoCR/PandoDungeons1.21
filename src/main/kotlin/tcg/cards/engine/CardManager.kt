package tcg.cards.engine

import pandodungeons.PandoDungeons
import tcg.cards.types.common.HealingHerbCard
import tcg.cards.types.common.ZombieCard
import tcg.cards.types.epic.*
import tcg.cards.types.legendary.BlackHoleCard
import tcg.cards.types.legendary.ChainHealCard
import tcg.cards.types.legendary.EarthenWallCard
import tcg.cards.types.mithic.SoulSiphonCard
import tcg.cards.types.rare.BoulderCard
import tcg.cards.types.rare.GoatCard
import tcg.cards.types.rare.SleepPowderCard
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
        HealingHerbCard.build(plugin)
        ChainHealCard.build(plugin)
        EarthenWallCard.build(plugin)
        SleepPowderCard.build(plugin) // falta textura
        SoulSiphonCard.build(plugin) // falta textura
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