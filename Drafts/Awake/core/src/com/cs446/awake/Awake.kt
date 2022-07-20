package com.cs446.awake

import com.badlogic.gdx.Game
import com.badlogic.gdx.utils.Array
import com.cs446.awake.model.*
import com.cs446.awake.ui.BattleScreen
import com.cs446.awake.ui.DungeonScreen
import com.cs446.awake.ui.VillageScreen
import com.cs446.awake.utils.BaseScreen

// maybe better to create a BaseGame class and extend from there
class Awake : Game() {

    // Variable for demo usage
    private val player : Player
    private val enemy : Enemy
    private val dungeonMap : DungeonMap

    companion object {
        const val TITLE = "AWAKE"
        lateinit var game: Awake
        fun setActiveScreen(s: BaseScreen) {
            game.setScreen(s)
        }
    }


    init {
        game = this
        // The below code are only for demo use, not for final project.
        // Create demo player
        val playerDeck = getTestDeck() // A function in file testDeck.kt
        val playerStates : MutableList<State> = mutableListOf()
        player = Player("Hero",300, 10, 10,"badlogic.jpg", playerDeck, playerStates, PlayerType.Human)
        // Create demo enemy
        val enemyDeck = getTestDeck()
        val enemyImage = Array<String?>(arrayOf("skeleton1.png","skeleton2.png","skeleton3.png","skeleton2.png"))
        val enemyStates : MutableList<State> = mutableListOf()
        enemy = Enemy(enemyImage,"Enemy",100, 99, 99,"badlogic.jpg", enemyDeck, enemyStates, PlayerType.AI)

        // For dungeon demo
        dungeonMap = DungeonMap(1)

        var card = MaterialCard("a","equipment.png","useassssssssssssssssssssssssssssssssssssssssss")
        var card1 = MaterialCard("b","equipment.png","useb")
        var card2 = MaterialCard("c","equipment.png","usec")
        var card3 = MaterialCard("d","equipment.png","usec")
        var card4 = MaterialCard("e","equipment.png","usec")
        val d = deck
        var card5 = ItemCard("f","equipment.png","usec",1,1,1,1,1,1,1,d)
        var card6 = ItemCard("g","equipment.png","usec",1,1,1,1,1,1,1,d)
        var card7 = ItemCard("h","equipment.png","usec",1,1,1,1,1,1,1,d)
        var card8 = ItemCard("i","equipment.png","usec",1,1,1,1,1,1,1,d)
        var card9 = MergableCard("j","equipment.png","usec")
        storage.add(card)
        storage.add(card1)
        storage.add(card2)
        storage.add(card3)
        storage.add(card4)
        storage.add(card5)
        storage.add(card6)
        storage.add(card7)
        storage.add(card8)
        storage.add(card9)

    }


    override fun create() {
        // Test Battle View
//        setActiveScreen(VillageScreen())
        setActiveScreen(VillageScreen())
//        setActiveScreen(BattleScreen(player, enemy))
    }

    override fun dispose() {
    }
}