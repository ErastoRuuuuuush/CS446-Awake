package com.cs446.awake.model


// cards that restore health in battle
class RestoreCard (cardName: String, img: String, usage: String, private val restoreAmount: Int) : ActionCard(cardName, img, usage){

    override fun useCard(target: Character){
        target.updateHealth(restoreAmount)
    }
}