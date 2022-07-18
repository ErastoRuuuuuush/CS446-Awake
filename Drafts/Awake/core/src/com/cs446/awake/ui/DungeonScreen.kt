package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.cs446.awake.model.DungeonMap
import com.cs446.awake.model.INVALIDMOVE
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen


class DungeonScreen(private val map: DungeonMap) : BaseScreen() {
    //// Variable of position related
    // Size of the entire screen
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()

    // DungeonMap Data
    private val level = map.level

    // Timer variables
    private var worldTimer  = -1
    private var activeTimer = false
    private val timerLimit = 10 // Not 0 in case of concurrency issue.
    private var endTimeFcn : () -> Unit = {} // lambda function about what to do when time ends
    private var duringTimeFcn : () -> Unit = {} // lambda function about what to do when each frame passed.
    // private val countdownLabel = Label(String.format("%03d", worldTimer), Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))


    // Function that active the timer
    private fun startTimer(frames: Int, endTime : () -> Unit, duringTime : () -> Unit) {
        endTimeFcn = endTime
        duringTimeFcn = duringTime
        worldTimer = frames
        activeTimer = true
    }

    // Function that count down the timer. Stop timer when time ends.
    private fun runTimer() {
        if (activeTimer) {
            if (worldTimer <= timerLimit) {
                // Time up
                activeTimer = false
                endTimeFcn()
            } else {
                // During count down
                duringTimeFcn()
                worldTimer--
            }
        }
    }

    override fun initialize() {
        Gdx.input.inputProcessor = stage
        //stage.addActor(countdownLabel)
        //countdownLabel.setPosition(screenWidth/2 - countdownLabel.width/2, screenHeight/2 + countdownLabel.height/2)

        // Background Picture
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dragon.jpeg")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        // Menu Bar Label
        val coin = Label("Coin: 20", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        val progress = Label("Level: $level/10", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        //"Village ${"■".repeat(level*2)}${"■".repeat(10-level*2)} BOSS"
        //"Health ${"♥".repeat(3)}"
        val health = Label("Health: 3/3", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))

        // Menu Bar Picture
        val bgPixmap = Pixmap(1, 1, Pixmap.Format.RGB565)
        bgPixmap.setColor(Color.GRAY)
        bgPixmap.fill()
        val textureRegionDrawableBg = TextureRegionDrawable(TextureRegion(Texture(bgPixmap)))
        val menuBar = Table()
        menuBar.background = textureRegionDrawableBg
        menuBar.setPosition(0f, screenHeight - 160f)
        menuBar.setSize(screenWidth, 160f)
        menuBar.top()
        menuBar.add(coin).expandX()
        menuBar.add(progress).expandX()
        menuBar.add(health).expandX()
        stage.addActor(menuBar)

        // Add Event Cards
        for (row in 0 until 3) {
            for (column in 0 until 7) {
                val card = BaseActor(0f, 0f, stage)
                // For reload the Screen use when exit from Battle.
                if (map.map[row][column].isFlipped()) {
                    card.loadTexture(map.map[row][column].frontImg)
                    map.map[row][column].trigger()
                } else {
                    card.loadTexture(map.map[row][column].backImg)
                }
                // By default, start position is show and triggered, final room is show but not triggered.
                if (row == 2 && column == 6) {
                    // Next Room (Boss)
                    card.loadTexture(map.map[row][column].frontImg)
                } else if (row == 0 && column == 0) {
                    // Current Position
                    card.loadTexture(map.map[row][column].frontImg)
                    map.map[row][column].trigger()
                }
                card.height = card.width // Card is a square
                card.centerAtPosition(screenWidth / 7 * column + card.width / 2 + 10, (screenHeight - 160) / 3 * row + card.height / 2)

                // Set event action
                card.addListener(object : InputListener() {
                    override fun touchDown(
                        event: InputEvent?,
                        x: Float,
                        y: Float,
                        pointer: Int,
                        button: Int
                    ): Boolean {
                        if (map.map[row][column].isFlipped()) return true
                        if (map.go(row, column) != INVALIDMOVE) {
                            card.loadTexture(map.map[row][column].frontImg)
                            card.height = card.width
                        }
                        return true
                    }
                })
            }
        }
    }

    override fun update(delta: Float) {
        runTimer()
        return
    }
}