package com.cs446.awake

import com.badlogic.gdx.Game
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
        val states : MutableList<State> = mutableListOf()
        val player = Player("Hero",100, 10, 10, deck1, states)

        player.updateState(State("Burn", 3, player))
        player.updateState(State("Freeze", 3, player))
        player.updateState(State("Poison", 3, player))

        val imgs = Array<String?>(arrayOf("skeleton1.png","skeleton2.png","skeleton3.png","skeleton2.png"))
        val enemy = Enemy(imgs,"Enemy",999, 99, 99, deck2, states)
        board = Board(player, enemy, player, 1)

    }

    override fun create() {
        setActiveScreen(EnterScreen(board))
    }

    override fun dispose() {
    }
}