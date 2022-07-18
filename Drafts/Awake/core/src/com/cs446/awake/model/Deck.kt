package com.cs446.awake.model

import com.badlogic.gdx.utils.Array

// a deck of action cards
class Deck (var deck: Array<ActionCard> = Array<ActionCard>()){

    fun addCard(card: ActionCard) {
        deck.add(card)
    }

    fun isEmpty() : Boolean {
        return deck.isEmpty
    }

    fun pop() : ActionCard {
        return deck.pop()
    }

    fun shuffle() {
        deck.shuffle()
    }

    fun count() : Int {
        return deck.size
    }

}