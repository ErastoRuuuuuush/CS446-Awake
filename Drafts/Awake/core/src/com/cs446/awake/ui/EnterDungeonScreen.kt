package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
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


class EnterDungeonScreen() : BaseScreen() {
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()

    // Timer variables
    private var worldTimer  = -1
    private var activeTimer = false
    private val timerLimit = 10 // Not 0 in case of concurrency issue.
    private var endTimeFcn : () -> Unit = {} // lambda function about what to do when time ends
    private var duringTimeFcn : () -> Unit = {} // lambda function about what to do when each frame passed.

    private var list = mutableListOf<String>("s","a")
    private lateinit var scrollPane : ScrollPane

    private lateinit var enter : BaseActor
    private lateinit var select : BaseActor
    private lateinit var back : BaseActor
    private lateinit var paper : BaseActor
    private lateinit var name : Label
    private lateinit var use : Label
    private lateinit var card : MergableCard
    private lateinit var border1 : Image
    private lateinit var border : Image
    private var v = true
    private var selected = false

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

    private fun showInfo(card: MergableCard) {
        name.setText(card.cardName)
        name.setFontScale(0.8f)
        name.setPosition(paper.x + 50, paper.y + 300)
        name.setSize(paper.width, paper.height)
        name.wrap = true
        use.setText(card.usage)
        use.setFontScale(0.8f)
        use.setPosition(paper.x + 50, paper.y)
        use.setSize(paper.width, paper.height)
        use.wrap = true
    }

    private fun backpackScroll() {
        paper = BaseActor(0f, 0f, stage)
        paper.loadTexture("paperboarder.png")
        paper.setPosition(100f, 300f)
        paper.setSize(390f, 780f)

        name = Label("", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        use = Label("", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        stage.addActor(name)
        stage.addActor(use)

        var table = Table()
        var container = Table()
        // Card's border
        val borderTexture =
            Texture(Gdx.files.internal("highlight_border.png")) // TODO: change the texture
        border = Image(borderTexture)
        border.isVisible = false
        for (c in storage.getStored()) {
            if (c is MaterialCard) {
                continue
            }
            if (c.count == 0) {
                continue
            }
            val cardActor = BaseActor(0f, 0f, stage)
            cardActor.loadTexture(c.img)
            cardActor.setSize(500f, 600f)
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
                    border.isVisible = true
                    border.setSize(
                        cardActor.width - 20,
                        cardActor.height + borderWidth * 2
                    )
                    border.isVisible = true
                    border.setPosition(cardActor.x + 20, cardActor.y - borderWidth)
                    border1.isVisible = false
                    card = c.clone()
                    v = true
                    selected = true
                    return true
                }
            })
            table.add(cardActor)
        }
        table.add(border)
        table.row()
        //table.setSize(screenWidth - 300, 860f)

        scrollPane = ScrollPane(table)
        scrollPane.scrollTo(0f,0f,0f,0f)

        container.add(scrollPane)
        container.setPosition(300f, 260f)
        container.setSize(screenWidth - 100, 430f)
        var skin = Skin()
        skin.add("logo", Texture("bpback.png"));
        container.background(skin.getDrawable("logo"))
        container.row()
        container.getCell(scrollPane).size(1900f,600f)
        stage.addActor(container)

        var table1 = Table()
        var container1 = Table()
        val borderTexture1 =
            Texture(Gdx.files.internal("highlight_border.png")) // TODO: change the texture
        border1 = Image(borderTexture1)
        border1.isVisible = false
        for (c in backPackItem.getStored()) {
            if (c is MaterialCard) {
                continue
            }
            if (c.count == 0) {
                continue
            }
            val cardActor = BaseActor(0f, 0f, stage)
            cardActor.loadTexture(c.img)
            cardActor.setSize(500f, 600f)
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
                    border1.isVisible = true
                    border1.setSize(
                        cardActor.width - 20,
                        cardActor.height + borderWidth * 2
                    )
                    border1.isVisible = true
                    border1.setPosition(cardActor.x + 20, cardActor.y - borderWidth)
                    border.isVisible = false
                    card = c.clone()
                    v = false
                    selected = true
                    return true
                }
            })
            table1.add(cardActor)
        }
        table1.add(border1)
        table1.row()
        //table.setSize(screenWidth - 300, 860f)

        scrollPane = ScrollPane(table1)
        scrollPane.scrollTo(0f,0f,0f,0f)

        container1.add(scrollPane)
        container1.setPosition(300f, 860f)
        container1.setSize(screenWidth - 100, 430f)
        var skin1 = Skin()
        skin1.add("logo", Texture("bpback.png"));
        container1.background(skin1.getDrawable("logo"))
        container1.row()
        container1.getCell(scrollPane).size(1900f,600f)
        stage.addActor(container1)

    }

    override fun initialize() {
        Gdx.input.inputProcessor = stage
        //stage.addActor(countdownLabel)
        //countdownLabel.setPosition(screenWidth/2 - countdownLabel.width/2, screenHeight/2 + countdownLabel.height/2)

        // Background Picture
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dragonBackground.png")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        backpackScroll()
        enter = BaseActor(0f, 0f, stage)
        enter.loadTexture("EndTurnButton.png")
        enter.setSize(300f, 350f)
        enter.centerAtPosition(screenWidth / 5 * 4 + enter.width / 2 + 200, (screenHeight / 2) + enter.height / 2 - 600)
        // Set event action
        enter.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                Awake.setActiveScreen(DungeonScreen(DungeonMap(dungeonLevel)))
                return true
            }
        })

        select = BaseActor(0f, 0f, stage)
        select.loadTexture("EndTurnButton.png")
        select.setSize(300f, 350f)
        select.centerAtPosition(screenWidth / 5 * 2 + select.width / 2 + 200, (screenHeight / 2) + select.height / 2 - 600)
        // Set event action
        select.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                if (!selected) {
                    return true
                }
                if (v) {
                    storage.remove(card)
                    backPackItem.add(card)
                } else {
                    backPackItem.remove(card)
                    storage.add(card)
                }
                Awake.setActiveScreen(EnterDungeonScreen())
                return true
            }
        })

        back = BaseActor(0f, 0f, stage)
        back.loadTexture("EndTurnButton.png")
        back.setSize(300f, 350f)
        back.centerAtPosition(screenWidth / 5 * 0 + back.width / 2 + 200, (screenHeight / 2) + back.height / 2 - 600)
        // Set event action
        back.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
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