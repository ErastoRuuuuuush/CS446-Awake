package com.cs446.awake.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.*
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array


abstract class AbstractActor(x: Float, y: Float, s: Stage, texture: Texture? = null
) : Actor() {
    var bound: Rectangle = Rectangle()
    private var animation: Animation<TextureRegion>? = null
    private var elapsedTime = 0f
    private var animationPaused = false
    private val velocityVec: Vector2 = Vector2(0f,0f)
    private val accelerationVec: Vector2 = Vector2(0f,0f)
    private var acceleration = 0f
    private var maxSpeed = 1000f
    private var deceleration = 0f

    init {
        setPosition(x, y)
        s.addActor(this)
        bound = Rectangle(x,y, width, height)
    }

    fun setAnimation(anim: Animation<TextureRegion>) {
        animation = anim
        val tr = animation?.getKeyFrame(0f)!!
        val w = tr.regionWidth.toFloat()
        val h = tr.regionHeight.toFloat()
        setSize(w, h)
        setOrigin(w / 2, h / 2)
    }

    fun setAnimationPaused(pause: Boolean) {
        animationPaused = pause
    }

    override fun act(dt: Float) {
        super.act(dt)
        bound = Rectangle(x,y, width, height)
        if (!animationPaused) elapsedTime += dt
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        val c: Color = color
        batch.setColor(c.r, c.g, c.b, c.a)
        if (animation != null && isVisible) batch.draw(
            animation!!.getKeyFrame(elapsedTime),
            x, y, originX, originY,
            width, height, scaleX, scaleY, rotation
        )
/*
            batch.draw(
                texture,
                ((Gdx.graphics.width - (texture?.width ?: 0)) / 2).toFloat(),
                ((Gdx.graphics.height - (texture?.height
                    ?: 0)) / 2).toFloat()
            )
 */

    }

    fun loadAnimationFromFiles(
        fileNames: Array<String?>,
        frameDuration: Float,
        loop: Boolean
    ): Animation<TextureRegion>? {
        val fileCount = fileNames.size
        val textureArray = Array<TextureRegion>()
        for (n in 0 until fileCount) {
            val fileName = fileNames[n]
            val texture = Texture(Gdx.files.internal(fileName))
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)
            textureArray.add(TextureRegion(texture))
        }
        val anim: Animation<TextureRegion> = Animation<TextureRegion>(frameDuration, textureArray)
        if (loop) anim.setPlayMode(Animation.PlayMode.LOOP) else anim.setPlayMode(Animation.PlayMode.NORMAL)
        // if (animation == null) setAnimation(anim)  // ERIC: removed this so can change pic during runtime.
        setAnimation(anim)
        return anim
    }

    fun loadAnimationFromSheet(
        fileName: String?, rows: Int, cols: Int,
        frameDuration: Float, loop: Boolean
    ): Animation<TextureRegion>? {
        val texture = Texture(Gdx.files.internal(fileName), true)
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)
        val frameWidth = texture.width / cols
        val frameHeight = texture.height / rows
        val temp = TextureRegion.split(texture, frameWidth, frameHeight)
        val textureArray = Array<TextureRegion>()
        for (r in 0 until rows) for (c in 0 until cols) textureArray.add(temp[r][c])
        val anim = Animation(
            frameDuration,
            textureArray
        )
        if (loop) anim.setPlayMode(Animation.PlayMode.LOOP) else anim.setPlayMode(Animation.PlayMode.NORMAL)
        if (animation == null) setAnimation(anim)
        return anim
    }

    fun loadTexture(fileName: String?): Animation<TextureRegion>? {
        val fileNames = Array<String?>(1)
        if (fileNames.isEmpty) {
            fileNames.add(fileName)
        } else {
            fileNames[0] = fileName
        }
        return loadAnimationFromFiles(fileNames, 1f, true)
    }

    fun isAnimationFinished(): Boolean {
        return animation!!.isAnimationFinished(elapsedTime)
    }

    fun setSpeed(speed: Float) {
        // if length is zero, then assume motion angle is zero degrees
        if (velocityVec.len() == 0f) velocityVec[speed] = 0f else velocityVec.setLength(speed)
    }

    fun getSpeed(): Float {
        return velocityVec.len()
    }

    fun setMotionAngle(angle: Float) {
        velocityVec.setAngleDeg(angle)
    }

    fun getMotionAngle(): Float {
        return velocityVec.angleDeg()
    }

    fun isMoving(): Boolean {
        return getSpeed() > 0
    }

    fun setAcceleration(acc: Float) {
        acceleration = acc
    }

    fun accelerateAtAngle(angle: Float) {
        accelerationVec.add(Vector2(acceleration, 0f).setAngleDeg(angle))
    }

    fun accelerateForward() {
        accelerateAtAngle(rotation)
    }

    fun setMaxSpeed(ms: Float) {
        maxSpeed = ms
    }

    fun setDeceleration(dec: Float) {
        deceleration = dec
    }

    fun applyPhysics(dt: Float) {
        // apply acceleration
        velocityVec.add(accelerationVec.x * dt, accelerationVec.y * dt)
        var speed = getSpeed()
        // decrease speed (decelerate) when not accelerating
        if (accelerationVec.len() == 0f) speed -= deceleration * dt
        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0f, maxSpeed)
        // update velocity
        setSpeed(speed)
        // apply velocity
        moveBy(velocityVec.x * dt, velocityVec.y * dt)
        // reset acceleration
        accelerationVec[0f] = 0f
    }

    fun getList(stage: Stage, className: String?): ArrayList<BaseActor>? {
        val list = ArrayList<BaseActor>()
        var theClass: Class<*>? = null
        try {
            theClass = Class.forName(className)
        } catch (error: Exception) {
            error.printStackTrace()
        }
        for (a in stage.actors) {
            if (theClass!!.isInstance(a)) list.add(a as BaseActor)
        }
        return list
    }

    fun count(stage: Stage?, className: String?): Int {
        return getList(stage!!, className)!!.size
    }

    fun centerAtPosition(x: Float, y: Float) {
        setPosition(x - width / 2, y - height / 2)
    }

    fun centerAtActor(other: BaseActor) {
        centerAtPosition(other.x + other.width / 2, other.y + other.height / 2)
    }

    fun setOpacity(opacity: Float) {
        color.a = opacity
    }


}