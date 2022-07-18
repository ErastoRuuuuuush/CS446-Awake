package com.cs446.awake.model

import java.lang.Integer.max

class CardData (cardList: MutableList<MergableCard>): Data<MergableCard>(cardList){
    // the sum of all element in the list
    var wood: Int = 0
    var fire: Int = 0
    var earth: Int = 0
    var metal: Int = 0
    var water: Int = 0
    var electric: Int = 0
    var wind : Int = 0

    // add a new card into the data list
    override fun add(card: MergableCard){
        // update sum
        wood += card.wood * card.count
        fire += card.fire * card.count
        earth += card.earth * card.count
        metal += card.metal * card.count
        water += card.water * card.count
        electric += card.electric * card.count
        wind += card.wind * card.count

        // if card with same name already exists, just add up the count
        for (existingCard in list){
            if (card.cardName == existingCard.cardName){
                existingCard.count += card.count
                return
            }
        }
        // else just add the card
        super.add(card.clone())
    }

    // remove a card.
    override fun remove(card: MergableCard){
        // if card with same name already exists, update
        for (existingCard in list){
            if (card.cardName == existingCard.cardName){
                var changedAmount = card.count
                // if more than the existing amount, remove all
                if (changedAmount > existingCard.count){
                    changedAmount = existingCard.count
                }
                existingCard.count -= changedAmount
                // update sum of elements
                wood -= card.wood * changedAmount
                fire -= card.fire * changedAmount
                earth -= card.earth * changedAmount
                metal -= card.metal * changedAmount
                water -= card.water * changedAmount
                electric -= card.electric * changedAmount
                wind -= card.wind * changedAmount
                break
            }
        }
    }


    // merge materials or items to get a new item
    // possilbeOutcomes should be all item cards, in particular, our global variable itemInfo
    fun merge(inputList: CardData, possilbeOutcomes: CardData): ItemCard?{
        var validList = CardData(mutableListOf<MergableCard>())
        for (item in possilbeOutcomes.getStored()){
            // be a candidate if all element fields are satisfied
            if (item.earth >= max(inputList.earth,0)
                && item.fire >= max(inputList.fire ,0)
                && item.metal >= max(inputList.metal,0)
                && item.electric >= max(inputList.electric,0)
                && item.water >= max(inputList.water,0)
                && item.wood >= max(inputList.wood,0)
                && item.wind >= max(inputList.wind,0)){
                validList.add(item)
            }
        }
        // select a random one
        return validList.randomSelect() as ItemCard?
    }


    // initialize the sum value for the elements, combine same cards if possible
    init {
        val initialList = list
        list = mutableListOf()
        for (card in initialList){
            add(card)
        }
    }
}
