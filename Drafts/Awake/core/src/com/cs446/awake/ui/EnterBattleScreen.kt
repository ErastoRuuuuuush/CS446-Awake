package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.cs446.awake.Awake
import com.cs446.awake.model.*
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen


class EnterBattleScreen() : BaseScreen() {
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
        use.setText(card.count.toString())
        use.setFontScale(0.8f)
        use.setPosition(paper.x + 50, paper.y)
        use.setSize(paper.width, paper.height)
        use.wrap = true
    }

    private fun backpackScroll() {
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
        border = Image(borderTexture)
        border.isVisible = false
        for (c in backPackItem.getStored()) {
            if (c is MaterialCard) {
                continue
            }
            if (c.count == 0) {
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
                    select.loadTexture("selectButton.png")
                    select.setSize(buttonHeight / select.height * select.width, buttonHeight)
                    select.setPosition(screenWidth / 2 - select.width / 2, 20f)
                    showInfo(c)
                    border.isVisible = true
                    border.setSize(
                        cardActor.width + 25,
                        cardActor.height + 25
                    )
                    border.isVisible = true
                    border.setPosition(cardActor.x-10, cardActor.y-10)
                    border1.isVisible = false
                    card = c.clone()
                    card.count = 1
                    v = true
                    selected = true
                    cardActor.toFront()
                    return true
                }
            })
            table.add(cardActor).pad(15f)
        }
        table.add(border)
        table.row()
        //table.setSize(screenWidth - 300, 860f)

        scrollPane = ScrollPane(table)
        table.setFillParent(true)
        scrollPane.scrollTo(0f,0f,0f,0f)

        container.setPosition(screenWidth / 4.5f, screenHeight / 6 - 50f)
        container.setSize(screenWidth / 5 * 3.9f, screenHeight / 2.5f)
        var skin = Skin()
        skin.add("logo", Texture("bpback.png"));
        container.background(skin.getDrawable("logo"))
        container.add(scrollPane).height(screenHeight / 2.5f).expandX()
        container.row()
        container.getCell(scrollPane).size(1750f,container.height /2*3)
        table.bottom()
        table.left()
        stage.addActor(container)

        // table1 is the upper row
        var table1 = Table()
        var container1 = Table()
        val borderTexture1 =
            Texture(Gdx.files.internal("highlight_border.png")) // TODO: change the texture
        border1 = Image(borderTexture1)
        border1.isVisible = false
        for (c in battleItem.getStored()) {
            if (c is MaterialCard) {
                continue
            }
            if (c.count == 0) {
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
                    border1.isVisible = true
                    border1.setSize(
                        cardActor.width + 25,
                        cardActor.height + 25
                    )
                    border1.isVisible = true
                    border1.setPosition(cardActor.x-10, cardActor.y-10)
                    border.isVisible = false

                    card = c.clone()
                    v = false
                    selected = true
                    select.loadTexture("cancel.png")
                    select.setSize(buttonHeight / select.height * select.width, buttonHeight)
                    select.setPosition(screenWidth / 2 - select.width / 2, 20f)
                    cardActor.toFront()
                    return true
                }
            })
            table1.add(cardActor).pad(15f)
        }
        table1.add(border1)
        table1.row()
        //table.setSize(screenWidth - 300, 860f)

        scrollPane = ScrollPane(table1)
        table1.setFillParent(true)
        scrollPane.scrollTo(0f,0f,0f,0f)

        container1.setPosition(screenWidth / 4.5f, screenHeight / 6 + container.height)
        container1.setSize(screenWidth / 5 * 3.9f, screenHeight / 2.5f)
        var skin1 = Skin()
        skin1.add("logo", Texture("bpback.png"));
        container1.background(skin1.getDrawable("logo"))
        container1.add(scrollPane).height(screenHeight / 2.5f).expandX()
        container1.row()
        container1.getCell(scrollPane).size(1750f,container.height /2*3)
        table1.bottom()
        table1.left()
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
        enter.loadTexture("goButton.png")

        enter.setSize(buttonHeight / enter.height * enter.width, buttonHeight)
        enter.setPosition(screenWidth / 20 * 19 - enter.width, 20f)
        // Set event action
        enter.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                // generate new player deck according to the items in backpack
                deck = Deck()
                for (item in battleItem.getStored()){
                    if (item !is ItemCard){
                        println("Warning: non-material in backpack")
                        continue
                    }
                    item.addToDeck()
                }
                player = Player("Hero", getHP(), getEnergy(), strength, "hero_bar.png", deck, mutableListOf(), PlayerType.Human)
                if (player != null && enemy != null) {
                    val p : Player = player as Player
                    val e : Enemy = enemy as Enemy
                    Awake.setActiveScreen(BattleScreen(p, e))
                }
                return true
            }
        })

        select = BaseActor(0f, 0f, stage)
        select.loadTexture("selectButton.png")
        select.setSize(buttonHeight / select.height * select.width, buttonHeight)
        select.setPosition(screenWidth / 2 - select.width / 2, 20f)
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
                    backPackItem.remove(card)
                    battleItem.add(card)
                } else {
                    battleItem.remove(card)
                    backPackItem.add(card)
                }
                println(battleItem.itemList.size)
                Awake.setActiveScreen(EnterBattleScreen())
                return true
            }
        })

    }

    override fun update(delta: Float) {
        runTimer()
        return
    }
}