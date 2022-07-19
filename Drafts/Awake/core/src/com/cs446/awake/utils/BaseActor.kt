package com.cs446.awake.utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.scenes.scene2d.Stage

class BaseActor(x: Float, y: Float, s: Stage, texture: Texture? = null, inTable: Boolean? = false) : AbstractActor(x,y,s,texture, inTable)