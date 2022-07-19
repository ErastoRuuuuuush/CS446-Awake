package com.cs446.awake.model

import java.lang.Integer.max

class ItemCardData (cardList: MutableList<ItemCard>): CardData(cardList as MutableList<MergableCard>){

    // add a new card into the data list
    fun add(card: ItemCard){
        super.add(card)
    }

    // return the card with this name, or null if not exist
    override fun find(name: String): ItemCard? {
        return super.find(name) as ItemCard?
    }

    // remove a card.
    fun remove(card: ItemCard){
        super.remove(card)
    }

    override fun getBelowLevel(level: Int): ItemCard? {
        return super.getBelowLevel(level) as ItemCard
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

    override fun randomSelect(): MergableCard? {
        return super.randomSelect() as ItemCard?
    }
}
