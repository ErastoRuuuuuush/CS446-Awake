package com.cs446.awake.model

import com.badlogic.gdx.utils.Array

// cards that apply state in battle

class StateCard (cardName: String, img: String, usage: String, private val state: State) : ActionCard(cardName, img, usage) {
    override fun useCard(target: Character){
        target.updateState(state)
    }
}