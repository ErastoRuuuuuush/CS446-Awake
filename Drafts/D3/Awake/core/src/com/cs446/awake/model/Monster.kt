package com.cs446.awake.model

import com.badlogic.gdx.utils.Array

// Todo: generate deck dynamically according to input field of monster
class Monster (val images: Array<String?>, val level: Int, val charName: String){
    fun getDeck (): Deck{
        return getTestDeck()
    }
    fun getHP (): Int{
        return 100
    }
    fun getEnergy(): Int{
        return 15
    }
    fun getStrength(): Int{
        return 15
    }
}