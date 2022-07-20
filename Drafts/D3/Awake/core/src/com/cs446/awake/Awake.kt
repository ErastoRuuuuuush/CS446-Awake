package com.cs446.awake

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Array
import com.cs446.awake.model.*
import com.cs446.awake.ui.EnterScreen
import com.cs446.awake.utils.BaseScreen

// maybe better to create a BaseGame class and extend from there
class Awake : Game() {

    lateinit var board: Board

    companion object {
        const val TITLE = "AWAKE"
        lateinit var game: Awake
        fun setActiveScreen(s: BaseScreen) {
            game.setScreen(s)
        }
    }


    init {
        game = this
        // shouldn't be here, only in start code for demo
        val deck1 = getTestDeck()
        val deck2 = getTestDeck()
        val playerStates : MutableList<State> = mutableListOf()
        val player = Player("Hero",300, 10, 10, "badlogic.jpg", deck1, playerStates, PlayerType.Human)

        val imgs = Array<String?>(arrayOf("skeleton1.png","skeleton2.png","skeleton3.png","skeleton2.png"))

        val enemyStates : MutableList<State> = mutableListOf()
        val enemy = Enemy(imgs,"Enemy",100, 99, 99, "badlogic.jpg", deck2, enemyStates, PlayerType.AI)

        board = Board(player, enemy)
        print("board created")

        
    }

    override fun create() {
        setActiveScreen(EnterScreen(board))
    }

    override fun dispose() {
    }
}