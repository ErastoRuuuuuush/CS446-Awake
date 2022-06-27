package com.cs446.awake.utils

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class DragDropActor(x: Float, y: Float, s: Stage, dropTarget: AbstractActor) : AbstractActor(x, y, s){
    val self: DragDropActor = this

    private var onDragIntersect : () -> Unit = {}
    private var onDropIntersect : () -> Unit = {}
    private var onDropNoIntersect : () -> Unit = {}

    var grabOffsetX: Float = 0f
    var grabOffsetY: Float = 0f

    var startX: Float = 0f
    var startY: Float = 0f

    init {
        addListener (object: InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                self.startX = self.x
                self.startY = self.y

                self.grabOffsetX = x
                self.grabOffsetY = y
                self.toFront()
                self.onDragStart()
                self.addAction(Actions.scaleTo(1.1f,1.1f,0.25f))
                return true
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                val deltaX = x - self.grabOffsetX
                val deltaY = y - self.grabOffsetY

                self.moveBy(deltaX, deltaY)
            }

            override fun touchUp(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ) {
                if (self.isIntersect(dropTarget)) {
                    self.onDropIntersect()
                } else {
                    self.onDropNoIntersect()
                }
                self.addAction(Actions.scaleTo(1.00f,1.00f,0.25f))
            }


        } )
    }

    fun isIntersect(other: AbstractActor): Boolean {
        val bounding1: Rectangle = this.bound
        val bounding2: Rectangle = other.bound
        if (bounding1 == null || bounding2 == null) return false
        return bounding1.overlaps(bounding2)
    }

    fun setOnDragIntersect(dragIntersectFunc: () -> Unit) {
        onDragIntersect = dragIntersectFunc
    }

    fun setOnDropIntersect(dropIntersectFunc: () -> Unit) {
        onDropIntersect = dropIntersectFunc
    }

    fun setOnDropNoIntersect(dropNoIntersectFunc: () -> Unit) {
        onDropNoIntersect = dropNoIntersectFunc
    }

    fun onDragStart() {

    }

    override fun act(dt: Float) {
        super.act(dt)
    }
}