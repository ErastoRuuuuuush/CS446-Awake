package com.cs446.awake.model

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

class Player(charName: String, HP: Int, energy: Int, strength: Int, deck: Deck, state: MutableList<State>, playerType: PlayerType) : Character(charName, HP, energy, strength, deck, state, playerType) {
    lateinit var energyBar : ProgressBar
    lateinit var strengthBar : ProgressBar

    private fun createBarStyle(backGroundColor: Color, frontColor: Color, height : Int) : ProgressBar.ProgressBarStyle {
        // bar background as red
        var pixmap = Pixmap(10, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(backGroundColor)
        pixmap.fill()
        var drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        val progressBarStyle = ProgressBar.ProgressBarStyle()
        progressBarStyle.background = drawable

        // health as green
        pixmap = Pixmap(0, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(frontColor)
        pixmap.fill()
        drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        progressBarStyle.knob = drawable

        pixmap = Pixmap(10, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(frontColor)
        pixmap.fill()
        drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        progressBarStyle.knobBefore = drawable

        return progressBarStyle
    }

    override fun initBars() {

        val heathBarStyle = createBarStyle(Color.BLACK, Color.RED, 10)

        healthBar = ProgressBar(0.0f, 1.0f, 0.01f, false, heathBarStyle)
        healthBar.value = 1.0f
        healthBar.setAnimateDuration(0.25f)
        healthBar.setBounds(100F, 120F, 400F, 10F)
        healthBar.setRange(0f, HP / 100.0f) // TODO: everytime deal damage, update this
        healthBar.value = HP / 100.0f

        val strengthBarStyle = createBarStyle(Color.BLACK, Color.SKY, 8)

        strengthBar =  ProgressBar(0.0f, 1.0f, 0.01f, false, strengthBarStyle)
        strengthBar.value = 1.0f
        strengthBar.setAnimateDuration(0.25f)
        strengthBar.setBounds(100F, 100F, 300F, 8F)
        strengthBar.setRange(0f, strength / 100.0f) // TODO: everytime deal damage, update this
        strengthBar.value = strength / 100.0f

        val energyBarStyle = createBarStyle(Color.BLACK, Color.GREEN, 8)

        energyBar =  ProgressBar(0.0f, 1.0f, 0.01f, false, energyBarStyle)
        energyBar.value = 1.0f
        energyBar.setAnimateDuration(0.25f)
        energyBar.setBounds(100F, 80F, 300F, 8F)
        energyBar.setRange(0f, energy / 100.0f) // TODO: everytime deal damage, update this
        energyBar.value = energy / 100.0f
    }

    override fun updateStrength(strengthChange: Int) {
        strength += strengthChange
        strengthBar.value = strength / 100f
    }

    override fun updateEnergy(energyChange: Int) {
        energy += energyChange
        energyBar.value = energy / 100f
    }
}