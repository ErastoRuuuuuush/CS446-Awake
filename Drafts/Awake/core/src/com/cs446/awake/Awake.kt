package com.cs446.awake

import com.badlogic.gdx.Game
import com.badlogic.gdx.utils.Array
import com.cs446.awake.model.*
import com.cs446.awake.ui.BattleScreen
import com.cs446.awake.ui.DungeonScreen
import com.cs446.awake.ui.EnterDungeonScreen
import com.cs446.awake.ui.MergeScreen
import com.cs446.awake.ui.VillageScreen
import com.cs446.awake.utils.BaseScreen

// maybe better to create a BaseGame class and extend from there
class Awake : Game() {

    companion object {
        const val TITLE = "AWAKE"
        lateinit var game: Awake
        fun setActiveScreen(s: BaseScreen) {
            game.setScreen(s)
        }
    }


    init {
        game = this

        reset()
    }


    override fun create() {
        // Test Battle View

//        setActiveScreen(EnterDungeonScreen())

//        setActiveScreen(BattleScreen(player, enemy))
        setActiveScreen(VillageScreen())
    }

    override fun dispose() {
    }
}