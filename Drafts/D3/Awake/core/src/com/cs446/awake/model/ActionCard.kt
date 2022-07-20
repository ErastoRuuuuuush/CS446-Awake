package com.cs446.awake.model


import com.badlogic.gdx.utils.Array
// cards that will be used in fights
class ActionCard (cardName: String, img: String, usage: String, val energyCost: Int, val strengthCost: Int, val healthChange: Int, val Effect: Array<State>) : Card(cardName, img, usage) {

    fun isHealCard() : Boolean {
        if (healthChange > 0) {
            return true
        }
        return false
    }
}
