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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.cs446.awake.Awake
import com.cs446.awake.model.*
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen
import com.cs446.awake.utils.DragDropActor

class MergeScreen() : BaseScreen() {

    // Screen size
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()

    private val buttonHeight = screenHeight / 6 - 75f
    private val cardHeight = screenHeight / 3

    // Background
    private lateinit var background : BaseActor
    private lateinit var paper : BaseActor

    // Merge Area
    private lateinit var mergeArea : BaseActor
    private var mergeDisplay = Container<Table>()
    private val mergeTable = Table()

    // Merge Button
    private lateinit var mergeCard : BaseActor

    // Clear Button
    private lateinit var clearMerge : BaseActor

    // Material Info
    private var mergeData = CardData(mutableListOf())
    private var materials = storage.getStored()

    // Material Table in a scroll pane
    private val materialTable = Table()
    // private val tableDisplay = AutoScrollPane(materialTable)
    private val tableDisplay = Container<Table>()

    // List store card actors
    private val mergeAreaCards = ArrayList<DragDropActor>()

    private var changeX = 0f
    private var toMerge = false
    // Split Pane that contains Merge Area at the top and Material Table at the bottom
    //private lateinit var splitPane : SplitPane

    // Timer variables
    private var worldTimer  = -1
    private var activeTimer = false
    private val timerLimit = 10 // Not 0 in case of concurrency issue.
    private var endTimeFcn : () -> Unit = {} // lambda function about what to do when time ends
    private var duringTimeFcn : () -> Unit = {} // lambda function about what to do when each frame passed.

    private lateinit var back : BaseActor


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

    /*
    private fun generateDragDrop(material: MergableCard, tableActor: BaseActor, cardStack: Stack) {
        val cardActor = DragDropActor(0f, 0f, stage, mergeDisplay, inTable = true)
        cardActor.toFront()
        cardActor.loadTexture("PoisonCard.png") //TODO: read card image & info
        // if not on the merge area, return back to the start position
        cardActor.setOnDropNoIntersect {
            cardActor.setPosition(cardActor.startX, cardActor.startY)
        }
        // if on the merge area, update merge list and storage
        cardActor.setOnDropIntersect {
            val oneMaterial : MergableCard = material.clone()
            oneMaterial.count = 1

            mergeData.add(oneMaterial)
            storage.remove(oneMaterial)
            println("Merge Area:")
            for (i in mergeData.getStored()) {
                println(i.cardName + " " + i.count.toString())
            }
            println("Storage:")
            for (i in storage.getStored()) {
                println(i.cardName + " " + i.count.toString())
            }

            mergeAreaCards.add(cardActor)

            if (material.count == 0) { // if empty
                // set to transparent before actually merge (leave a spot)
                tableActor.loadTexture("transparent.png")
            } else {
                // generate a new copy and add to stack
                generateDragDrop(material, tableActor, cardStack)
                cardStack.add(cardActor)
            }
        }
    }

     */

