package com.cs446.awake.model

// material cards that could be collected
class MaterialCard (cardName: String, img: String, usage: String,
                    val wood: Int = 0, val fire: Int = 0, val earth: Int = 0, val metal: Int = 0,
                    val water: Int = 0, val electric: Int = 0, val wind : Int = 0 ) : Card(cardName, img, usage)
{

}