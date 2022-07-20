package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.particles.ParticleSorter
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Null
import com.cs446.awake.Awake
import com.cs446.awake.Awake.Companion.setActiveScreen
import com.cs446.awake.model.*
import com.cs446.awake.utils.*
import java.awt.Rectangle
import kotlin.math.abs

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
    private var cardHeight = Texture("Heal.png").height.toFloat()

    private lateinit var enemyDisplay : BaseActor
    private lateinit var infoAITurn : Label
    private lateinit var infoPlayerTurn : Label
    private lateinit var aITurn : BaseActor
    private lateinit var playerTurn : BaseActor
    private lateinit var aICursor : BaseActor
    private lateinit var playerCursor : BaseActor
    private lateinit var finishPlayerRound : BaseActor // A button
    private val cardList = ArrayList<DragDropActor>() // Used for cleaning

    private lateinit var enemyAttackActor : BaseActor
    private lateinit var playerImageActor : BaseActor
    private lateinit var enemyImageActor: BaseActor

    // Card's border
    private val borderTexture =
        Texture(Gdx.files.internal("highlight_border.png")) // TODO: change the texture
    private val borderImage = Image(borderTexture)

    // description
    private val descriptionTexture =
        Texture(Gdx.files.internal("paperboarder.png"))
    private val descriptionImage = Image(descriptionTexture)
    private val descriptionTable = Table()
    private val descriptionWidth = descriptionImage.width / 2
    private val descriptionHeight = descriptionImage.height / 4

    private val descriptionFont = BitmapFont(Gdx.files.internal("font/font1.fnt"))


    //// Variable of game Core
    private var currentTurn : Character = player
    private var roundCount: Int = 0
    // Timer variables
    private var worldTimer  = -1
    private var activeTimer = false
    private val timerLimit = 5 // Not 0 in case of concurrency issue.
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

    // Notify that character froze and call the endTurn.
    private fun frozeNotify(who: String) {
        // Character is froze due to the negative state applied, cannot use any card.
        val frozeNotification = Label("$who froze due to negative state!", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        frozeNotification.setPosition(screenWidth/2 - frozeNotification.width/2, screenHeight/2 - frozeNotification.height/2)
        stage.addActor(frozeNotification)
        // Let notification vanish after 1 sec of display
        val endTime: () -> Unit = {
            val duringTime : () -> Unit = { frozeNotification.color.a = (worldTimer / 30f) } // alpha value is the opacity value
            val endTime : () -> Unit = {
                frozeNotification.remove()
                endTurn() // End enemy's turn
            }
            startTimer(30, endTime, duringTime)
        }
        startTimer(50, endTime, {}) // about 1 second
    }

    // Let enemy (AI) draw cards.
    private fun enemyTurn() {
        stage.addActor(aITurn)
        stage.addActor(aICursor)
        enemyImageActor.toFront()
        enemy.healthBar.toFront()

        if (enemy.canUseCard) {
            // Enemy use one card
            val card = currentTurn.selectRamdomCard()
            val cardActor = BaseActor(0f, 0f, stage)
            cardActor.loadTexture(card.img)
            cardActor.centerAtPosition(screenWidth / 2, screenHeight)
            cardActor.moveBy(0f, -550f)

            // Same border image as the player's one
            borderImage.setSize(
                cardActor.width + 20f,
                cardActor.height + 20f
            )
            borderImage.setPosition(cardActor.x + 10f, cardActor.y + 5f)
            stage.addActor(borderImage)
            cardActor.toFront()

            // The following code will do
            // 1. display card for 1sec
            // 2. vanish the card in 0.5sec
            // 3. remove the card and finish AI turn

            // 1. display card for about 1sec
            val timeUp: () -> Unit = {
                // When time up, vanish card
                val duringTime: () -> Unit = {
                    // 2. vanish the card in about 0.5sec
                    val value: Float = worldTimer / 60f
                    borderImage.color.a = value
                    cardActor.setOpacity(value)
                }
                val endTime: () -> Unit = {
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
        } else {
            frozeNotify("enemy") // This will call endTurn automatically
        }
    }

    // Let player draw cards.
    private fun playerTurn() {
        stage.addActor(playerTurn)
        stage.addActor(playerCursor)
        playerImageActor.toFront()
        player.healthBar.toFront()
        player.energyBar.toFront()

        if (player.canUseCard) {
            stage.addActor(finishPlayerRound)
        } else {
            frozeNotify("You")// This will call endTurn automatically
        }
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

    private fun noCardNofity(who: String) {
        val noCard = Label("$who no Cards!", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        noCard.setPosition(screenWidth/2 - noCard.width/2, screenHeight/2 + noCard.height)
        stage.addActor(noCard)
    }

    // Function that apply the start part of round of game and active AI if it is AI's turn.
    private fun startTurn() {
        // Clean the round indicator
        infoAITurn.remove()
        infoPlayerTurn.remove()
        aITurn.remove()
        playerTurn.remove()
        aICursor.remove()
        playerCursor.remove()
        // PreRound: Restore energy and apply state effect
        if (currentTurn == player) {
            if (!player.preRound()) {
                // player does not have any cards
                noCardNofity("Player")
                loseGame()
                return
            }
        } else {
            if (!enemy.preRound()) {
                // enemy does not have any cards
                noCardNofity("Enemy")
                winGame()
                return
            }
        }

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
    private fun useCard(card: ActionCard) : Boolean {

        // Notify everyone to apply effect of card
        if (!player.update(card, from = currentTurn)) {
            return false
        }
        enemy.update(card, from = currentTurn)

        // Remove the used card
        currentTurn.removeCard(card)
        // Check game status
        if (isPlayerWin() != null) {
            // Game end
            if (isPlayerWin() == true) {
                winGame()
            } else {
                loseGame()
            }
        }
        return true
    }

    // Function that apply the end part of round of game.
    private fun endTurn() {
        // PostRound: Check if any state time is expired, remove state
        if (currentTurn == player) {
            player.postRound()
        } else {
            enemy.postRound()
        }
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
        succeed() // call global data to store succeed battle
        // Clean the round indicator
        infoAITurn.remove()
        infoPlayerTurn.remove()
        aITurn.remove()
        playerTurn.remove()
        aICursor.remove()
        playerCursor.remove()

        val winLabel = Label("You Win!", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        winLabel.setPosition(screenWidth/2 - winLabel.width/2, screenHeight/2 - winLabel.height/2)
        stage.addActor(winLabel)
        // TODO: Display enemy die animation or pause enemy animation
        // Let enemy vanish
        val duringTime : () -> Unit = { enemyDisplay.setOpacity(worldTimer / 60f) }
        val endTime : () -> Unit = {
            if (activeBoss) {
                // Beat the boss
                enemyDisplay.remove()
                storage.append(backPackMaterial)
                storage.append(backPackItem)
                storage.append(battleItem)
                backPackItem = ItemCardData(mutableListOf())
                backPackMaterial = MaterialCardData(mutableListOf())
                battleItem = ItemCardData(mutableListOf())
                dungeonLevel = 1
                setActiveScreen(VillageScreen())
                dumpJson()
            } else {
                enemyDisplay.remove()
                backPackItem.append(battleItem)
                battleItem = ItemCardData(mutableListOf()) // clear battle item
                if (dungeonMap != null) {
                    setActiveScreen(DungeonScreen(dungeonMap as DungeonMap))
                }
                dumpJson()
            }

        }
        startTimer(60, endTime, duringTime) // about 1 second
    }

    // The game result with player lose.
    private fun loseGame() {
        reset() // loose everything
        // Clean the round indicator
        infoAITurn.remove()
        infoPlayerTurn.remove()
        aITurn.remove()
        playerTurn.remove()
        aICursor.remove()
        playerCursor.remove()

        // TODO: Exit back to Village.
        val looseLabel = Label("You Lose!", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        looseLabel.setPosition(screenWidth/2 - looseLabel.width/2, screenHeight/2 - looseLabel.height/2)
        stage.addActor(looseLabel)

        val back = Label(">> Back to Village <<", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        back.setPosition(screenWidth/2 - back.width/2, screenHeight/2 - back.height)
        stage.addActor(back)

        back.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                // Clear eveything one has.
                backPackItem = ItemCardData(mutableListOf())
                backPackMaterial = MaterialCardData(mutableListOf())
                battleItem = ItemCardData(mutableListOf())
                setActiveScreen(VillageScreen())
                dungeonLevel = 1
                return true
            }
        })

    }

    // Function that render player's card on the screen.
    // Call at the beginning of the player's turn.
    // TODO: The Card should only be movable at player's turn. (currentTurn == Player)
    // TODO: The Card can apply to enemy and player itself. (need a Actor area to target to player)
    private fun renderCard() {
        for (card in cardList) {
            card.remove()
        }
        cardList.clear()

        // Card Actor
        // TODO: Click and show card info
        val cardTotal = player.hand.size - 1
        for ((handIndex, card) in player.hand.withIndex()) {
            // TODO: Change target enemy to player for heal card, need an area to drop for player
            lateinit var cardActor : DragDropActor
            if (card.isHealCard()) {
                cardActor = DragDropActor(0f, 0f, stage, playerImageActor)
            } else {
                cardActor = DragDropActor(0f, 0f, stage, enemyAttackActor)
            }

            cardActor.loadTexture(card.img)
            // y-coord is set to hide the bottom half, click to elevate?
            // cardActor.setSize(cardHeight / cardActor.height * cardActor.width, cardHeight)
            cardActor.setSize(cardHeight / cardActor.height * cardActor.width, cardHeight)
            cardActor.centerAtPosition(350f, cardHeight / 2 - 40f)
            // cardActor.rotation = 30f - 15f*handIndex
            // cardActor.rotateBy(30f - 15f*handIndex)

            cardActor.moveBy(
                (screenWidth - (cardTotal * cardActor.width + (cardTotal - 1) * intervalWid)) / 2 + handIndex * (cardActor.width + intervalWid),
                // 70f + (2-handIndex) *35f
            0f
            )

            cardActor.setOnDropIntersect {
                if (useCard(card)) {
                    cardActor.remove()
                    borderImage.remove()
                } else {
                    cardActor.setPosition(cardActor.startX, cardActor.startY)
                    cardActor.setRotation(cardActor.startRotation)
                    borderImage.remove()
                }

            }
            cardActor.setOnDragIntersect {
                borderImage.setSize(
                    cardActor.width + 45f,
                    cardActor.height + 55f
                )

                borderImage.setPosition(
                    cardActor.x - 36f,
                    cardActor.y - 55f
                )

                stage.addActor(borderImage)
                cardActor.toFront()
            }

            cardActor.setOnClick {
                cardActor.setRotation(0f)
                descriptionTable.setSize(
                    descriptionWidth,
                    descriptionHeight
                )

                var descriptionLabel = Label(card.usage, Label.LabelStyle(descriptionFont, Color.WHITE))
                descriptionLabel.setWidth(1000f)
                descriptionLabel.setHeight(500f)
                descriptionLabel.wrap = true

                descriptionTable.add(descriptionLabel).width(400f)
                descriptionTable.setPosition(cardActor.x - descriptionWidth / 4, cardActor.y+cardActor.height + 20f)

                stage.addActor(descriptionTable)
            }

            cardActor.setOnDrag {
                descriptionTable.remove()
                descriptionTable.reset()
            }

            cardActor.setOnDrop {
                descriptionTable.remove()
                descriptionTable.reset()
            }

            cardActor.setOnDropNoIntersect {
                cardActor.setPosition(cardActor.startX, cardActor.startY)
                cardActor.setRotation(cardActor.startRotation)
                borderImage.remove()
            }
            cardActor.setOnDragNoIntersect {
                borderImage.remove()
            }
            // Add to card list so can clean next round
            cardList.add(cardActor)
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
        background.loadTexture("battle$dungeonLevel.png")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        // Enemy Animation (or picture)
        // TODO: 素材: 敌人: 动画素材 or 图片素材
        enemyDisplay = BaseActor(0f, 0f, stage)
        // TODO: if Animation
        enemyDisplay.loadAnimationFromFiles(enemy.images, 0.5f, true)
        // TODO: if picture
        // enemy.loadTexture("skeleton1.png")
        val enemyHeight = screenHeight
        enemyDisplay.setSize(enemyHeight / enemyDisplay.height * enemyDisplay.width, enemyHeight )
        enemyDisplay.centerAtPosition(screenWidth / 2, screenHeight / 2 + 75f)

        // TODO: remove this line when above done.
        // enemyDisplay.centerAtPosition(screenWidth / 2, screenHeight)
        // enemyDisplay.moveBy(0f, -550f)

        enemyAttackActor = BaseActor(0f, 0f, stage)
        enemyAttackActor.loadTexture("transparent.png")
        // enemyAttackActor.loadTexture("card_empty.png")
        // enemyAttackActor.setOpacity(0.3f)
        enemyAttackActor.setSize(enemyAttackActor.width*3f, enemyAttackActor.height*1.5f)
        enemyAttackActor.centerAtPosition(screenWidth/2, screenHeight)
        enemyAttackActor.moveBy(0f, -300f)

        // state background
        val playerStateBg = BaseActor(0f, 0f, stage)
        playerStateBg.loadTexture("state_background.png")
        playerStateBg.setSize(500f, 100f)
        playerStateBg.centerAtPosition(510f, screenHeight-800f)

        val enemyStateBg = BaseActor(0f, 0f, stage)
        enemyStateBg.loadTexture("state_background.png")
        enemyStateBg.setSize(500f, 100f)
        enemyStateBg.centerAtPosition(1850f, screenHeight-140f)

        // enemy actor
        enemyImageActor = BaseActor(0f, 0f, stage)
        enemyImageActor.loadTexture(enemy.enemyImage)
        enemyImageActor.setSize(enemyImageActor.width/3*2, enemyImageActor.height/5*3)
        enemyImageActor.centerAtPosition(screenWidth - 350f, screenHeight - 150f)

        // player actor

        playerImageActor = BaseActor(0f, 0f, stage)
        playerImageActor.loadTexture(player.playerImage)
        playerImageActor.setSize(playerImageActor.width/5*3, playerImageActor.height/5*3)
        playerImageActor.centerAtPosition(380f, 150f)

        // Bars
        stage.addActor(enemy.healthBar)
//        stage.addActor(enemy.energyBar)
        stage.addActor(player.healthBar)
        stage.addActor(player.energyBar)

        // Description
        descriptionTable.setBackground(TextureRegionDrawable(TextureRegion(descriptionTexture)))
        descriptionFont.getData().setScale(0.8f)

        // State
        for ((stateIndex, state) in stateList.withIndex()) {
            val stateImg = Texture("${state.lowercase()}.png")
            val stateWidth = stateImg.width.toFloat()

            // State of player
            val playerStateActor = BaseActor(0f, 0f, stage)
            playerStateActor.loadTexture("${state.lowercase()}.png")
            playerStateActor.centerAtPosition(-648f, screenHeight - 800f)
            playerStateActor.moveBy(
                (screenWidth - (4 * stateWidth + 3 * intervalWid)) / 2 + stateIndex * stateWidth,
                0f
            )
            playerStateActor.setOpacity(0.3f)

            // State of enemy
            val enemyStateActor = BaseActor(0f, 0f, stage)
            enemyStateActor.loadTexture("${state.lowercase()}.png")
            enemyStateActor.centerAtPosition(690f, screenHeight - 140f)
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

        aITurn = BaseActor(0f, 0f, stage, inTable = true)
        aITurn.loadTexture("enemy_bar_highlight.png")
        aITurn.setSize(enemyImageActor.width + 150f, enemyImageActor.height+ 70f)
        aITurn.centerAtActor(enemyImageActor)

        aICursor = BaseActor(0f,0f, stage, inTable = true)
        val list1 = Array<String?>(arrayOf("cursor_enemy.png", "cursor_enemy2.png"))
        aICursor.loadAnimationFromFiles(list1, 0.5f, true)
        // aICursor.loadTexture("cursor_enemy.png")
        val aICursorHeight = 160f
        aICursor.setSize(aICursorHeight / aICursor.height * aICursor.width, aICursorHeight)
        aICursor.setPosition(enemyImageActor.x + 475f, enemyImageActor.y - 160f)
        // Player-Round Indicator
        // * Not yet added to stage, only added when Player turn starts
        infoPlayerTurn = Label("Your-Round", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        infoPlayerTurn.y = screenHeight / 2 + infoPlayerTurn.height / 2
        playerTurn = BaseActor(0f, 0f, stage, inTable = true)
        playerTurn.loadTexture("hero_bar_highlight.png")
        playerTurn.setSize(playerImageActor.width + 35f, playerImageActor.height + 5f)
        playerTurn.centerAtActor(playerImageActor)
        playerTurn.moveBy(0f, 3f)

        playerCursor = BaseActor(0f,0f, stage, inTable = true)
        val list2 = Array<String?>(arrayOf("cursor_player.png", "cursor_player2.png"))
        playerCursor.loadAnimationFromFiles(list2, 0.5f, true)
        // playerCursor.loadTexture("cursor_player.png")
        val playerCursorHeight = 160f
        playerCursor.setSize(playerCursorHeight / playerCursor.height * playerCursor.width, playerCursorHeight)
        playerCursor.setPosition(playerImageActor.x + 45f, playerImageActor.y + playerCursor.height + 145f)

        // Finish-Player-Round button
        // * Not yet added to stage, only added when Player turn starts
        finishPlayerRound = BaseActor(0f, 0f, stage)
        finishPlayerRound.loadTexture("EndTurnButton.png")
        finishPlayerRound.setSize(finishPlayerRound.width / 2, finishPlayerRound.height / 2)
        finishPlayerRound.centerAtPosition(screenWidth - 250f, 400f)
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

