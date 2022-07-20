package com.cs446.awake.model


import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array


class Enemy(val images: Array<String?>, charName: String, HP: Int, energy: Int, strength: Int, var enemyImage: String, deck: Deck, state: MutableList<State>, playerType: PlayerType) : Character(charName, HP, energy, strength, enemyImage,deck, state, playerType) {

    override fun initBars() {
        val heathBarStyle = createBarStyle(Color.BLACK, Color.RED, 20)

        healthBar = ProgressBar(0.0f, 1.0f, 0.01f, false, heathBarStyle)
        healthBar.value = 1.0f
        healthBar.setAnimateDuration(0.25f)
        healthBar.setBounds(1400F, 980F, 600F, 20f)
        healthBar.setRange(0f, HP / 100.0f) // TODO: everytime deal damage, update this
        healthBar.value = HP / 100.0f

        val energyBarStyle = createBarStyle(Color.BLACK, Color.SKY, 20)

        energyBar =  ProgressBar(0.0f, 1.0f, 0.01f, false, energyBarStyle)
        energyBar.value = 1.0f
        energyBar.setAnimateDuration(0.25f)
        energyBar.setBounds(1500F, 930F, 500F, 20f)
        energyBar.setRange(0f, energy / 100.0f) // TODO: everytime deal damage, update this
        energyBar.value = energy / 100.0f
    }

    override fun initCharImage() {
    }

    override fun initChar() {
//        initCharImage()
        initBars()
    }
}