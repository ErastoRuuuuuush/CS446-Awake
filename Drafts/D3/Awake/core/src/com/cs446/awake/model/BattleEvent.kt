package com.cs446.awake.model

class BattleEvent (backImg: String, frontImg: String, val monster: Monster) : Event(backImg, frontImg)
{
    override fun trigger() {
        super.trigger()
        val monsterDeck = monster.getDeck()
        enemy = Enemy(monster.images, monster.charName, monster.getHP(), monster.getEnergy(), monster.getStrength(), monsterDeck, mutableListOf(), PlayerType.AI)
        // TODO: notify viewer to select battle cards
    }
}