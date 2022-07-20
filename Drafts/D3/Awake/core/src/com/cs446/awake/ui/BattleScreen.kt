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
import com.cs446.awake.Awake
import com.cs446.awake.model.*
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen
import com.cs446.awake.utils.DragDropActor
import kotlin.math.abs


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

        val enemyAttackActor = BaseActor(0f, 0f, stage)
        enemyAttackActor.loadTexture("card_empty.png")
        enemyAttackActor.setOpacity(0.3f)
        enemyAttackActor.setSize(enemyAttackActor.width*2, enemyAttackActor.height*2)
        enemyAttackActor.centerAtPosition(wid/2, height)
        enemyAttackActor.moveBy(0f, -300f)



        // enemy actor
        stage.addActor(board.enemy.healthBar)
        stage.addActor(board.enemy.energyBar)
        val enemyImageActor = BaseActor(0f, 0f, stage)
        enemyImageActor.loadTexture(board.enemy.enemyImage)
        enemyImageActor.centerAtPosition(wid - 220f, height - 150f)

        // player actor
        stage.addActor(board.player.healthBar)
//        stage.addActor(board.player.strengthBar)
        stage.addActor(board.player.energyBar)
        val playerImageActor = BaseActor(0f, 0f, stage)
        playerImageActor.loadTexture(board.player.playerImage)
        playerImageActor.centerAtPosition(220f, 150f)


        val stateImg = Texture("burn.png")
        val stateWidth = stateImg.width.toFloat()

        // init player states

        val stateList =
            Array<String>(arrayOf("Burn", "Freeze", "Poison", "Paralysis", "Sleep"))
        for ((stateIndex, state) in stateList.withIndex()) {
            val stateActor = BaseActor(0f, 0f, stage)
            board.player.characterStateMap[state] = stateActor

            stateActor.loadTexture(state.lowercase() + ".png")
            stateActor.centerAtPosition(-650f, height - 850f)
            stateActor.moveBy(
                (wid - (4 * stateWidth + 3 * intervalWid)) / 2 + stateIndex * stateWidth,
                0f
            )
            stateActor.setOpacity(0.3f)

            for (playerState in board.player.states) {
                if (playerState.stateName == state) stateActor.setOpacity(1f)
            }
        }


        // init enemy states

        for ((stateIndex, state) in stateList.withIndex()) {
            val stateActor = BaseActor(0f, 0f, stage)
            board.enemy.characterStateMap[state] = stateActor

            stateActor.loadTexture(state.lowercase() + ".png")
            stateActor.centerAtPosition(650f, height - 150f)
            stateActor.moveBy(
                (wid - (4 * stateWidth + (4 - 1) * intervalWid)) / 2 + stateIndex * stateWidth,
                0f
            )
            stateActor.setOpacity(0.3f)

            for (enemyState in board.enemy.states) {
                if (enemyState.stateName == state) stateActor.setOpacity(1f)
            }
        }

        if (!board.isAITurn()) {
            // Border for card
            val borderTexture =
                Texture(Gdx.files.internal("highlight_border.png")) // TODO: change the texture
            val borderImage = Image(borderTexture)

            val descriptionTexture =
                Texture(Gdx.files.internal("card_empty.png"))
            val descriptionImage = Image(descriptionTexture)

            val descriptionTable = Table()
            descriptionTable.add(descriptionImage).fill()
//            descriptionTable.add(Label("Dummy Description", Skin())).expandY().fillY()

            val endTurnActor = BaseActor(0f, 0f, stage)
            endTurnActor.loadTexture("EndTurnButton.png")
            endTurnActor.centerAtPosition(wid - 250f, 400f)
            endTurnActor.addListener(object : InputListener() {
                override fun touchDown(
                    event: InputEvent?,
                    x: Float,
                    y: Float,
                    pointer: Int,
                    button: Int
                ): Boolean {
                    board.endRound()
                    endTurnActor.remove()
                    return true
                }
            })

            // Card Actor
            val cardTotal = board.player.hand.size - 1
            for ((handIndex, card) in board.player.hand.withIndex()) {
                val cardActor = DragDropActor(0f, 0f, stage, enemyAttackActor, playerImageActor)
                cardActor.loadTexture(card.img)
                // y-coord is set to hide the bottom half, click to elevate?
                cardActor.centerAtPosition(350f, height - 950f)
                cardActor.setRotation(30f - 15f*handIndex)
                cardActor.moveBy(
                    (wid - (cardTotal * cardActor.width + (cardTotal - 1) * intervalWid)) / 2 + handIndex * (cardActor.width + intervalWid),
                    70f - abs(2-handIndex)*35f
                )
                if (card.isHealCard()) {
                    cardActor.setOnDropPlayerIntersect {
                        cardActor.remove()
                        board.removeCard(card)
                        board.notify(card)
                        borderImage.remove()
                    }
                    cardActor.setOnDragPlayerIntersect {
                        val borderWidth = 30
                        borderImage.setSize(
                            cardActor.width + borderWidth * 2,
                            cardActor.height + borderWidth * 2
                        )

                        borderImage.setPosition(
                            cardActor.x - borderWidth,
                            cardActor.y - borderWidth
                        )

//                        borderImage.setOrigin((cardActor.width + borderWidth * 2)/2, (cardActor.height + borderWidth * 2)/2)
//                        borderImage.setRotation(30f - 15f*handIndex)

                        stage.addActor(borderImage)
                        cardActor.toFront()
//                        cardActor.setRotation(0f)
                    }

                    cardActor.setOnDropEnemyIntersect {
                        cardActor.setPosition(cardActor.startX, cardActor.startY)
                        cardActor.setRotation(cardActor.startRotation)
                        borderImage.remove()
                    }
                    cardActor.setOnDragEnemyIntersect {
                        borderImage.remove()
                    }

                } else {
                    cardActor.setOnDropEnemyIntersect {
                        cardActor.remove()
                        board.removeCard(card)
                        board.notify(card)
                        borderImage.remove()
                    }
                    cardActor.setOnDragEnemyIntersect {
                        //println("CARD USING?")
                        val borderWidth = 30
                        borderImage.setSize(
                            cardActor.width + borderWidth * 2,
                            cardActor.height + borderWidth * 2
                        )

                        borderImage.setPosition(
                            cardActor.x - borderWidth,
                            cardActor.y - borderWidth
                        )

//                        borderImage.setOrigin((cardActor.width + borderWidth * 2)/2, (cardActor.height + borderWidth * 2)/2)
//                        borderImage.setRotation(30f - 15f*handIndex)

                        stage.addActor(borderImage)
                        cardActor.toFront()
                    }

                    cardActor.setOnClick {
                        cardActor.setRotation(0f)
                        println("clicking")
                        descriptionTable.setSize(
                            cardActor.width,
                            cardActor.height
                        )
                        descriptionTable.setPosition(cardActor.x, cardActor.y+cardActor.height)

                        stage.addActor(descriptionTable)
                    }

                    cardActor.setOnDrag {
                        descriptionTable.remove()
                    }

                    cardActor.setOnDrop {
                        descriptionTable.remove()
                    }

                    cardActor.setOnDropPlayerIntersect {
                        cardActor.setPosition(cardActor.startX, cardActor.startY)
                        cardActor.setRotation(cardActor.startRotation)
                        borderImage.remove()
                    }
                    cardActor.setOnDragPlayerIntersect {
                        borderImage.remove()
                    }
                }

                cardActor.setOnDropNoIntersect {
                    cardActor.setPosition(cardActor.startX, cardActor.startY)
                    cardActor.setRotation(cardActor.startRotation)
                    borderImage.remove()
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
                cardActor.centerAtPosition(350f, height - 950f)
                cardActor.setRotation(30f - 15f*handIndex)
                cardActor.moveBy(
                    (wid - (cardTotal * cardActor.width + (cardTotal - 1) * intervalWid)) / 2 + handIndex * (cardActor.width + intervalWid),
                    70f - abs(2-handIndex)*35f
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
                    board.endRound()
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
                board.startGame()
                board.enemy.initBars()
                board.player.initBars()

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

        if (board.win() != null) {
            stage.clear()
            val font = BitmapFont(Gdx.files.internal("Arial120Bold.fnt"))
            val buttonStyle = TextButtonStyle()
            buttonStyle.font = font
            val textButton = TextButton( if (board.win() == true) "You win!" else "You lose", buttonStyle)
            textButton.setPosition(wid/2 - textButton.width/2, height/2 - textButton.height/2)
            stage.addActor(textButton)
            textButton.addListener(object : InputListener() {
                override fun touchDown(
                    event: InputEvent?,
                    x: Float,
                    y: Float,
                    pointer: Int,
                    button: Int
                ): Boolean {
                    val deck1 = getTestDeck()
                    val deck2 = getTestDeck()
                    val playerStates : MutableList<State> = mutableListOf()
                    val player = Player("Hero",300, 10, 10, "badlogic.jpg", deck1, playerStates, PlayerType.Human)

                    val imgs = Array<String?>(arrayOf("skeleton1.png","skeleton2.png","skeleton3.png","skeleton2.png"))

                    val enemyStates : MutableList<State> = mutableListOf()
                    val enemy = Enemy(imgs,"Enemy",100, 99, 99, "badlogic.jpg", deck2, enemyStates, PlayerType.AI)

                    val newboard = Board(player, enemy)
                    Awake.setActiveScreen(EnterScreen(newboard))
                    return true
                }
            })
        }
    }
}