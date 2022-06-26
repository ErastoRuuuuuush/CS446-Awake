package com.cs446.awake.model

import com.badlogic.gdx.utils.Array

// cards that apply state in battle

class StateCard (cardName: String, img: String, usage: String, energyCost: Int, strengthCost: Int, private val state: State) : ActionCard(cardName, img, usage, energyCost, strengthCost) {
    override fun useCard(target: Character){
        target.updateState(state)
    }
}