    private fun renderTable() {
        mergeTable.clear()
        materialTable.clear()
        for (material in materials) {
            val cardStack = Stack()
            if (material.count == 0) {
                val cardTableActor = BaseActor(0f, 0f, stage, inTable = true)
                cardTableActor.toFront()
                cardTableActor.loadTexture("transparent.png") //TODO: read card image & info
                cardStack.add(cardTableActor)
                continue
            }
            var count = material.count
            while (count > 0) {
                val cardActor = DragDropActor(0f, 0f, stage, mergeArea, inTable = true)
                cardActor.toFront()
                cardActor.loadTexture(material.img)
                // cardActor.setSize(300f, 300f)
                cardActor.setSize(cardHeight/cardActor.height*cardActor.width, cardHeight)
                // if not on the merge area, return back to the start position
                cardActor.setOnDropNoIntersect {
                    mergeArea.isVisible = false
                    toMerge = false
                    cardActor.setPosition(cardActor.startX, cardActor.startY)
                }
                // if on the merge area, update merge list and storage
                cardActor.setOnDropIntersect {
                    mergeArea.isVisible = false
                    toMerge = false
                    if (mergeAreaCards.size >= 7) {
                        cardActor.setPosition(cardActor.startX, cardActor.startY)
                    }
                    else if (!mergeAreaCards.contains(cardActor)){
                        val oneMaterial: MergableCard = material.clone()
                        oneMaterial.count = 1

                        mergeData.add(oneMaterial)
                        storage.remove(oneMaterial)
                        println("Merge Area:")
                        for (i in mergeData.getStored()) {
                            println(i.cardName + " " + i.count.toString())
                        }
                        println("Storage:")
                        for (i in storage.getStored()) {
                            println(i.cardName + " " + i.count.toString())
                        }

                        cardStack.removeActor(cardActor)
                        mergeAreaCards.add(cardActor)
                        val newCard = BaseActor(0f, 0f, stage, inTable = true)
                        newCard.toFront()

                        newCard.loadTexture(oneMaterial.img)
                        newCard.setSize(cardHeight/newCard.height*newCard.width, cardHeight)


                        mergeTable.add(newCard).expandX().pad(10f).right()
                    }
                }

                // controls drag card or swipe table
                cardActor.setOnDragNoIntersect {
                    if (!toMerge) {
                        changeX = cardActor.x - cardActor.startX
                        tableDisplay.setPosition(tableDisplay.x + changeX, tableDisplay.y)
                    }
                    mergeArea.isVisible = true
                }
                cardActor.setOnDragIntersect {
                    toMerge = true
                    mergeArea.isVisible = true
                }
                cardStack.add(cardActor)
                count--
            }

            materialTable.add(cardStack).expandX().pad(10f).right()
        }
        tableDisplay.actor = materialTable
        stage.addActor(tableDisplay)

        mergeDisplay.actor = mergeTable
        stage.addActor(mergeDisplay)
        tableDisplay.toFront()
    }

