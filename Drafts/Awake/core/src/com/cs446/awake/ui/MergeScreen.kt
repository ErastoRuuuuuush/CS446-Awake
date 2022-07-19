package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.cs446.awake.model.*
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen
import com.cs446.awake.utils.DragDropActor
import com.cs446.awake.utils.AutoScrollPane

class MergeScreen() : BaseScreen() {

    // Screen size
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()

    // Background
    private lateinit var background : BaseActor

    // Merge Area
    private lateinit var mergeDisplay : BaseActor

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

    // Split Pane that contains Merge Area at the top and Material Table at the bottom
    //private lateinit var splitPane : SplitPane

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

    private fun renderTable() {
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
                val cardActor = DragDropActor(0f, 0f, stage, mergeDisplay, inTable = true)
                cardActor.toFront()
                cardActor.loadTexture("PoisonCard.png") //TODO: read card image & info
                // if not on the merge area, return back to the start position
                cardActor.setOnDropNoIntersect {
                    cardActor.setPosition(cardActor.startX, cardActor.startY)
                }
                // if on the merge area, update merge list and storage
                cardActor.setOnDropIntersect {
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

                    mergeAreaCards.add(cardActor)
                }
                cardStack.add(cardActor)
                count--
            }

            materialTable.add(cardStack).expandX().pad(10f).right()
        }
        tableDisplay.actor = materialTable
        stage.addActor(tableDisplay)
    }

    override fun initialize() {
        // test data
        storage.add(stone)
        storage.add(stone)
        storage.add(log)
        storage.add(log)
        storage.add(log)
        tableDisplay.setSize(screenWidth, screenHeight - 500)
        tableDisplay.setPosition(0f,screenHeight -1000 )
        println("Init Storage:")
        for (i in storage.getStored()) {
            println(i.cardName + " " + i.count.toString())
        }
        materials = storage.getStored()

        Gdx.input.inputProcessor = stage

        // set background
        background = BaseActor(0f, 0f, stage)
        background.loadTexture("dragon.jpeg") //TODO: background image
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        // set merge area
        mergeDisplay = BaseActor(0f, 0f, stage)
        mergeDisplay.loadTexture("transparent.png") //TODO: transparent merge area
        mergeDisplay.setSize(screenWidth, screenHeight / 2)
        mergeDisplay.centerAtPosition(screenWidth / 2, screenHeight * 3 / 4)

        // init table
        renderTable()

        // merge card button
        mergeCard = BaseActor(0f, 0f, stage)
        mergeCard.loadTexture("EndTurnButton.png")
        mergeCard.centerAtPosition(screenWidth - 250f, screenHeight / 2)
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
                val outputCard = mergeData.merge(mergeData, itemInfo)
                println("Merge Clicked")
                if (outputCard != null) {
                    println("Card Merged")
                    storage.add(outputCard)

                    // add this item into storage
                    val outputCardActor = BaseActor(0f, 0f, stage)
                    outputCardActor.toFront()
                    outputCardActor.loadTexture("skeleton1.png") //TODO: read card image & info
                    // TODO: remove card after several seconds

                    mergeAreaCards.clear()

                    // refresh table
                    renderTable()
                } else {
                    println("Invalid Merge")
                }

                // TODO: show this card and add this card to item list, generate the cardActor
                return true
            }
        })

        // clear merge button
        clearMerge = BaseActor(0f, 0f, stage)
        clearMerge.loadTexture("EndTurnButton.png")
        clearMerge.centerAtPosition(250f, screenHeight - 100f)
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

        //splitPane = SplitPane(mergeDisplay, tableDisplay, true, skin)
    }

    override fun update(delta: Float) {

    }

}