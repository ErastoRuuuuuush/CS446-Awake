package com.cs446.awake.model

class BattleEvent (backImg: String, frontImg: String, val monster: Monster) : Event(backImg, frontImg)
{
    override fun trigger() : Int{
        super.trigger()
        val monsterDeck = monster.getDeck()
        enemy = Enemy(monster.images, monster.charName, monster.getHP(), monster.getEnergy(), monster.getStrength(), monsterDeck, mutableListOf(), PlayerType.AI)

        // for sake of test only, hard code player
        val deck = getTestDeck()
        val playerStates : MutableList<State> = mutableListOf()
        player = Player("Hero",300, 10, 10, deck, playerStates, PlayerType.Human)
        return BATTLE
    }
}