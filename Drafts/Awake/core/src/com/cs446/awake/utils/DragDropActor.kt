package com.cs446.awake.utils

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class DragDropActor(x: Float, y: Float, s: Stage, dropTarget: AbstractActor, inTable: Boolean? = false) : AbstractActor(x, y, s, inTable = inTable){

    val self: DragDropActor = this

    private var onDragIntersect : () -> Unit = {}
    private var onDragNoIntersect : () -> Unit = {}
    private var onDropIntersect : () -> Unit = {}
    private var onDropNoIntersect : () -> Unit = {}
    private var onClick : () -> Unit = {}
    private var onDrag : () -> Unit = {}
    private var onDrop : () -> Unit = {}

    var grabOffsetX: Float = 0f
    var grabOffsetY: Float = 0f

    var startX: Float = 0f
    var startY: Float = 0f

    var startRotation = 0f

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
                self.startRotation = self.rotation

                self.onClick()

                self.grabOffsetX = x
                self.grabOffsetY = y
                self.toFront()
                self.onDragStart()
                self.addAction(Actions.scaleTo(1.1f,1.1f,0.25f))
                return true
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                self.onDrag()

                val deltaX = x - self.grabOffsetX
                val deltaY = y - self.grabOffsetY

                self.moveBy(deltaX, deltaY)
                if (self.isIntersect(dropTarget)) {
                    self.onDragIntersect()
                } else {
                    self.onDragNoIntersect()
                }
            }

            override fun touchUp(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ) {
                self.onDrop()

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

    fun setOnClick(clickFunc: () -> Unit) {
        onClick = clickFunc
    }

    fun setOnDrag(dragFunc: () -> Unit) {
        onDrag = dragFunc
    }

    fun setOnDrop(dropFunc: () -> Unit) {
        onDrop = dropFunc
    }

    fun setOnDragIntersect(dragIntersectFunc: () -> Unit) {
        onDragIntersect = dragIntersectFunc
    }

    fun setOnDragNoIntersect(dragIntersectFunc: () -> Unit) {
        onDragNoIntersect = dragIntersectFunc
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