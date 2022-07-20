package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
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

class VillageScreen() : BaseScreen() {
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()
    private val houseWidth = screenWidth / 4f

    // Timer variables
    private var worldTimer  = -1
    private var activeTimer = false
    private val timerLimit = 10 // Not 0 in case of concurrency issue.
    private var endTimeFcn : () -> Unit = {} // lambda function about what to do when time ends
    private var duringTimeFcn : () -> Unit = {} // lambda function about what to do when each frame passed.
    // private val countdownLabel = Label(String.format("%03d", worldTimer), Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
    private val dungeonMap : DungeonMap = DungeonMap(1)

    private lateinit var villageMusic : Music

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
        readJson()
        Gdx.input.inputProcessor = stage
        //stage.addActor(countdownLabel)
        //countdownLabel.setPosition(screenWidth/2 - countdownLabel.width/2, screenHeight/2 + countdownLabel.height/2)

        // Music
        villageMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/village_bgm.wav"))
        villageMusic.setLooping(true)
        villageMusic.volume = 200f
        villageMusic.play()


        // Background Picture
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("villageBackground.png")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        // Save before quit
        val quitButton = BaseActor(0f, 0f, stage)
        quitButton.loadTexture("Icon_quit.png")
        quitButton.setSize(150f, 150f)
        quitButton.centerAtPosition(100f, 950f)
        quitButton.toFront()
        quitButton.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ) : Boolean {
                dumpJson()
                val duringTime: () -> Unit = {
                    val winLabel = Label("Saving...", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
                    winLabel.setPosition(screenWidth/2 - winLabel.width/2, screenHeight/2 - winLabel.height/2)
                    stage.addActor(winLabel)
                }
                startTimer(20, {Gdx.app.exit()}, duringTime)
                return true
            }
        })

        val house1 = BaseActor(0f, 0f, stage)
        house1.loadTexture("villages/TX House A.png")

        // house1.height = house1.width
        house1.setSize(houseWidth,houseWidth/house1.width*house1.height )
        house1.centerAtPosition(screenWidth / 5 * 0 + house1.width / 2 + 100, (screenHeight / 2) + 50f)

        // Set event action
        house1.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                villageMusic.stop()
                Awake.setActiveScreen(BackpackScreen(1))
                return true
            }
        })

        val label1width = house1.width - 50f
        val label1 = BaseActor(0f, 0f, stage)
        label1.loadTexture("storage.png")
        label1.setSize(label1width, label1width/label1.width*label1.height)
        label1.centerAtActor(house1)
        label1.moveBy(-25f,-house1.height/2 - 80f)

        val house2 = BaseActor(0f, 0f, stage)
        house2.loadTexture("villages/TX House C.png")

        // house2.height = house2.width
        house2.setSize(houseWidth,houseWidth/house2.width*house2.height )
        house2.centerAtActor(house1)
        house2.moveBy(house1.width + 100f, 0f)
        // house2.centerAtPosition(screenWidth / 5 * 1 + house2.width / 2 + 100, (screenHeight / 2) - 100f)

        // Set event action
        house2.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                villageMusic.stop()
                Awake.setActiveScreen(BackpackScreen(0))
                return true
            }
        })


        val label2width = house2.width - 50f
        val label2 = BaseActor(0f, 0f, stage)
        label2.loadTexture("gallery.png")
        label2.setSize(label2width, label2width/label2.width*label2.height)
        label2.centerAtActor(house2)
        label2.moveBy(0f,-house2.height/2 - 80f)

        val house3 = BaseActor(0f, 0f, stage)
        house3.loadTexture("villages/TX House B.png")

        // house3.height = house3.width
        house3.setSize(houseWidth,houseWidth/house3.width*house3.height )
        house3.centerAtActor(house2)
        house3.moveBy(house2.width +100f, 100f)
        // house3.centerAtPosition(screenWidth / 5 * 2 + house3.width / 2 + 100, (screenHeight / 2))

        // Set event action
        house3.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                villageMusic.stop()
                Awake.setActiveScreen(MergeScreen())
                return true
            }
        })

        /*
        val potion = BaseActor(0f, 0f, stage)
        potion.loadTexture("potion.png")

        potion.width = house3.width / 3
        potion.height = potion.width
        potion.centerAtPosition(house3.x + 50, (screenHeight / 2) + house3.height / 2 - 400)

        val bar3 = Table()
        // bar1.background = textureRegionDrawableBg
        bar3.setPosition(house3.x + 90 + potion.width, potion.y + 30)
        bar3.setSize(potion.width / 2, potion.height / 2)
        bar3.top()
         */
        // val label3 = Label("Craft", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        // label3.setFontScale(0.5f,0.5f)
        // bar3.add(label3)
        // stage.addActor(bar3)
        val label3width = house3.width - 50f
        val label3 = BaseActor(0f, 0f, stage)
        label3.loadTexture("craft.png")
        label3.setSize(label3width, label3width/label3.width*label3.height)
        label3.centerAtActor(house3)
        label3.moveBy(-25f,-house3.height/2 - 80f)

        val dungeon = BaseActor(0f, 0f, stage)
        dungeon.loadTexture("dungeonDirect.png")

        dungeon.setSize(label1width , label1width/dungeon.width*dungeon.height)
        dungeon.centerAtPosition(screenWidth - dungeon.width/2, screenHeight / 7)

        // Set event action
        dungeon.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                villageMusic.stop()
                Awake.setActiveScreen(EnterDungeonScreen())
                return true
            }
        })
    }

    override fun update(delta: Float) {
        runTimer()
        return
    }
}