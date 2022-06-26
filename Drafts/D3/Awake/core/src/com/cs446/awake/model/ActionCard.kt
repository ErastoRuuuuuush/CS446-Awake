package com.cs446.awake.model


import com.badlogic.gdx.utils.Array
// cards that will be used in fights
class ActionCard (cardName: String, img: String, usage: String, val energyCost: Int, val strengthCost: Int, val healthChange: Int, Effect: Array<State>) : Card(cardName, img, usage) {
}
