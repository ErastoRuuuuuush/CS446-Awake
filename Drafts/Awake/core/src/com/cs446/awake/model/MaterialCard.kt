package com.cs446.awake.model

// material cards that could be collected
class MaterialCard (cardName: String, img: String, usage: String, wood: Int = 0,
                    fire: Int = 0, earth: Int = 0, metal: Int = 0, water: Int = 0,
                    electric: Int = 0, wind : Int = 0, count: Int = 1):
    MergableCard(cardName, img, usage, count, wood, fire, earth, metal, water, electric, wind)
{

}