package com.cs446.awake.model

// cards that deal damage in battle
class AttackCard (cardName: String, img: String, usage: String, private val damage: Int) : ActionCard(cardName, img, usage){

    override fun useCard(target: Character){
        target.updateHealth(-damage)
    }
}