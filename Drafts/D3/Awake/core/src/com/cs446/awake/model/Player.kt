package com.cs446.awake.model

class Player(charName: String, HP: Int, energy: Int, strength: Int, deck: Deck, state: MutableList<State>) : Character(charName, HP, energy, strength, deck, state) {
    init {
        // assuming initially 5 cards in hand
        drawCard()
        drawCard()
        drawCard()
        drawCard()
    }
}