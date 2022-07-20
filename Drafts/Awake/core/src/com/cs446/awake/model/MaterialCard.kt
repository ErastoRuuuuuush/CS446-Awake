package com.cs446.awake.model

import com.google.gson.Gson

// material cards that could be collected
class MaterialCard (cardName: String, img: String, usage: String, wood: Int = 0,
                    fire: Int = 0, earth: Int = 0, metal: Int = 0, water: Int = 0,
                    electric: Int = 0, wind : Int = 0, count: Int = 1, val level: Int = 1):
    MergableCard(cardName, img, usage, count, wood, fire, earth, metal, water, electric, wind)
{

    // create a clone (deep copy) of the data
    override fun clone(): MaterialCard{
        val string = Gson().toJson(this, MaterialCard::class.java)
        return Gson().fromJson<MaterialCard>(string, MaterialCard::class.java)
    }
}