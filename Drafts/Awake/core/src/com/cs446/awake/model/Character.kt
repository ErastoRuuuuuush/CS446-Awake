package com.cs446.awake.model

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Null
import com.cs446.awake.utils.BaseActor
import org.jetbrains.annotations.NotNull

abstract class Character (val charName: String, val maxHP: Int, val maxEnergy: Int, val maxStrength: Int, var charImage: String, val deck: Deck, var states: MutableList<State>, var playerType: PlayerType) {
    var hand: MutableList<ActionCard> = mutableListOf()
    var energy = maxEnergy
    var strength = maxStrength
    var HP = maxHP
    var characterStateMap = HashMap<String, BaseActor>()
    var canUseCard = true
    lateinit var healthBar : Image
    lateinit var energyBar : Image
    lateinit var image : Texture

    var originalHP = maxHP
    var originalEnergy = maxEnergy
    var healthOriginalWidth = 0f
    var healthStartX = 0f
    var energyOriginalWidth = 0f
    var energyStartX = 0f


    fun createBarStyle(backGroundColor: Color, frontColor: Color, height : Int) : ProgressBar.ProgressBarStyle {
        // bar background as red
        var pixmap = Pixmap(10, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(backGroundColor)
        pixmap.fill()
        var drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        val progressBarStyle = ProgressBar.ProgressBarStyle()
        progressBarStyle.background = drawable


        // health as green
        pixmap = Pixmap(0, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(frontColor)
        pixmap.fill()
        drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        progressBarStyle.knob = drawable

        pixmap = Pixmap(10, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(frontColor)
        pixmap.fill()
        drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        progressBarStyle.knobBefore = drawable

        return progressBarStyle
    }

    abstract fun initBars()

    abstract fun initCharImage()

    abstract fun initChar()

    fun update(card: ActionCard, from: Character) : Boolean{
        // If this card is used by myself, deduct the cost, and restores health if the card allows
        if (from == this){
            if (!updateEnergy(0-card.energyCost)) return false
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
        return true
    }

    // strategy for character to use card in game. default: use the first card.
    open fun selectCard(): ActionCard {
        val card: ActionCard = hand[0]
        removeCard(card)
        return card
    }

    open fun removeCard(card: ActionCard) {
        hand.remove(card)
    }

    // add try catch block for 1. empty deck 2.hand full
    // return false if cannot drawCard
    open fun drawCard(): Boolean {
        if (deck.isEmpty()){
            return false
        }
        val c = deck.pop() // deck should shuffle when it is empty
        hand.add(c)
        return true
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

    // Draw cards and apply states, if no cards,return false
    open fun preRound(): Boolean {
        updateEnergy(3)
        while (hand.size < 5) {
            if (!drawCard()) {
                if (hand.size == 0) {
                    return false
                } else {
                    break
                }
            }
        }


        println("start with states:")
        canUseCard = true
        for (s in states) {
            println(s.stateName + " for ${s.effectiveRound} rounds")
            if (s.apply(this)) {
                // True means freeze character
                canUseCard = false
            }
        }
        return true
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

    abstract fun updateHealth(HpChange: Int)

    open fun updateStrength(strengthChange: Int) {
        strength += strengthChange
    }

    abstract fun updateEnergy(energyChange: Int) : Boolean


    fun isDead(): Boolean {
        return HP <= 0
    }

    init {
        // shuffle the deck
        deck.shuffle()
    }

}