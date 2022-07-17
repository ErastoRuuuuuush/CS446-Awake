package com.cs446.awake.model

// cards that could be used in merge system: material card and item cards
open class MergableCard (cardName: String, img: String, usage: String, count: Int = 1,
                    val wood: Int = 0, val fire: Int = 0, val earth: Int = 0, val metal: Int = 0,
                    val water: Int = 0, val electric: Int = 0, val wind : Int = 0 ) : Card(cardName, img, usage, count)
{
}