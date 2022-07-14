package com.cs446.awake.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Null
import com.cs446.awake.utils.BaseActor
import org.jetbrains.annotations.NotNull

abstract class Character (val charName: String, val maxHP: Int, val maxEnergy: Int, val maxStrength: Int, val deck: Deck, var states: MutableList<State>, var playerType: PlayerType) {
    var hand: MutableList<ActionCard> = mutableListOf()
    var energy = maxEnergy
    var strength = maxStrength
    var HP = maxHP
    var characterStateMap = HashMap<String, BaseActor>()
    var bd : Board? = null
    lateinit var healthBar : ProgressBar

    abstract fun initBars()

    fun setBoard(bd: Board){
        this.bd = bd
    }

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
        println("health $HP")
    }

    fun selectRamdomCard(): ActionCard {
        val card: ActionCard = hand[0]
        removeCard(card)
        return card
    }

    open fun removeCard(card: ActionCard) {
        hand.remove(card)
    }

    // add try catch block for 1. empty deck 2.hand full
    open fun drawCard(){
        if (deck.isEmpty()){
            HP = 0
            return endRound()
        }
        val c = deck.pop() // deck should shuffle when it is empty
        hand.add(c)
    }

    fun updateState(newState: State){
        var curState: State? = states.find {card -> card.stateName == newState.stateName}
        if (curState == null) {
            states.add(newState)
            addStateIcon(newState)
        } else {
            curState.extend(newState)
        }
    }

    fun removeStateIcon(state:State){
        val name: String = state.stateName
        characterStateMap[name]!!.setOpacity(0.3f)
    }

    fun addStateIcon(state:State){
        val name: String = state.stateName
        characterStateMap[name]!!.setOpacity(1f)
    }

    fun removeStates(removedStates: MutableList<String>){
        var d = mutableListOf<State>()
        for (s in states){
            if (removedStates.contains(s.stateName)){
                d.add(s)
                removeStateIcon(s)
            }
        }
        states.removeAll(d)

    }

    open fun preRound() {
        // demo only, restore some amount of energy in real game
        energy += 3
        strength += 3
        while (hand.size < 5) drawCard()


        println("start with states:")
        for (s in states) {
            println(s.stateName + " for ${s.effectiveRound} rounds")
            s.apply(this)
        }
    }

    /*
    open fun reset() {
        for (i in 1..5) {
            drawCard()
        }
    }

     */

    open fun endRound() {
        this.bd?.endRound()
    }

    open fun postRound(){
        var removedStates = mutableListOf<String>()
        for (state in states){
            if (state.effectiveRound <= 0) {
                removedStates.add(state.stateName)
            }
        }
        removeStates(removedStates)

    }

    fun updateHealth(HpChange: Int){
        HP += HpChange
        healthBar.value = HP / 100f
//        println(charName + " remaining health " + HP.toString())
    }

    open fun updateStrength(strengthChange: Int) {
        strength += strengthChange
    }

    open fun updateEnergy(energyChange: Int) {
        energy += energyChange
    }

    fun isDead(): Boolean {
        return HP <= 0
    }

}