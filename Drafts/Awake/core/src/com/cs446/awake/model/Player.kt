package com.cs446.awake.model

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.cs446.awake.utils.BaseActor

class Player(charName: String, HP: Int, energy: Int, strength: Int, var playerImage: String, deck: Deck, state: MutableList<State>, playerType: PlayerType) : Character(charName, HP, energy, strength, playerImage, deck, state, playerType) {
//    lateinit var strengthBar : ProgressBar

    override fun initBars() {
//        healthBar = BaseActor(0f, 0f, stage)
//        healthBar.loadTexture("HP_line.png")
//        energyBar = BaseActor(0f, 0f, stage)
//        energyBar.loadTexture("MP_line.png")

        healthBar = Image(Texture(Gdx.files.internal("HP_line.png")))
        energyBar = Image(Texture(Gdx.files.internal("MP_line.png")))

        healthBar.setSize(308f, 82f)
        energyBar.setSize(338f, 82f)
        healthBar.setPosition(350f, 95f)
        energyBar.setPosition(320f, 28f)

        healthOriginalWidth = healthBar.width
        healthStartX = healthBar.x
        energyOriginalWidth = energyBar.width
        energyStartX = energyBar.x

    }

    override fun updateHealth(HpChange: Int){
        HP += HpChange
        healthBar.setSize(HP.toFloat()/originalHP * healthOriginalWidth, healthBar.height)
    }


    override fun updateEnergy(energyChange: Int) : Boolean{
        // not valid if decrease below 0
        if (energyChange + energy < 0) return false
        energy += energyChange
        if (energy > maxEnergy) energy = maxEnergy
        energyBar.setSize(energy.toFloat()/originalEnergy * energyOriginalWidth, energyBar.height)
        println("Energy: " + energy)
        return true
    }

    override fun initCharImage() {

    }
    override fun initChar() {
//        initCharImage()

    }
}