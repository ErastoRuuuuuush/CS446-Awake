package com.cs446.awake.model

// return a test deck
fun getTestDeck(): Deck {
    val testDeck : Deck = Deck()
    var i = 1
    while (i <= 10){
        // make 10 - i attack cards with damage i
        var j = i
        while (j < 10) {
            val attackCard : AttackCard = AttackCard(("AttackCard$i$j"),"img.jpg", "Deals $i damage after costing ${j/2} strength", 0, j/2, i )
            testDeck.addCard(attackCard)
        }
        // make a card that restores i health
        val restoreCard : RestoreCard = RestoreCard("RestoreCard$i", "img.jpg", "Restores $i health after costing $i energy", i,0,i)
        testDeck.addCard(restoreCard)
    }
    // make some state cards
    val stateCard : StateCard = StateCard("burner", "img.jpg", "burns target for 3 rounds", 3, 0, State("burn", 3))
    return testDeck
}
