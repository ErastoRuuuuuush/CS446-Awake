package com.cs446.awake.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Array

abstract class Character (val charName: String, var HP: Int, var energy: Int, var strength: Int, val deck: Deck, var state: MutableList<State>) {

    var hand: Array<Card> = Array<Card>()
    // var deck: Array<Card> = Array<Card>()

    fun useCard(card: Card, target: Character) {}

    // add try catch block for 1. empty deck 2.hand full
    fun drawCard(){
        val c = deck.pop()
        hand.add(c)
    }

    fun updateState(newState: State){
        state.add(newState)
    }

    fun removeState(removedStates: MutableList<String>){

    }

    fun updateHealth(HpChange: Int){
        HP += HpChange
    }

    fun isDead(){}

}