package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Array
import com.cs446.awake.model.*
import com.cs446.awake.utils.*

// TODO: List
//   1. 点击卡牌，出现卡牌信息
//   2. 可以对自己使用卡牌
//   3. 图片/血条/信息栏 界面提升(纯图片，无交互)


// Core Screen of the battle, exit only when battle ends, will not save data if exit.
// TODO: Graphic:
//   * Background Picture:   dungeon.png -> battle.png
//   * State Pictures (3~5): "${state.lowercase()}.png"
//   * Card Border Pic: highlight_border.png
// Next Screen: DungeonScreen
//   * Go to: when battle ends
//   * Return: N/A (new BattleScreen will generate)
// Prev Screen: DungeonScreen
// Game logic:
//   1. EnterBattleScreen
//   2. BattleScreen
//   3. startTurn
//   4. endTurn
//   5. Loop from 3. If End Game, jump to 6.
//   6. End Game / Exit Screen
class BattleScreen(private val player: Player, private val enemy: Enemy) : BaseScreen(){
    //// Variable of position related
    // Size of the entire screen
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()
    // Spacing between state icons
    private val intervalWid = 10f

    //// Variable of data related
    // All States TODO: Should not be here, should be in some global data
    private val stateList = Array<String>(arrayOf("Burn", "Freeze", "Poison", "Paralysis", "Sleep"))

    //// Variable of display related
    private lateinit var enemyDisplay : BaseActor
    private lateinit var infoAITurn : Label
    private lateinit var infoPlayerTurn : Label
    private lateinit var finishPlayerRound : BaseActor // A button
    private val cardList = ArrayList<DragDropActor>() // Used for cleaning
    // Card's border
    private val borderTexture =
        Texture(Gdx.files.internal("highlight_border.png")) // TODO: change the texture
    private val borderImage = Image(borderTexture)

    //// Variable of game Core
    private var currentTurn : Character = player
    private var roundCount: Int = 0
    // Timer variables
    private var worldTimer  = -1
    private var activeTimer = false
    private val timerLimit = 10 // Not 0 in case of concurrency issue.
    private var endTimeFcn : () -> Unit = {} // lambda function about what to do when time ends
    private var duringTimeFcn : () -> Unit = {} // lambda function about what to do when each frame passed.


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

    // Let enemy (AI) draw cards.
    private fun enemyTurn() {
        stage.addActor(infoAITurn)

        // Enemy use one card
        val card = currentTurn.selectRamdomCard()
        val cardActor = BaseActor(0f, 0f, stage)
        cardActor.loadTexture(card.img)
        cardActor.centerAtPosition(screenWidth / 2, screenHeight)
        cardActor.moveBy(0f, -550f)

        // Same border image as the player's one
        val borderWidth = 30
        borderImage.setSize(
            cardActor.width + borderWidth * 2,
            cardActor.height + borderWidth * 2
        )
        borderImage.setPosition(cardActor.x - borderWidth, cardActor.y - borderWidth)
        stage.addActor(borderImage)

        // The following code will do
        // 1. display card for 1sec
        // 2. vanish the card in 0.5sec
        // 3. remove the card and finish AI turn

        // 1. display card for about 1sec
        val timeUp : () -> Unit = {
            // When time up, vanish card
            val duringTime : () -> Unit = {
                // 2. vanish the card in about 0.5sec
                val value : Float = worldTimer / 60f
                borderImage.color.a = value
                cardActor.setOpacity(value)
            }
            val endTime : () -> Unit = {
                // 3. remove the card and finish AI turn
                borderImage.remove()
                borderImage.color.a = 1f // Reset the alpha value
                cardActor.remove()
                // Apply the Card effect
                useCard(card)
                // End enemy Turn
                endTurn()
            }
            startTimer(40, endTime, duringTime)
        }
        startTimer(60, timeUp) {}
    }

    // Let player draw cards.
    private fun playerTurn() {
        stage.addActor(infoPlayerTurn)
        stage.addActor(finishPlayerRound)
    }

    // Function that check if player win or lose.
    // True -> win
    // False -> lose
    // null -> game continue
    private fun isPlayerWin(): Boolean? {
        if (player.isDead()) {
            println("\n You Lose！")
            return false
        }
        if (enemy.isDead()) {
            println("\n You Win！")
            return true
        }
        return null
    }

