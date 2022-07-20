package com.cs446.awake.model

import java.lang.Integer.max

class MaterialCardData (cardList: MutableList<MaterialCard>): CardData(cardList as MutableList<MergableCard>){

    // store the copy of list as MaterialCards. Only validate after generateItemList is called
    var itemList = mutableListOf<MaterialCard>()
    fun generateItemList(){
        itemList = list as MutableList<MaterialCard>
    }

    // add a new card into the data list
    override fun add(card: MergableCard) {
        if (card is MaterialCard) super.add(card)
        println("trying to add a non-material into deck")
    }

    // return the card with this name, or null if not exist
    override fun find(name: String): MaterialCard? {
        val result = super.find(name)
        if (result is MaterialCard) return result
        return null
    }

    // remove a card.
    override fun remove(card: MergableCard){
        if (card is MaterialCard) super.remove(card)
        println("trying to remove a non-material from deck")
    }


    // get a random card below certain level
    open fun getBelowLevel(level: Int): MaterialCard? {
        var validList = MaterialCardData(mutableListOf<MaterialCard>())
        for (card in getStored()){
            if (card !is MaterialCard){
                println("not a material card found in the list")
                continue
            }
            // be a candidate if all element fields are satisfied
            if (card.level <= level){
                validList.add(card)
            }
        }
        // select a random one
        return validList.randomSelect()
    }

    // return the possible merge from this list given input list
    fun merge(inputList: CardData): MaterialCard?{
        var validList = MaterialCardData(mutableListOf<MaterialCard>())
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
        return validList.randomSelect() as MaterialCard?
    }

    override fun randomSelect(): MaterialCard? {
        return super.randomSelect() as MaterialCard?
    }
}
