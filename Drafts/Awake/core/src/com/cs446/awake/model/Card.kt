package com.cs446.awake.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.google.gson.Gson


open class Card(val cardName: String, val img: String, var usage: String = "", var count: Int = 1) {

    open fun use() {}

    // create a clone (deep copy) of the dat
    open fun clone(): Card{
        val string = Gson().toJson(this, Card::class.java)
        return Gson().fromJson<MergableCard>(string, Card::class.java)
    }
}