package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Timer
import com.cs446.awake.model.Board
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen
import com.cs446.awake.utils.DragDropActor


class BattleScreen(private val board: Board) : BaseScreen(){
    private val wid = Gdx.graphics.width.toFloat()
    private val height = Gdx.graphics.height.toFloat()
    private var round = board.currentRound

    lateinit var background : BaseActor
    lateinit var enemy : BaseActor
    var intervalWid = 10f

    private fun startGame() {
        background = BaseActor(0f, 0f, stage)
        enemy = BaseActor(0f, 0f, stage)

        background.loadTexture("dungeon.png")
        background.setSize(wid, (wid / background.width * background.height))
        background.centerAtPosition(wid / 2, height / 2)

        enemy.loadAnimationFromFiles(board.enemy.images, 0.5f, true)
        // enemy.loadTexture("skeleton1.png")
        enemy.centerAtPosition(wid / 2, height)
        enemy.moveBy(0f, -550f)

        // health bar
        // TODO: Fit health bar into Actor model
        // bar background as red
        var pixmap = Pixmap(100, 20, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.RED)
        pixmap.fill()
        var drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        val progressBarStyle = ProgressBar.ProgressBarStyle()
        progressBarStyle.background = drawable

        // health as green
        pixmap = Pixmap(0, 20, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.GREEN)
        pixmap.fill()
        drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        progressBarStyle.knob = drawable

        pixmap = Pixmap(100, 20, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.GREEN)
        pixmap.fill()
        drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        progressBarStyle.knobBefore = drawable


        val healthBar = ProgressBar(0.0f, 1.0f, 0.01f, false, progressBarStyle)
        healthBar.value = 1.0f
        healthBar.setAnimateDuration(0.25f)
        healthBar.setBounds(500F, 1000F, 1000F, 20F)

        stage.addActor(healthBar)
        board.enemy.healthBar = healthBar
        healthBar.setRange(0f, board.enemy.HP / 100.0f) // TODO: everytime deal damage, update this
        healthBar.value = board.enemy.HP / 100.0f
        val stateImg = Texture("burn.png")
        val stateWidth = stateImg.width.toFloat()

        // init player states

        val stateList =
            Array<String>(arrayOf("Burn", "Freeze", "Poison", "Paralysis", "Sleep"))
        for ((stateIndex, state) in stateList.withIndex()) {
            val stateActor = BaseActor(0f, 0f, stage)
            board.player.characterStateMap[state] = stateActor

            stateActor.loadTexture(state.lowercase() + ".png")
            stateActor.centerAtPosition(-800f, height - 1000f)
            stateActor.moveBy(
                (wid - (4 * stateWidth + 3 * intervalWid)) / 2 + stateIndex * stateWidth,
                0f
            )
            stateActor.setOpacity(0.3f)

            for (playerState in board.player.state) {
                if (playerState.stateName == state) stateActor.setOpacity(1f)
            }
        }


        // init enemy states

        for ((stateIndex, state) in stateList.withIndex()) {
            val stateActor = BaseActor(0f, 0f, stage)
            board.enemy.characterStateMap[state] = stateActor

            stateActor.loadTexture(state.lowercase() + ".png")
            stateActor.centerAtPosition(800f, height - 100f)
            stateActor.moveBy(
                (wid - (4 * stateWidth + (4 - 1) * intervalWid)) / 2 + stateIndex * stateWidth,
                0f
            )
            stateActor.setOpacity(0.3f)

            for (enemyState in board.player.state) {
                if (enemyState.stateName == state) stateActor.setOpacity(1f)
            }
        }

        if (!board.isAITurn()) {
            // Border for card
            val borderTexture =
                Texture(Gdx.files.internal("highlight_border.png")) // TODO: change the texture
            val borderImage = Image(borderTexture)

            val endTurnActor = BaseActor(0f, 0f, stage)
            endTurnActor.loadTexture("EndTurnButton.png")
            endTurnActor.centerAtPosition(wid - 250f, 150f)
            endTurnActor.addListener(object : InputListener() {
                override fun touchDown(
                    event: InputEvent?,
                    x: Float,
                    y: Float,
                    pointer: Int,
                    button: Int
                ): Boolean {
                    board.postRound()
                    board.switchTurn()
                    endTurnActor.remove()
                    return true
                }
            })

            // Card Actor
            val cardTotal = board.player.hand.size - 1
            for ((handIndex, card) in board.player.hand.withIndex()) {
                val cardActor = DragDropActor(0f, 0f, stage, enemy)
                cardActor.loadTexture(card.img)
                // y-coord is set to hide the bottom half, click to elevate?
                cardActor.centerAtPosition(0f, height - 950f)
                cardActor.moveBy(
                    (wid - (cardTotal * cardActor.width + (cardTotal - 1) * intervalWid)) / 2 + handIndex * (cardActor.width + intervalWid),
                    0f
                )

                cardActor.setOnDropIntersect {
                    println("intersected when dropped")
                    // check later on
                    // if (board.checkTurn(board.player)) {
                    cardActor.remove()
                    board.removeCard(card)
                    board.notify(card)
                    borderImage.remove()
                }
                cardActor.setOnDropNoIntersect {
                    cardActor.setPosition(cardActor.startX, cardActor.startY)
                    borderImage.remove()
                }
                cardActor.setOnDragIntersect {
                    //println("CARD USING?")
                    val borderWidth = 30
                    borderImage.setSize(
                        cardActor.width + borderWidth * 2,
                        cardActor.height + borderWidth * 2
                    )
                    borderImage.setPosition(cardActor.x - borderWidth, cardActor.y - borderWidth)
                    stage.addActor(borderImage)
                    cardActor.toFront()
                }
                cardActor.setOnDragNoIntersect {
                    borderImage.remove()
                }

            }
        }

            // Eric start


            if (board.isAITurn()) {
                val cardTotal = board.player.hand.size - 1
                for ((handIndex, card) in board.player.hand.withIndex()) {
                    val cardActor = BaseActor(0f, 0f, stage)
                    cardActor.loadTexture(card.img)
                    // y-coord is set to hide the bottom half, click to elevate?
                    cardActor.centerAtPosition(0f, height - 950f)
                    cardActor.moveBy(
                        (wid - (cardTotal * cardActor.width + (cardTotal - 1) * intervalWid)) / 2 + handIndex * (cardActor.width + intervalWid),
                        0f
                    )
                }

                val font = BitmapFont(Gdx.files.internal("Arial120Bold.fnt"))
                val buttonStyle = TextButtonStyle()
                buttonStyle.font = font
                val textButton = TextButton("Finish AI Round", buttonStyle)
                textButton.y = height / 2 + textButton.height / 2
                stage.addActor(textButton)
                textButton.addListener(object : InputListener() {
                    override fun touchDown(
                        event: InputEvent?,
                        x: Float,
                        y: Float,
                        pointer: Int,
                        button: Int
                    ): Boolean {
                        board.activeAI()
                        textButton.remove()
                        board.postRound()
                        board.switchTurn()
                        return true
                    }
                })
            }
        }

