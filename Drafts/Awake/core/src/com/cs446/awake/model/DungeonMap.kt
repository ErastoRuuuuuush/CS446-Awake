package com.cs446.awake.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.utils.Array
val battleProbabilty = 30
val collectProbabilty = 30
val rownum = 2
val colnum = 6
// ToDo: generate map according to level
class DungeonMap(val level: Int) {
    var map: Array<Array<Event>> = Array<Array<Event>>()
    var stepsLeft : Int = 15
    init {
        for (row in 0..rownum){
            var eventRow = Array<Event>()
            for (col in 0..colnum){
                // The first is always empty entry
                if (row == 0 && col == 0){
                    eventRow.add(Event("Attack.png", "Heal.png"))
                    continue
                }
                // The last is always empty exit for next level, except for level == 4, which has the boss
                if (row == rownum && col == colnum){
                    if (level == 4) eventRow.add(BattleEvent("Attack.png", "skeleton1.png", m43))
                    else eventRow.add(Event("Attack.png", "Heal.png"))
                    continue
                }
                // randomize between battle, item, or empty
                val ram = (0 until 100).random()
                if (ram < battleProbabilty) {
                    var monster : Monster? = null
                    while (monster == null){
                        monster = monsterInfo.getMonster(level)
                        if (monster != null) eventRow.add(BattleEvent("Attack.png", "skeleton1.png", monster))
                    }
                } else if (ram < battleProbabilty + collectProbabilty){
                    var material : MaterialCard? = null
                    while (material == null){
                        material = materialInfo.getBelowLevel(level)
                        if (material != null) eventRow.add(CollectEvent("Attack.png", "Fire.png", material))
                    }
                } else {
                    eventRow.add(Event("Attack.png", "Heal.png"))
                }
            }
            map.add(eventRow)
        }
        // flipped the entry point
        map[0][0].trigger()
    }

    // check if going to a coordinate is allowed
    fun canGo(row: Int, col: Int): Boolean {
        // if out of map or no step left then return false
        if (col > colnum || col < 0 || row > rownum || row < 0 || stepsLeft <= 0) return false
        // if one of its four neighbours is flipped, the return true
        if (col < colnum && map[row][col+1].isFlipped()) return true
        if (col > 0 && map[row][col-1].isFlipped()) return true
        if (row < rownum && map[row+1][col].isFlipped()) return true
        if (row > 0 && map[row-1][col].isFlipped()) return true
        // other wise default false
        return false
    }

    // go to position row,col
    // decrement steps, return 0 if cannot go, 1 if empty event, 2 if collect event, 3 if battle event, 4 if leave the level
    fun go(row:Int, col: Int): Int {
        if (! canGo(row, col)) return INVALIDMOVE
        if (row == rownum && col == colnum) return NEXTLEVEL
        return map[row][col].trigger()
    }
}