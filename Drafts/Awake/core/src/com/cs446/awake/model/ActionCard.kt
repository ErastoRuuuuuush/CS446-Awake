package com.cs446.awake.model


import com.badlogic.gdx.utils.Array
import com.google.gson.Gson

// cards that will be used in fights
class ActionCard (cardName: String, img: String, usage: String = "", val energyCost: Int,
                  val strengthCost: Int, val healthChange: Int, val Effect: Array<State>, count: Int = 1)
    : Card(cardName, img, usage, count) {

    // create a clone (deep copy) of the data
    override fun clone(): ActionCard{
        val stringItem = Gson().toJson(this, ActionCard::class.java)
        return Gson().fromJson<ActionCard>(stringItem, ActionCard::class.java)
    }
    fun isHealCard() : Boolean {
        if (healthChange > 0) {
            return true
        }
        return false
    }


    init {
        // initialize the usage if not given
        if (usage == ""){
            if (healthChange > 0){
                this.usage += "Heals $healthChange HP, "
            } else if (healthChange < 0) {
                this.usage += "Deals ${-healthChange} damage, "
            }
            for (i in (0 until Effect.size)){
                val state = Effect[i]
                this.usage += ("applies " + state.stateName + " for ${state.effectiveRound} rounds, ")
            }
            this.usage += "and costs $energyCost energy."
        }
    }

}
