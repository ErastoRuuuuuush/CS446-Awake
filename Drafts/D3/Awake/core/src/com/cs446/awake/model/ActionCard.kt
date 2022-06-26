package com.cs446.awake.model

import com.badlogic.gdx.utils.Array
// cards that will be used in fights
abstract class ActionCard (cardName: String, img: String, usage: String) : Card(cardName, img, usage) {
    abstract fun useCard(target: Character)
}