package com.cs446.awake.model

class ItemCard (cardName: String, img: String, usage: String, var actions: List<ActionCard>) : Card(cardName, img, usage)
{
    override fun use(){}
}