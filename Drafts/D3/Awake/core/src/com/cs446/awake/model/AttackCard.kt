package com.cs446.awake.model

// cards that deal damage in battle
class AttackCard (cardName: String, img: String, usage: String, energyCost: Int, strengthCost: Int, private val damage: Int) : ActionCard(cardName, img, usage, energyCost, strengthCost){

    override fun useCard(target: Character){
        target.updateHealth(-damage)
    }
}