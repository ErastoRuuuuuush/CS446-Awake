package com.cs446.awake.model

import com.badlogic.gdx.utils.Array

class MonsterData (monsterList: MutableList<Monster>): Data<Monster>(monsterList){
    fun getMonster(level: Int = dungeonLevel) : Monster?{
        var validList = MonsterData(mutableListOf())
        for (monster in getStored()){
            if (monster.level <= level){
                validList.add(monster)
            }
        }
        // select a random one
        return validList.randomSelect()
    }
}