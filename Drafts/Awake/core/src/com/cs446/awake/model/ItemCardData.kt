package com.cs446.awake.model

import java.lang.Integer.max

class ItemCardData (cardList: MutableList<ItemCard>): CardData(cardList as MutableList<MergableCard>){

    // store the copy of list as ItemCards. Only validate after generateItemList is called
    var itemList = mutableListOf<ItemCard>()
    fun generateItemList(){
        itemList = list as MutableList<ItemCard>
    }

    // add a new card into the data list
    override fun add(card: MergableCard) {
        if (card is ItemCard) super.add(card)
        println("trying to add a non-item into deck")
    }

    // return the card with this name, or null if not exist
    override fun find(name: String): ItemCard? {
        val result = super.find(name)
        if (result is ItemCard) return result
        return null
    }

    // remove a card.
    override fun remove(card: MergableCard){
        if (card is ItemCard) super.remove(card)
        println("trying to remove a non-item from deck")
    }

    // return the possible merge from this list given input list
    fun merge(inputList: CardData): ItemCard?{
        var validList = ItemCardData(mutableListOf<ItemCard>())
        for (item in getStored()){
            // be a candidate if all element fields are satisfied
            if (item.earth <= max(inputList.earth,0)
                && item.fire <= max(inputList.fire ,0)
                && item.metal <= max(inputList.metal,0)
                && item.electric <= max(inputList.electric,0)
                && item.water <= max(inputList.water,0)
                && item.wood <= max(inputList.wood,0)
                && item.wind <= max(inputList.wind,0)){
                validList.add(item)
            }
        }
        // select a random one
        return validList.randomSelect() as ItemCard?
    }

    override fun randomSelect(): ItemCard? {
        return super.randomSelect() as ItemCard?
    }
}