    // Function that apply the start part of round of game and active AI if it is AI's turn.
    private fun startTurn() {
        // Clean the round indicator
        infoAITurn.remove()
        infoPlayerTurn.remove()
        // PreRound: Restore energy and apply state effect
        player.preRound()
        enemy.preRound()
        // Give card to player for player's turn
        if (currentTurn == player) {
            renderCard()
            roundCount++
        }
        // Let character use card now.
        if (currentTurn == player) {
            playerTurn()
        } else {
            enemyTurn()
        }
    }

    // Function that apply event of using cards.
    private fun useCard(card: ActionCard) {
        // Remove the used card
        currentTurn.removeCard(card)
        // Notify everyone to apply effect of card
        player.update(card, from = currentTurn)
        enemy.update(card, from = currentTurn)
        // Check game status
        if (isPlayerWin() != null) {
            // Game end
            if (isPlayerWin() == true) {
                winGame()
            } else {
                loseGame()
            }
        }
    }

    // Function that apply the end part of round of game.
    private fun endTurn() {
        // PostRound: Check if any state time is expired, remove state
        player.postRound()
        enemy.postRound()
        // Check if game ends
        if (isPlayerWin() != null) {
            // Game end
            if (isPlayerWin() == true) {
                winGame()
            } else {
                loseGame()
            }
        } else {
            // Game continue
            // Switch turn
            currentTurn = if (currentTurn == player) enemy else player
            // continue to next round of game
            startTurn()
        }
    }

