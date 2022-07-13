package com.cs446.awake.model

import com.badlogic.gdx.utils.Array

class MonsterData (val monsterInfo: Array<Monster>): Data(){
    override fun getStored(): Array<Any> {
        return monsterInfo as Array<Any>
    }
}