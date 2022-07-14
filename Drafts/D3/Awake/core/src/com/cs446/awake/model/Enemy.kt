package com.cs446.awake.model


import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array


class Enemy(val images: Array<String?>, charName: String, HP: Int, energy: Int, strength: Int, deck: Deck, state: MutableList<State>, playerType: PlayerType) : Character(charName, HP, energy, strength, deck, state, playerType) {

    override fun initBars() {
        // bar background as red
        var pixmap = Pixmap(100, 15, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.BLACK)
        pixmap.fill()
        var drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        val progressBarStyle = ProgressBar.ProgressBarStyle()
        progressBarStyle.background = drawable

        // health as green
        pixmap = Pixmap(0, 15, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.RED)
        pixmap.fill()
        drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        progressBarStyle.knob = drawable

        pixmap = Pixmap(100, 15, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.RED)
        pixmap.fill()
        drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        progressBarStyle.knobBefore = drawable


        healthBar = ProgressBar(0.0f, 1.0f, 0.01f, false, progressBarStyle)
        healthBar.value = 1.0f
        healthBar.setAnimateDuration(0.25f)
        healthBar.setBounds(500F, 1000F, 1200F, 15F)
        healthBar.setRange(0f, HP / 100.0f) // TODO: everytime deal damage, update this
        healthBar.value = HP / 100.0f
    }
}