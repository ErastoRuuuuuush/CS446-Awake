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
import com.cs446.awake.model.DungeonMap
import com.cs446.awake.model.VillageMap
import com.cs446.awake.model.INVALIDMOVE
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen

class VillageScreen() : BaseScreen() {
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()

    // Timer variables
    private var worldTimer  = -1
    private var activeTimer = false
    private val timerLimit = 10 // Not 0 in case of concurrency issue.
    private var endTimeFcn : () -> Unit = {} // lambda function about what to do when time ends
    private var duringTimeFcn : () -> Unit = {} // lambda function about what to do when each frame passed.
    // private val countdownLabel = Label(String.format("%03d", worldTimer), Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
    private val dungeonMap : DungeonMap = DungeonMap(1)

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
        background.loadTexture("villageBackground.png")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        val house1 = BaseActor(0f, 0f, stage)
        house1.loadTexture("House1.png")

        house1.height = house1.width // Card is a square
        house1.centerAtPosition(screenWidth / 5 * 0 + house1.width / 2 + 100, (screenHeight / 2) + house1.height / 2 - 200)

        // Set event action
        house1.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                Awake.setActiveScreen(BackpackScreen(1))
                return true
            }
        })

        val anvil = BaseActor(0f, 0f, stage)
        anvil.loadTexture("anvil.png")

        anvil.width = house1.width / 3
        anvil.height = anvil.width
        anvil.centerAtPosition(house1.x + 50, (screenHeight / 2) + house1.height / 2 - 400)

        val bar1 = Table()
        // bar1.background = textureRegionDrawableBg
        bar1.setPosition(house1.x + 90 + anvil.width, anvil.y + 30)
        bar1.setSize(anvil.width / 2, anvil.height / 2)
        bar1.top()
        val label1 = Label("Storage", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        label1.setFontScale(0.5f,0.5f)
        bar1.add(label1)
        stage.addActor(bar1)

        val house2 = BaseActor(0f, 0f, stage)
        house2.loadTexture("House2.png")

        house2.height = house2.width // Card is a square
        house2.centerAtPosition(screenWidth / 5 * 1 + house2.width / 2 + 100, (screenHeight / 2) + house2.height / 2 - 200)

        // Set event action
        house2.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                Awake.setActiveScreen(BackpackScreen(0))
                return true
            }
        })


        val apple = BaseActor(0f, 0f, stage)
        apple.loadTexture("apple.png")

        apple.width = house2.width / 3
        apple.height = apple.width
        apple.centerAtPosition(house2.x + 50, (screenHeight / 2) + house2.height / 2 - 400)

        val bar2 = Table()
        // bar1.background = textureRegionDrawableBg
        bar2.setPosition(house2.x + 90 + apple.width, apple.y + 30)
        bar2.setSize(apple.width / 2, apple.height / 2)
        bar2.top()
        val label2 = Label("Gallery", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        label2.setFontScale(0.5f,0.5f)
        bar2.add(label2)
        stage.addActor(bar2)

        val house3 = BaseActor(0f, 0f, stage)
        house3.loadTexture("House3.png")

        house3.height = house3.width // Card is a square
        house3.centerAtPosition(screenWidth / 5 * 2 + house3.width / 2 + 100, (screenHeight / 2) + house3.height / 2 - 200)

        // Set event action
        house3.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                Awake.setActiveScreen(DungeonScreen(dungeonMap))
                return true
            }
        })

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
        val label3 = Label("Craft", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        label3.setFontScale(0.5f,0.5f)
        bar3.add(label3)
        stage.addActor(bar3)

        val house4 = BaseActor(0f, 0f, stage)
        house4.loadTexture("House4.png")

        house4.height = house4.width // Card is a square
        house4.centerAtPosition(screenWidth / 5 * 3 + house4.width / 2 + 100, (screenHeight / 2) + house4.height / 2 - 200)

        // Set event action
        house4.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                Awake.setActiveScreen(DungeonScreen(dungeonMap))
                return true
            }
        })

        val build = BaseActor(0f, 0f, stage)
        build.loadTexture("build.png")

        build.width = house4.width / 3
        build.height = build.width
        build.centerAtPosition(house4.x + 50, (screenHeight / 2) + house4.height / 2 - 400)

        val bar4 = Table()
        // bar1.background = textureRegionDrawableBg
        bar4.setPosition(house4.x + 90 + build.width, build.y + 30)
        bar4.setSize(build.width / 2, build.height / 2)
        bar4.top()
        val label4 = Label("Placeholder", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        label4.setFontScale(0.5f,0.5f)
        bar4.add(label4)
        stage.addActor(bar4)

        for (i in 0 until 3) {
            val dummy = BaseActor(0f, 0f, stage)
            dummy.loadTexture("dummy.png")
            dummy.width = house1.width / 2
            dummy.height = house1.height / 2
            dummy.centerAtPosition(
                screenWidth - dummy.width - 350,
                (screenHeight / 2) + dummy.height / 2 - 400 + 200*i
            )
        }

        val dungeon = BaseActor(0f, 0f, stage)
        dungeon.loadTexture("img.jpg")

        dungeon.height = dungeon.width * 3
        dungeon.centerAtPosition(screenWidth - dungeon.width + 150, (screenHeight / 2) + dungeon.height / 2 - 500)

        // Set event action
        dungeon.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
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