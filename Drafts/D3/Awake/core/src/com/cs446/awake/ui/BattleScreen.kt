package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.cs446.awake.model.Board
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen
import com.cs446.awake.utils.DragDropActor


class BattleScreen(private val board: Board) : BaseScreen(){

    override fun initialize() {
        super.initialize()

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

        val cardImg = Texture("card_empty.png")
        val cardWidth = cardImg.width.toFloat()
        var intervalWid = 40f
        val cardTotal = board.player.hand.size -1
        for ((handIndex, card) in board.player.hand.withIndex()) {
            val cardActor = DragDropActor(0f, 0f, stage, enemy)
            cardActor.loadTexture(card.img)
            // y-coord is set to hide the bottom half, click to elevate?
            cardActor.centerAtPosition(0f, height - 1000f)
            cardActor.moveBy((wid-(cardTotal*cardWidth + (cardTotal-1)*intervalWid))/2 + handIndex*cardWidth,0f)
            cardActor.setOnDropIntersect {
                println("intersected when dropped")
            }
            cardActor.setOnDropNoIntersect {
                cardActor.setPosition(cardActor.startX, cardActor.startY)
            }
        }

        val stateImg = Texture("burn.png")
        val stateWidth = stateImg.width.toFloat()
        intervalWid = 40f
        val stateTotal = board.player.state.size -1
        for ((stateIndex, state) in board.player.state.withIndex()){
            val stateActor = BaseActor(0f, 0f, stage)
            stateActor.loadTexture(state.img)
            stateActor.centerAtPosition(-100f, height -780f)
            stateActor.moveBy((wid-(stateTotal*stateWidth + (stateTotal-1)*intervalWid))/2 + stateIndex*stateWidth,0f)

        }

    }

    override fun update(delta: Float) {
    }
}