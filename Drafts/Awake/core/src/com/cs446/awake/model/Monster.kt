package com.cs446.awake.model

import com.badlogic.gdx.utils.Array


class Monster (val images: Array<String?>, val level: Int,  val HP: Int,
               val charName: String, val reward: Map<MaterialCard, Int>, val actions: Deck, val avator: String){
    fun getDeck (inputLevel: Int = dungeonLevel): Deck{
        //strengthen monster according to level
        val levelDiff = inputLevel - level + 1
        val monsterDeck = Deck()
        for (i in (0 until actions.count())){
            val curCard = actions.pop()
            // generate a separate copy for each card count
            for (j in (0 until (curCard.count + levelDiff))){
                val copyCard = ActionCard(curCard.cardName, curCard.img, curCard.usage, curCard.energyCost,
                    curCard.strengthCost, curCard.healthChange + levelDiff * 5, curCard.Effect, 1)
                monsterDeck.addCard(copyCard)
            }
        }
        return monsterDeck
    }
    fun getHP (inputLevel: Int = dungeonLevel): Int{
        //strengthen monster according to level
        val levelDiff = inputLevel - level + 1
        return HP + levelDiff * 30
    }
    fun getEnergy(): Int{
        return 1000
    }
    fun getStrength(): Int{
        return 10000
    }
}