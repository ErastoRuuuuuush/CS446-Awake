package com.cs446.awake.model

import com.badlogic.gdx.utils.Array

class MaterialData (val materialInfo: Array<MaterialCard>): Data(){
    override fun getStored(): Array<Any> {
        return materialInfo as Array<Any>
    }
}