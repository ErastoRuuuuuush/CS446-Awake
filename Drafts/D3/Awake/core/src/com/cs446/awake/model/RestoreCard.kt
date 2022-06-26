package com.cs446.awake.model


// cards that restore health in battle
class RestoreCard (cardName: String, img: String, usage: String, energyCost: Int, strengthCost: Int, private val restoreAmount: Int) : ActionCard(cardName, img, usage, energyCost, strengthCost){

    override fun useCard(target: Character){
        target.updateHealth(restoreAmount)
    }
}