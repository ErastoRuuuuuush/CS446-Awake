package com.cs446.awake.model

class BattleEvent (backImg: String, frontImg: String, val monster: Monster) : Event(backImg, frontImg)
{
    override fun trigger() : Int{
        super.trigger()
        val monsterDeck = monster.getDeck()
        enemy = Enemy(monster.images, monster.charName, monster.getHP(), monster.getEnergy(), monster.getStrength(), monsterDeck, mutableListOf(), PlayerType.AI)
        // generate new player deck according to the items in backpack
        deck = Deck()
        for (item in backPackItem.getStored()){
            item.use()
        }
        player = Player("Hero",300, 10, 10, deck, mutableListOf(), PlayerType.Human)
        return BATTLE
    }
}