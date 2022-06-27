package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.cs446.awake.model.Board
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen


class BattleScreen(private val board: Board) : BaseScreen(){

    override fun initialize() {
        Gdx.input.inputProcessor = stage;
        val wid = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()


        val bgImg = Texture("dungeon.png")
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dungeon.png")
        background.setSize(wid, (wid / bgImg.width * bgImg.height))
        background.centerAtPosition(wid/2, height/2)

        /*
        val imgs = Array<String?>()
        imgs.add("skeleton1.png")
        imgs.add("skeleton2.png")
        imgs.add("skeleton3.png")
        imgs.add("skeleton2.png")
         */
        // val enemyImg = Texture("skeleton1.png")
        val enemy = BaseActor(0f, 0f, stage)
        enemy.loadAnimationFromFiles(board.enemy.images, 0.5f, true)
        // enemy.loadTexture("skeleton1.png")
        enemy.centerAtPosition(wid/2, height)
        enemy.moveBy(0f,-550f)

        // Border for card
        val borderTexture = Texture(Gdx.files.internal("badlogic.jpg")) // TODO: change the texture
        val borderImage = Image(borderTexture)

        // Card Actor
        val cardImg = Texture("card_empty.png")
        val cardWidth = cardImg.width.toFloat()
        var intervalWid = 40f
        val cardTotal = board.player.hand.size -1
        for ((handIndex, card) in board.player.hand.withIndex()) {
            val cardActor = BaseActor(0f, 0f, stage)
            cardActor.loadTexture(card.img)
            // y-coord is set to hide the bottom half, click to elevate?
            cardActor.centerAtPosition(0f, height - 1000f)
            cardActor.moveBy((wid-(cardTotal*cardWidth + (cardTotal-1)*intervalWid))/2 + handIndex*cardWidth,0f)

            // drag and drop listener for card
            cardActor.addListener(object : DragListener() {
                override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                    cardActor.moveBy(x - cardActor.width / 2, y - cardActor.height / 2)
                    // Control highlight border of cards
                    if (event != null) {
                        if (event.stageX in 730.0..1450.0 && event.stageY in 440.0..1080.0) {
                            val borderWidth = 5
                            borderImage.setSize( event.target.width + borderWidth * 2, event.target.height + borderWidth * 2)
                            borderImage.setPosition( event.target.x - borderWidth, event.target.y - borderWidth)
                            stage.addActor(borderImage)
                            event.target.toFront()
                        }
                        else {
                            borderImage.remove()
                        }
                    }
                }
            })

            // touchDown and touchUp Actor
            cardActor.addListener(object : InputListener() {
                override fun touchDown(
                    event: InputEvent?,
                    x: Float,
                    y: Float,
                    pointer: Int,
                    button: Int
                ): Boolean {
                    val actor = stage.hit(x, y, true)
                    if (actor != null) {
                        println("touchDown: " + actor.name.toString())
                    }
                    return true
                }

                override fun touchUp(
                    event: InputEvent?,
                    x: Float,
                    y: Float,
                    pointer: Int,
                    button: Int
                ) {
                    val actor = stage.hit(x, y, true)
                    if (event != null) {
                        if (event.stageX in 730.0..1450.0 && event.stageY in 440.0..1080.0) {
                            println("Card Used")
                            // TODO: remove card and call effect fun
                        }
                        else {
                            println("Card Not Used")
                            // TODO: card return to start position
                        }
                    }
                    borderImage.remove()
                }
            })
        }

        val stateImg = Texture("burn.png")
        val stateWidth = stateImg.width.toFloat()
        intervalWid = 40f
        val playerStateTotal = board.player.state.size -1
        for ((stateIndex, state) in board.player.state.withIndex()){
            val stateActor = BaseActor(0f, 0f, stage)
            stateActor.loadTexture(state.img)
            stateActor.centerAtPosition(-800f, height -1000f)
            stateActor.moveBy((wid-(playerStateTotal*stateWidth + (playerStateTotal-1)*intervalWid))/2 + stateIndex*stateWidth,0f)
        }


        val enemyStateTotal = board.enemy.state.size -1
        for ((stateIndex, state) in board.enemy.state.withIndex()){
            val stateActor = BaseActor(0f, 0f, stage)
            stateActor.loadTexture(state.img)
            stateActor.centerAtPosition(800f, height-100f)
            stateActor.moveBy((wid-(enemyStateTotal*stateWidth + (enemyStateTotal-1)*intervalWid))/2 + stateIndex*stateWidth,0f)
        }

    }


    override fun update(delta: Float) {
    }
}