package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.cs446.awake.Awake
import com.cs446.awake.model.*
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen
import com.cs446.awake.utils.DragDropActor

val TOTAL_ITEM = itemInfo.getStored().size
val TOTAL_MATERIAL = materialInfo.getStored().size

class BackpackScreen(val g: Int) : BaseScreen() {
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()
    private val buttonHeight = screenHeight / 6 - 75f

    // Timer variables
    private var worldTimer  = -1
    private var activeTimer = false
    private val timerLimit = 10 // Not 0 in case of concurrency issue.
    private var endTimeFcn : () -> Unit = {} // lambda function about what to do when time ends
    private var duringTimeFcn : () -> Unit = {} // lambda function about what to do when each frame passed.

    private var list = mutableListOf<String>("s","a")
    private lateinit var scrollPane : ScrollPane

    private lateinit var material : BaseActor
    private lateinit var weapon : BaseActor
    private lateinit var back : BaseActor
    private lateinit var paper : BaseActor
    private lateinit var name : Label
    private lateinit var use : Label
    private lateinit var ip : Label
    private lateinit var mp : Label

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

    fun showInfo(card: MergableCard) {
        name.setText(card.cardName)
        name.setFontScale(0.8f)
        name.setPosition(paper.x + 50, paper.y + 300)
        name.setSize(paper.width, paper.height)
        name.wrap = true
        use.setText(card.count.toString())
        use.setFontScale(0.8f)
        use.setPosition(paper.x + 50, paper.y)
        use.setSize(paper.width, paper.height)
        use.wrap = true
    }

    fun backpackScroll(i: Int) {
        paper = BaseActor(0f, 0f, stage)
        paper.loadTexture("map/empty.png")
        paper.setPosition(screenWidth / 20, screenHeight / 6 - 50f)
        paper.setSize(390f, screenHeight / 2.5f * 2 + 50f)

        name = Label("", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        use = Label("", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        stage.addActor(name)
        stage.addActor(use)

        var table = Table()
        var container = Table()

        // Card's border
        val borderTexture =
            Texture(Gdx.files.internal("highlight_border.png")) // TODO: change the texture
        val borderImage = Image(borderTexture)
        borderImage.isVisible = false
        for (c in storage.getStored()) {
            if (i == 1 && c is MaterialCard) {
                continue
            }
            if (i == 2 && c is ItemCard) {
                continue
            }
            if (g == 1 && c.count == 0) {
                continue
            }
            val cardActor = BaseActor(0f, 0f, stage)
            cardActor.loadTexture(c.img)
            val cardHeight = screenHeight / 2.5f - 100f
            cardActor.setSize(cardHeight / cardActor.height*cardActor.width, cardHeight)
            cardActor.addListener(object : InputListener() {
                override fun touchDown(
                    event: InputEvent?,
                    x: Float,
                    y: Float,
                    pointer: Int,
                    button: Int
                ): Boolean {
                    showInfo(c)
                    val borderWidth = 30
                    borderImage.setSize(
                        cardActor.width + 25,
                        cardActor.height + 25
                    )
                    borderImage.setPosition(cardActor.x-10, cardActor.y-10)
                    borderImage.isVisible = true
                    cardActor.toFront()
                    return true
                }
            })
            table.add(cardActor)
        }
        table.add(borderImage)
        table.row()

        scrollPane = ScrollPane(table)
        scrollPane.scrollTo(0f,0f,0f,0f)

        container.add(scrollPane)
        container.setPosition(screenWidth / 4.5f, screenHeight / 4 - 50f)
        container.setSize(screenWidth / 5 * 3.9f, screenHeight / 2f)
        var skin = Skin()
        skin.add("logo", Texture("bpback.png"));
        container.background(skin.getDrawable("logo"))
        container.row()
        container.getCell(scrollPane).size(1720f,container.height /2*3)
        stage.addActor(container)

        back = BaseActor(0f, 0f, stage)
        back.loadTexture("backButton.png")
        back.setSize(buttonHeight / back.height * back.width, buttonHeight)
        back.setPosition(screenWidth / 20, 20f)
        // Set event action
        back.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                villageMusic.stop()
                Awake.setActiveScreen(BackpackScreen(g))
                return true
            }
        })

    }

    override fun initialize() {
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
        background.loadTexture("darkbackground.png")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        weapon = BaseActor(0f, 0f, stage)
        weapon.loadTexture("equipment.png")
        weapon.setSize(800f, 850f)
        weapon.centerAtPosition(screenWidth / 3f, screenHeight / 2)
        // Set event action
        weapon.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                weapon.remove()
                material.remove()
                back.remove()
                ip.isVisible = false
                mp.isVisible = false
                backpackScroll(1)
                return true
            }
        })

        material = BaseActor(0f, 0f, stage)
        material.loadTexture("material.png")
        material.setSize(800f, 850f)
        material.centerAtPosition(screenWidth / 2.8f * 2, screenHeight / 2)
        // Set event action
        material.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                weapon.remove()
                material.remove()
                back.remove()
                ip.isVisible = false
                mp.isVisible = false
                backpackScroll(2)
                return true
            }
        })

        var ilen = 0
        var mlen = 0
        for (c in storage.getStored()) {
            if (c is ItemCard) {
                ilen += 1
            } else {
                mlen += 1
            }
        }
        ip = Label("$ilen / $TOTAL_ITEM", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        mp = Label("$mlen / $TOTAL_MATERIAL", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        ip.setPosition(weapon.x + 250, weapon.y + 810)
        mp.setPosition(material.x + 250, material.y + 810)
        stage.addActor(ip)
        stage.addActor(mp)
        if (g == 1) {
            ip.isVisible = false
            mp.isVisible = false
        }

        back = BaseActor(0f, 0f, stage)
        back.loadTexture("backButton.png")
        back.setSize(buttonHeight / back.height * back.width, buttonHeight)
        back.setPosition(screenWidth / 20, 20f)
        // Set event action
        back.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                villageMusic.stop()
                Awake.setActiveScreen(VillageScreen())
                return true
            }
        })

    }

    override fun update(delta: Float) {
        runTimer()
        return
    }
}