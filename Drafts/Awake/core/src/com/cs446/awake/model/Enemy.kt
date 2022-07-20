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
import com.badlogic.gdx.utils.Array
import com.cs446.awake.utils.BaseActor


class Enemy(val images: Array<String?>, charName: String, HP: Int, energy: Int, strength: Int, var enemyImage: String, deck: Deck, state: MutableList<State>, playerType: PlayerType) : Character(charName, HP, energy, strength, enemyImage,deck, state, playerType) {

    override fun initBars() {
//        healthBar = BaseActor(0f, 0f, stage)
//        healthBar.loadTexture("HP_line.png")
//        energyBar = BaseActor(0f, 0f, stage)
//        energyBar.loadTexture("MP_line.png")

        healthBar = Image(Texture(Gdx.files.internal("HP_line.png")))
        energyBar = Image(Texture(Gdx.files.internal("MP_line.png")))

        healthBar.setSize(388f, 82f)
        energyBar.setSize(550f, 80f)
        healthBar.setPosition(1682f, 767f)
        energyBar.setPosition(1450f, 900f)

        healthOriginalWidth = healthBar.width
        healthStartX = healthBar.x
        energyOriginalWidth = energyBar.width
        energyStartX = energyBar.x
    }


    override  fun updateHealth(HpChange: Int){
        HP += HpChange
        val gap = healthBar.width - HP.toFloat()/originalHP * healthOriginalWidth
        healthBar.setSize(HP.toFloat()/originalHP * healthOriginalWidth, healthBar.height)
        healthBar.setPosition(healthBar.x + gap, healthBar.y)
    }


    override fun updateEnergy(energyChange: Int) : Boolean{
        energy += energyChange
        if (energy > maxEnergy) energy = maxEnergy
        val gap = energyBar.width - energy.toFloat()/originalEnergy * energyOriginalWidth
        energyBar.setSize(energy.toFloat()/originalEnergy * energyOriginalWidth, energyBar.height)
        energyBar.setPosition(energyBar.x + gap, energyBar.y)
        return true
    }
    override fun initCharImage() {
    }

    override fun initChar() {
//        initCharImage()
//        initBars()
    }
}