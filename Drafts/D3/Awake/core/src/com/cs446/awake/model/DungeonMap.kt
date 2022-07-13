package com.cs446.awake.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.utils.Array
val battleProbabilty = 10
val collectProbabilty = 30
val rownum = 2
val colnum = 6
// ToDo: generate map according to level
class DungeonMap(val level: Int) {
    var map: Array<Array<Event>> = Array<Array<Event>>()
    var currow = 0
    var curcol = 0
    init {
        for (row in 0..rownum){
            var eventRow = Array<Event>()
            for (col in 0..colnum){
                // The first is always empty entry
                if (row == 0 && col == 0){
                    eventRow.add(Event("cardback", "entry"))
                    continue
                }
                // The last is always the boss
                if (row == rownum && col == colnum){
                    eventRow.add(BattleEvent("back", "boss", monsterInfo.randomSelect() as Monster))
                    continue
                }
                // randomize between battle, item, or empty
                val ram = (0..100).random()
                if (ram < battleProbabilty){
                    eventRow.add(BattleEvent("back", "boss", monsterInfo.randomSelect() as Monster))
                } else if (ram < battleProbabilty + collectProbabilty){
                    eventRow.add(CollectEvent("back", "collect", materialInfo.randomSelect() as MaterialCard))
                } else {
                    eventRow.add(Event("cardback", "empty"))
                }
            }
            map.add(eventRow)
        }
    }

    // check if going to a coordinate is allowed
    fun canGo(row: Int, col: Int): Boolean {
        if (col > colnum || col < 0 || row > rownum || row < 0) return false
        if (col < colnum && map[row][col+1].isFlipped()) return true
        if (col > 0 && map[row][col-1].isFlipped()) return true
        if (row < rownum && map[row+1][col].isFlipped()) return true
        if (row > 0 && map[row-1][col].isFlipped()) return true
        return false
    }

    // go to position row,col
    fun go(row:Int, col: Int): Boolean {
        if (! canGo(row, col)) return false
        map[row][col].trigger()
        return true
    }
}