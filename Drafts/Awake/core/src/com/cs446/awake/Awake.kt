package com.cs446.awake

import com.badlogic.gdx.Game
import com.badlogic.gdx.utils.Array
import com.cs446.awake.model.*
import com.cs446.awake.ui.BattleScreen
import com.cs446.awake.ui.DungeonScreen
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
        player = Player("Hero",300, 10, 10, playerDeck, playerStates, PlayerType.Human)
        // Create demo enemy
        val enemyDeck = getTestDeck()
        val enemyImage = Array<String?>(arrayOf("skeleton1.png","skeleton2.png","skeleton3.png","skeleton2.png"))
        val enemyStates : MutableList<State> = mutableListOf()
        enemy = Enemy(enemyImage,"Enemy",100, 99, 99, enemyDeck, enemyStates, PlayerType.AI)

        // For dungeon demo
        dungeonMap = DungeonMap(1)
    }


    override fun create() {
        // Test Battle View
        setActiveScreen(DungeonScreen(dungeonMap))
        // setActiveScreen(BattleScreen(player, enemy))
    }

    override fun dispose() {
    }
}