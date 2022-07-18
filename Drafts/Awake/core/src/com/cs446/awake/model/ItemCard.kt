package com.cs446.awake.model

class ItemCard (cardName: String, img: String, usage: String, wood: Int = 0,
                 fire: Int = 0, earth: Int = 0, metal: Int = 0, water: Int = 0,
                 electric: Int = 0, wind : Int = 0, var actionCards: Deck, count: Int = 1):
    MergableCard(cardName, img, usage, count, wood, fire, earth, metal, water, electric, wind){

    // add the action cards of this item
    override fun use() {
        for (i in (0 until actionCards.count())){
            val curCard = actionCards.pop()
            // generate a separate copy for each card count
            for (j in (0 until curCard.count)){
                val copyCard = ActionCard(curCard.cardName, curCard.img, curCard.usage, curCard.energyCost,
                     curCard.strengthCost, curCard.healthChange, curCard.Effect, 1)
                deck.addCard(copyCard)
            }
        }
    }
    }