    // The game result with player wins.
    private fun winGame() {
        val winLabel = Label("You Win!", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        winLabel.setPosition(screenWidth/2 - winLabel.width/2, screenHeight/2 - winLabel.height/2)
        stage.addActor(winLabel)
        // TODO: Display enemy die animation or pause enemy animation
        // Let enemy vanish
        val duringTime : () -> Unit = { enemyDisplay.setOpacity(worldTimer / 60f) }
        val endTime : () -> Unit = {
            enemyDisplay.remove()
            // TODO: Exit back to Dungeon
        }
        startTimer(60, endTime, duringTime) // about 1 second
    }

    // The game result with player lose.
    private fun loseGame() {
        // TODO: Exit back to Village.
        val winLabel = Label("You Lose!", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        winLabel.setPosition(screenWidth/2 - winLabel.width/2, screenHeight/2 - winLabel.height/2)
    }

    // Function that render player's card on the screen.
    // Call at the beginning of the player's turn.
    // TODO: The Card should only be movable at player's turn. (currentTurn == Player)
    // TODO: The Card can apply to enemy and player itself. (need a Actor area to target to player)
    private fun renderCard() {
        cardList.clear() // Clean all card displayed

        // Card Actor
        // TODO: Click and show card info
        val cardTotal = player.hand.size - 1
        for ((handIndex, card) in player.hand.withIndex()) {
            // TODO: Change target enemy to player for heal card, need an area to drop for player
            val cardActor = DragDropActor(0f, 0f, stage, enemyDisplay)
            cardActor.toFront()
            cardActor.loadTexture(card.img)
            cardActor.centerAtPosition(0f, screenHeight - 950f)
            cardActor.moveBy(
                (screenWidth - (cardTotal * cardActor.width + (cardTotal - 1) * intervalWid)) / 2 + handIndex * (cardActor.width + intervalWid),
                0f
            )
            // Event when card drag hit enemy TODO: add when hit player itself
            cardActor.setOnDropIntersect {
                println("intersected when dropped")
                // TODO: check later on when the turn is not player, should not able to move.
                //   if (board.checkTurn(board.player)) {
                cardActor.remove()
                useCard(card)
                borderImage.remove()
            }
            // Event when card drag not hit enemy, go back to original position
            cardActor.setOnDropNoIntersect {
                cardActor.setPosition(cardActor.startX, cardActor.startY)
                borderImage.remove()
            }
            // Event when card is being dragged
            cardActor.setOnDragIntersect {
                val borderWidth = 30
                borderImage.setSize(
                    cardActor.width + borderWidth * 2,
                    cardActor.height + borderWidth * 2
                )
                borderImage.setPosition(cardActor.x - borderWidth, cardActor.y - borderWidth)
                stage.addActor(borderImage)
                cardActor.toFront()
            }
            // Event when card is released from drag
            cardActor.setOnDragNoIntersect {
                borderImage.remove()
            }
            // Add current card to the cardList so that can be cleaned each round.
            // cardList.add(cardActor)
        }
    }

    // Function that initialize Battle View:
    //   * background
    //   * enemy display
    //   * enemy health bar, player health bar, player energy bar
    //   * state of player, state of enemy
    //   * [not show] game turn indicator label (x2)
    //   * [not show] player end turn button
    // Property change:
    //   * State - opacity: by Character
    //   * Health - Bar: by Character
    //   * Energy - Bar: by Player
    private fun battleScreen() {
        stage.clear() // Clean BattleEnter View

        // Background Picture
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dungeon.png")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        // Enemy Animation (or picture)
        // TODO: 素材: 敌人: 动画素材 or 图片素材
        enemyDisplay = BaseActor(0f, 0f, stage)
        // TODO: if Animation
        enemyDisplay.loadAnimationFromFiles(enemy.images, 0.5f, true)
        // TODO: if picture
        // enemy.loadTexture("skeleton1.png")
        // TODO: remove this line when above done.
        enemyDisplay.centerAtPosition(screenWidth / 2, screenHeight)
        enemyDisplay.moveBy(0f, -550f)

        // Bars
        // TODO: NOTICE: strength bar will not be used in final game design, need remove.
        stage.addActor(enemy.healthBar)
        stage.addActor(player.healthBar)
        stage.addActor(player.energyBar)

        // State
        for ((stateIndex, state) in stateList.withIndex()) {
            val stateImg = Texture("${state.lowercase()}.png")
            val stateWidth = stateImg.width.toFloat()

            // State of player
            val playerStateActor = BaseActor(0f, 0f, stage)
            playerStateActor.loadTexture("${state.lowercase()}.png")
            playerStateActor.centerAtPosition(-900f, screenHeight - 1000f)
            playerStateActor.moveBy(
                (screenWidth - (4 * stateWidth + 3 * intervalWid)) / 2 + stateIndex * stateWidth,
                0f
            )
            playerStateActor.setOpacity(0.3f)

            // State of enemy
            val enemyStateActor = BaseActor(0f, 0f, stage)
            enemyStateActor.loadTexture("${state.lowercase()}.png")
            enemyStateActor.centerAtPosition(800f, screenHeight - 100f)
            enemyStateActor.moveBy(
                (screenWidth - (4 * stateWidth + (4 - 1) * intervalWid)) / 2 + stateIndex * stateWidth,
                0f
            )
            enemyStateActor.setOpacity(0.3f)

            // Future property change by Character class updateState(...)
            player.characterStateMap[state] = playerStateActor
            enemy.characterStateMap[state] = enemyStateActor
        }

        // TODO: Better display (using pic or other text font)
        // AI-Round Indicator
        // * Not yet added to stage, only added when AI turn starts
        infoAITurn = Label("AI-Round", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        infoAITurn.y = screenHeight / 2 + infoAITurn.height / 2

        // Player-Round Indicator
        // * Not yet added to stage, only added when Player turn starts
        infoPlayerTurn = Label("Your-Round", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        infoPlayerTurn.y = screenHeight / 2 + infoPlayerTurn.height / 2

        // Finish-Player-Round button
        // * Not yet added to stage, only added when Player turn starts
        finishPlayerRound = BaseActor(0f, 0f, stage)
        finishPlayerRound.loadTexture("EndTurnButton.png")
        finishPlayerRound.centerAtPosition(screenWidth - 250f, 150f)
        finishPlayerRound.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                finishPlayerRound.remove()
                endTurn()
                return true
            }
        })
        finishPlayerRound.remove() // Remove from display stage
    }

    // Function that initialize Battle Start View:
    //   * background
    //   * text info (touch to start)
    private fun battleEnterScreen() {
        // Background Picture
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dragon.jpeg")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth/2, screenHeight/2)
        background.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                enemy.initBars()
                player.initBars()
                battleScreen()
                startTurn()
                return true
            }
        })

        // Text Info
        val start = BaseActor(0f, 0f, stage)
        start.loadTexture("start-message.png")
        start.centerAtPosition(screenWidth/2, screenHeight)
        start.moveBy(0f, -800f)
    }

    override fun initialize() {
        Gdx.input.inputProcessor = stage
        battleEnterScreen()
    }

    // Currently called at 60fps speed
    override fun update(delta: Float) {
        runTimer()
    }
}

