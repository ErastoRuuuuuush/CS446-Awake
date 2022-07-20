package com.cs446.awake.model


// a deck of action cards
class Deck (var deck: MutableList<ActionCard> = mutableListOf()){

    fun addCard(card: ActionCard) {
        deck.add(card)
    }

    fun isEmpty() : Boolean {
        return deck.size == 0
    }

    fun pop() : ActionCard {
        val topCard = deck[deck.size-1]
        deck.remove(topCard)
        return topCard
    }

    fun shuffle() {
        deck.shuffle()
    }

    fun count() : Int {
        return deck.size
    }

}