    override fun initialize() {
        Gdx.input.inputProcessor = stage;

        // Eric Start
        val font = BitmapFont(Gdx.files.internal("Arial120Bold.fnt"))
        val buttonStyle = TextButtonStyle()
        buttonStyle.font = font
        val textButton = TextButton("Start Fight!", buttonStyle)
        textButton.setPosition(wid/2 - textButton.width/2, height/2 + textButton.height/2)
        stage.addActor(textButton)
        textButton.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                textButton.remove()
                board.startRound()
                startGame()
                return true
            }
        })
        // Eric End
    }

    override fun update(delta: Float) {
        if (round != board.currentRound) {
            stage.clear()
            startGame()
            round = board.currentRound
        }

        if (board.win() == true) {
            stage.clear()
            val font = BitmapFont(Gdx.files.internal("Arial120Bold.fnt"))
            val buttonStyle = TextButtonStyle()
            buttonStyle.font = font
            val textButton = TextButton("You win!", buttonStyle)
            textButton.setPosition(wid/2 - textButton.width/2, height/2 - textButton.height/2)
            stage.addActor(textButton)
        }

        if (board.win() == false) {
            stage.clear()
            val font = BitmapFont(Gdx.files.internal("Arial120Bold.fnt"))
            val buttonStyle = TextButtonStyle()
            buttonStyle.font = font
            val textButton = TextButton("You lose", buttonStyle)
            textButton.setPosition(wid/2 - textButton.width/2, height/2 - textButton.height/2)
            stage.addActor(textButton)
        }
    }
}