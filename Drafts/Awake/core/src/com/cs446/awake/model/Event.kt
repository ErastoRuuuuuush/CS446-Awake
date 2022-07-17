package com.cs446.awake.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array


open class Event(val backImg: String, val frontImg: String) {
    private var flipped: Boolean = false
    // return false if not activated
    fun isFlipped(): Boolean {
        return flipped
    }
    open fun trigger() : Int {
        flipped = true
        return EMPTY
    }
}