    override fun initialize() {
        // test data
        storage.add(log)
        storage.add(log)
        storage.add(log)
        storage.add(log)
        storage.add(log)

        // set background
        background = BaseActor(0f, 0f, stage)
        background.loadTexture("dragonBackground.png") //TODO: background image
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        var mergeField = BaseActor(0f,0f,stage)
        mergeField.loadTexture("bpback.png")
        mergeField.setSize(screenWidth*2, screenHeight/2.5f)
        mergeField.centerAtPosition(screenWidth/2,screenHeight / 4 * 3f + 15f)

        val mergeText = BaseActor(0f, 0f, stage)
        mergeText.loadTexture("mergeField.png")
        val mergeTextHeight = screenHeight/8
        mergeText.setSize(mergeTextHeight / mergeText.height * mergeText.width, mergeTextHeight)
        mergeText.setPosition(50f,screenHeight -120)

        /*
        val txt1 = Label("Merge Field", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        txt1.setSize(screenWidth/3, screenHeight/2)
        txt1.setPosition(50f,screenHeight -400)
        txt1.setFontScale(0.5f)
        txt1.wrap = true
        stage.addActor(txt1)

         */

        var storageField = BaseActor(0f,0f,stage)
        storageField.loadTexture("bpback.png")
        storageField.setSize(screenWidth*2, screenHeight/2.5f)
        storageField.centerAtPosition(screenWidth/2,screenHeight / 3 )

        val storageText = BaseActor(0f, 0f, stage)
        storageText.loadTexture("storageField.png")
        val storageTextHeight = screenHeight/8
        storageText.setSize(storageTextHeight / storageText.height * storageText.width, storageTextHeight)
        storageText.setPosition(50f,screenHeight -565)

        /*
        val txt2 = Label("Storage Field", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        txt2.setSize(screenWidth/3, screenHeight/2)
        txt2.setPosition(50f,screenHeight -800)
        txt2.setFontScale(0.5f)
        txt2.wrap = true
        stage.addActor(txt2)

         */

        tableDisplay.setSize(screenWidth/2, screenHeight/2.5f)
        tableDisplay.setPosition(500f,screenHeight / 6 - 40f )
        println("Init Storage:")
        for (i in storage.getStored()) {
            println(i.cardName + " " + i.count.toString())
        }
        materials = storage.getStored()

        Gdx.input.inputProcessor = stage

        // set merge area
        mergeDisplay.setSize(screenWidth/2, screenHeight/2.5f)
        mergeDisplay.setPosition(500f,screenHeight -450 )
        mergeArea = BaseActor(0f, 0f, stage)
        mergeArea.toFront()
        mergeArea.loadTexture("highlight_border.png")
        mergeArea.setSize(screenWidth, screenHeight/2.5f)
        mergeArea.centerAtPosition(screenWidth / 2, screenHeight * 3 / 4)
        mergeArea.isVisible = false

        // init table
        renderTable()

        // merge card button
        mergeCard = BaseActor(0f, 0f, stage)
        mergeCard.loadTexture("merge.png")
        mergeCard.setSize(buttonHeight / mergeCard.height * mergeCard.width, buttonHeight)
        mergeCard.setPosition(screenWidth / 20 * 19 - mergeCard.width, 20f)
        mergeCard.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                // TODO: check empty
                // the output item after merging

                val outputCard = mergeData.merge()

                println("Merge Clicked")
                if (outputCard != null) {
                    println("Card Merged")

                    // txt1.isVisible = false
                    // txt2.isVisible = false
                    mergeText.isVisible = false
                    storageText.isVisible = false
                    mergeField.isVisible = false
                    storageField.isVisible = false
                    storage.add(outputCard)

                    mergeAreaCards.clear()
                    mergeData = CardData(mutableListOf())

                    val txt = Label("Congratulation! You have successfully merged these cards. Click to collect the card.", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
                    txt.setSize(screenWidth/3, screenHeight/2)
                    txt.setPosition(screenWidth/5, screenHeight/2)
                    txt.setFontScale(0.5f)
                    txt.wrap = true
                    stage.addActor(txt)

                    mergeTable.clear()
                    materialTable.clear()

                    // add this item into storage
                    val outputCardActor = BaseActor(0f, 0f, stage)
                    outputCardActor.toFront()
                    outputCardActor.loadTexture(outputCard.img) //TODO: read card image & info
                    outputCardActor.setPosition(screenWidth/2 - outputCardActor.width/2, screenHeight/2 - 300)
                    outputCardActor.setSize(cardHeight/outputCardActor.height*outputCardActor.width * 1.3f, cardHeight*1.3f)


                    outputCardActor.addListener(object : InputListener() {
                        override fun touchDown(
                            event: InputEvent?,
                            x: Float,
                            y: Float,
                            pointer: Int,
                            button: Int
                        ): Boolean {
                            // refresh table
                            txt.isVisible = false
                            // txt1.isVisible = true
                            // txt2.isVisible = true
                            mergeText.isVisible = true
                            storageText.isVisible = true
                            mergeField.isVisible = true
                            storageField.isVisible = true
                            outputCardActor.remove()
                            renderTable()

                            return true
                        }
                    })
//
//                    // remove card after several seconds
//                    val duringTime : () -> Unit = { outputCardActor.setOpacity(worldTimer / 60f) }
//                    val endTime : () -> Unit = {
//                        outputCardActor.remove()
//                    }
//                    startTimer(180, endTime, duringTime) // about 3 seconds
                } else {
                    println("Invalid Merge")
                }

                // TODO: show this card and add this card to item list, generate the cardActor
                return true
            }
        })

        // clear merge button
        clearMerge = BaseActor(0f, 0f, stage)
        clearMerge.loadTexture("cancel1.png")
        clearMerge.setSize(buttonHeight / clearMerge.height * clearMerge.width, buttonHeight)
        clearMerge.setPosition(screenWidth / 2 - clearMerge.width / 2, 20f)
        clearMerge.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                println("Clear Clicked")
                // backend restore cards
                storage.append(mergeData)
                mergeData = CardData(mutableListOf())

                // clear merge area
                mergeAreaCards.clear()

                // refresh table
                renderTable()

                return true
            }
        })

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
                Awake.setActiveScreen(VillageScreen())
                return true
            }
        })

    }

    override fun update(delta: Float) {
        runTimer()
    }

}