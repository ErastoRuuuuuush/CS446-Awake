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
import com.cs446.awake.Awake
import com.cs446.awake.model.*
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen
import com.cs446.awake.utils.DragDropActor


class DungeonScreen(private val map: DungeonMap) : BaseScreen() {
    //// Variable of position related
    // Size of the entire screen
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()

    // DungeonMap Data
    private val level = map.level

    // Card's border
    private val borderTexture =
        Texture(Gdx.files.internal("highlight_border.png")) // TODO: change the texture
    private val borderImage = Image(borderTexture)

    // Timer variables
    private val timerList = ArrayList<Timer>()
    private var lockTouchDown = false


    override fun initialize() {
        Gdx.input.inputProcessor = stage
        //stage.addActor(countdownLabel)
        //countdownLabel.setPosition(screenWidth/2 - countdownLabel.width/2, screenHeight/2 + countdownLabel.height/2)

        // Background Picture
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("darkbackground.png")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        // Menu Bar Label
        val progress = Label("Level: $level/4", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        //"Village ${"■".repeat(level*2)}${"■".repeat(10-level*2)} BOSS"
        //"Health ${"♥".repeat(3)}"

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
        menuBar.add(progress).expandX()
        stage.addActor(menuBar)

        // Add Event Cards
        for (row in 0 until 3) {
            for (col in 0 until 7) {
                val card = BaseActor(0f, 0f, stage)
                // For reload the Screen use when exit from Battle.
                if (map.map[row][col].isFlipped()) {
                    card.loadTexture(map.map[row][col].frontImg)
                    map.map[row][col].trigger()
                } else {
                    card.loadTexture(map.map[row][col].backImg)
                }
                // By default, start position is show and triggered, final room is show but not triggered.
                if (row == 2 && col == 6) {
                    // Next Room (Boss)
                    card.loadTexture(map.map[row][col].frontImg)
                } else if (row == 0 && col == 0) {
                    // Current Position
                    card.loadTexture(map.map[row][col].frontImg)
                    map.map[row][col].trigger()
                }
                card.setSize(275f,275f)
                // card.height = card.width // Card is a square
                card.centerAtPosition(screenWidth / 7 * col + card.width / 2 + 10, (screenHeight - 160) / 3 * row + card.height / 2)

                // Set event action
                card.addListener(object : InputListener() {
                    override fun touchDown(
                        event: InputEvent?,
                        x: Float,
                        y: Float,
                        pointer: Int,
                        button: Int
                    ): Boolean {
                        if (lockTouchDown) return true
                        if (map.map[row][col].isFlipped()) return true
                        val result = map.go(row, col)
                        if (result != INVALIDMOVE) {
                            card.loadTexture(map.map[row][col].frontImg)
                            card.setSize(275f,275f)
                            // card.height = card.width
                        }
                        if (result == COLLECT) {
                            val timer = Timer(0)
                            collectAnimation(row, col, timer)
                        } else if (result == BATTLE) {
                            battleAnimation()
                        } else if (result == NEXTLEVEL) {
                            nextLevelAnimation()
                        }
                        return true
                    }
                })
            }
        }
    }
    private fun collectAnimation(row: Int, col: Int, timeCount: Timer) {
        // Notification message
        val itemNotify = Label("Collected Item!", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        itemNotify.setPosition(screenWidth/2 - itemNotify.width/2, screenHeight/2 + itemNotify.height/2)
        stage.addActor(itemNotify)

        val colletEvent = map.map[row][col] as CollectEvent
        // Get the item picture
        val itemPic = BaseActor(0f, 0f, stage)
        itemPic.loadTexture(colletEvent.material.img)
        itemPic.centerAtPosition(screenWidth / 2, screenHeight)
        itemPic.moveBy(0f, -550f)

        // Add card border image
        val borderWidth = 30
        borderImage.setSize(
            borderImage.width + borderWidth * 2,
            borderImage.height + borderWidth * 2
        )
        borderImage.setPosition(itemPic.x - borderWidth, itemPic.y - borderWidth)
        stage.addActor(borderImage)
        itemNotify.toFront()

        // Display 0.3 sec
        val timeUp: () -> Unit = {
            // When time up, vanish card
            val duringTime: () -> Unit = {
                // vanish the card in about 0.2sec
                val value: Float = timeCount.time / 20f
                borderImage.color.a = value
                itemPic.color.a = value
                itemNotify.color.a = value
            }
            val endTime: () -> Unit = {
                borderImage.remove()
                borderImage.color.a = 1f // Reset the alpha value
                itemNotify.remove()
                itemPic.remove()
            }
            startTimer(20, endTime, duringTime, timeCount)
        }
        startTimer(30, timeUp, {}, timeCount)
    }

    // TODO: lockTouchDown
    private fun battleAnimation() {
//        lockTouchDown = true
        val timer = Timer(0)
        val itemNotify = Label("Battle!", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        itemNotify.setPosition(screenWidth/2 - itemNotify.width/2, screenHeight/2 + itemNotify.height/2)
        stage.addActor(itemNotify)

        // Display 0.4 sec
        val timeUp: () -> Unit = {
            // When time up, vanish card
            val duringTime: () -> Unit = {
                // vanish the card in about 0.2sec
                val value: Float = timer.time / 20f
                itemNotify.color.a = value
            }
            val endTime: () -> Unit = {
                itemNotify.remove()
            }
            startTimer(20, endTime, duringTime, timer)
        }
        startTimer(30, timeUp, {}, timer)
    }

    // Will also save (dump) data
    private fun nextLevelAnimation() {
        lockTouchDown = true
        val timer = Timer(0)
        val itemNotify = Label("Next Level!", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        itemNotify.setPosition(screenWidth/2 - itemNotify.width/2, screenHeight/2 + itemNotify.height/2)
        stage.addActor(itemNotify)

        // Display 0.4 sec
        val timeUp: () -> Unit = {
            // When time up, vanish card
            val duringTime: () -> Unit = {
                // vanish the card in about 0.2sec
                val value: Float = timer.time / 20f
                itemNotify.color.a = value
            }
            val endTime: () -> Unit = {
                itemNotify.remove()
                stage.clear()
                Awake.setActiveScreen(DungeonScreen(DungeonMap(level+1)))
                dumpJson()
            }
            startTimer(20, endTime, duringTime, timer)
        }
        startTimer(30, timeUp, {}, timer)
    }

    // Function that active the timer
    private fun startTimer(frames: Int, endTime : () -> Unit, duringTime : () -> Unit, timer: Timer) {
        timer.endTimeFcn = endTime
        timer.duringTimeFcn = duringTime
        timer.time = frames
        timer.activeTimer = true
        timerList.add(timer)
    }

    // Function that count down the timer. Stop timer when time ends.
    private fun runTimer() {
        for (i in 0 until timerList.size) {
            val timer = timerList[i]
            if (timer.activeTimer) {
                if (timer.time <= timer.timerLimit) {
                    // Time up
                    timer.activeTimer = false
                    timer.endTimeFcn()
                } else {
                    // During count down
                    timer.duringTimeFcn()
                    timer.time--
                }
            }
        }
    }

    override fun update(delta: Float) {
        runTimer()
        return
    }
}