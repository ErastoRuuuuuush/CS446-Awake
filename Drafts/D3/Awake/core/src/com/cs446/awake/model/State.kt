package com.cs446.awake.model

class State(val stateName: String,
            var effectiveRound: Int,
            var target: Character,
            ) {

    var releaseProbability = 0.0    // prob of getting rid of the status
    var moveProbability = 1.0  // probability of making of move
    var damage = 0.0
    var energyEffect = 0.0
    var strengthEffect = 0.0
    var releaseList: MutableList<String> = mutableListOf()  // release from other states when cur state is added


    init {
        when(stateName) {
            "Burn" -> setBurn()
            "Freeze" -> setFreeze()
            "Poison" -> setPoison()
            "Paralysis" -> setParalysis()
            "Sleep" -> setSleep()
        }

    }

    private fun setBurn(){
        releaseProbability = 0.2
        energyEffect = -0.5
        damage = -0.0625
        releaseList.add("Freeze")

    }

    private fun setFreeze(){
        releaseProbability = 0.2
        moveProbability = 0.0
        strengthEffect = -0.5
        releaseList.add("Burn")

    }

    private fun setPoison(){
        damage = -0.0625
    }

    private fun setParalysis(){
        moveProbability = 0.75
    }

    private fun setSleep(){
        moveProbability = 0.0
        releaseList.addAll(listOf("Burn", "Freeze", "Poison", "Paralysis"))

    }

    fun apply(){
        target.removeState(releaseList)
    }

}