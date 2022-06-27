package com.cs446.awake.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Array

abstract class Character (val charName: String, val maxHP: Int, val maxEnergy: Int, val maxStrength: Int, val deck: Deck, var state: MutableList<State>) {
    var hand: MutableList<ActionCard> = mutableListOf()
    var energy = maxEnergy
    var strength = maxStrength
    var HP = maxHP

    fun update(card: ActionCard, from: Character) {
        // If this card is used by myself, deduct the cost, and restores health if the card allows
        if (from == this){
            updateEnergy(0-card.energyCost)
            updateStrength(0-card.strengthCost)
            if (card.healthChange > 0){
                updateHealth(card.healthChange)
            }
        }
        else { // I am an enemy of the user
            if (card.healthChange < 0) { // The card deals damage
                updateHealth(card.healthChange)
            }
            for (s in card.Effect) {
                updateState(s)
            }
        }
    }

    open fun selectHandCard(): ActionCard {
        return hand[0]
    }

    // add try catch block for 1. empty deck 2.hand full
    open fun drawCard(){
        if (deck.isEmpty()){
            HP = 0
            return null
        }
        val c = deck.pop() // deck should shuffle when it is empty
        hand.add(c)
    }

    fun updateState(newState: State){
        var curState: State? = state.find {card -> card.stateName == newState.stateName}
        if (curState == null) {
            state.add(newState)
        } else {
            curState.extend(newState)
        }
    }

    fun removeState(removedStates: MutableList<String>){
        var d = mutableListOf<State>()
        for (s in state){
            if (removedStates.contains(s.stateName)){
                d.add(s)
            }
        }
        state.removeAll(d)

    }

    open fun preRound() {
        // demo only, restore some amount of energy in real game
        energy = maxEnergy
        strength = maxStrength
        drawCard()

        for (s in state) {
            s.apply(this)
        }
    }

    open fun reset() {
        for (i in 1..5) {
            drawCard()
        }
    }

    open fun endRound() {
    }

    open fun postRound(){
    }

    fun updateHealth(HpChange: Int){
        HP += HpChange
    }

    fun updateStrength(strengthChange: Int) {
        strength += strengthChange
    }

    fun updateEnergy(energyChange: Int) {
        energy += energyChange
    }

    fun isDead(): Boolean {
        return HP <= 0
    }

}