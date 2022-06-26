package com.cs446.awake.model
import com.badlogic.gdx.utils.Array

// return a test deck
fun getTestDeck(): Deck {
    val testDeck = Deck()
    var i = 1
    while (i <= 5){
        // make 5 - i attack cards with damage i
        var j = i
        while (j < 5) {
            val attackCard = ActionCard(("AttackCard$i$j"),"img.jpg", "Deals $i damage after costing ${j/2} strength", 0, j/2, i, Array<State>(0))
            testDeck.addCard(attackCard)
            j ++
        }
        // make a card that restores i health
        val restoreCard = ActionCard("RestoreCard$i", "img.jpg", "Restores $i health after costing $i energy", i,0, (0-i), Array<State>(0))
        testDeck.addCard(restoreCard)
        j ++
    }
    // make some state cards
    i = 1
    while (i < 4){
        val stateCard1 = ActionCard("burner$i", "img.jpg", "burns target for $i rounds and paralysis target for 1 round",
            i, 0, 0, Array<State>(arrayOf(State("Burn", i),State("Paralysis", 1))))
        testDeck.addCard(stateCard1)
        val stateCard2 = ActionCard("freezer", "img.jpg", "freezes target for $i rounds",
            i-1, 0, 0, Array<State>(arrayOf(State("Freeze", i))))
        testDeck.addCard(stateCard2)
    }
    return testDeck
}
