package com.cs446.awake.model


// TODO - Character Methods:
// reset() reset everything (MAY not really needed)
// preRound() check its state and apply if needed
// drawCard() get character cards
// useCard() wait user input for card used, return a Card Class
// applyCard(cardUsed: Card) receive card and apply to this character

class Board (val player: Player,val enemy: Enemy) {
    private var turn: Character = player
    private var target: Character = enemy
    private var currentRound = 0

    init {
//        startGame()
    }

    fun startGame() {
        turn.reset()
        target.reset()
        print("game started")
        while (!finished()) {
            // Separate function for easy maintenance and upgrade add-ons in future.
            currentRound++
            preRound()
            startRound()
            endRound()
            postRound()
            switchTurn()
            println("Round $currentRound")
        }
    }

    private fun finished(): Boolean {
        if (player.isDead() || enemy.isDead()) {
            return true
        }
        return false
    }

    private fun preRound() {
        turn.preRound()
        target.preRound()
    }

    private fun startRound() {
        val Card = turn.selectHandCard() ?: return

        // Option 1 - Notify one
        // target.useCard(Card, from = turn)
        // Option 2 - Notify everyone
        target.update(card, from = turn)
        turn.update(card, from = turn)
    }

    private fun endRound(){}

    private fun postRound() {
        turn.postRound()
        target.postRound()
    }

    private fun switchTurn() {
        val temp = turn
        turn = target
        target = temp
    }

    fun removeEnemy(target: Enemy) {}
}