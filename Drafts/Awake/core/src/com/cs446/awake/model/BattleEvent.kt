package com.cs446.awake.model

class BattleEvent (backImg: String, frontImg: String, val monster: Monster) : Event(backImg, frontImg)
{
    override fun trigger() : Int{
        super.trigger()
        val monsterDeck = monster.getDeck(dungeonLevel)
        enemy = Enemy(monster.images, monster.charName, monster.getHP(dungeonLevel),
            monster.getEnergy(), monster.getStrength(), "badlogic.jpg",
            monsterDeck, mutableListOf(), PlayerType.AI)
        // generate new player deck according to the items in backpack
        deck = Deck()
        for (item in backPackItem.getStored()){
            if (item !is ItemCard){
                println("Warning: non-material in backpack")
                continue
            }
            item.addToDeck(deck)
        }
        player = Player("Hero", HP, energy, strength, "badlogic.jpg",deck, mutableListOf(), PlayerType.Human)

        // add the reward to the backpack
        for (rewarded in monster.reward.keys){
            rewarded.count = monster.reward[rewarded]!!
            backPackMaterial.add(rewarded)
        }

        val randomized = materialInfo.getBelowLevel(monster.level)
        if (randomized != null){
            backPackMaterial.add(randomized)
        }
        return BATTLE
    }
}