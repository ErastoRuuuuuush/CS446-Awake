package com.cs446.awake.model

import kotlin.random.Random

class State(val stateName: String,
            var effectiveRound: Int
            ) {

    var releaseProbability = 0.0    // prob of getting rid of the status
    var moveProbability = 1.0  // probability of making of move
    var damage = 0.0
    var energyEffect = 0.0
    var strengthEffect = 0.0
    var releaseList: MutableList<String> = mutableListOf()  // release from other states when cur state is added
    var description = ""
    var img = ""


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

        // add description
        img = "burn.png"
        description = "Burn"

    }

    private fun setFreeze(){
        releaseProbability = 0.2
        moveProbability = 0.0
        strengthEffect = -0.5
        releaseList.add("Burn")

        img = "freeze.png"
        description = "Freeze"

    }

    private fun setPoison(){
        damage = -0.0625

        img = "poison.png"
        description = "Poison"
    }

    private fun setParalysis(){
        moveProbability = 0.75

        img = "paralysis.png"
        description = "Paralysis"
    }

    private fun setSleep(){
        moveProbability = 0.0
        releaseList.addAll(listOf("Burn", "Freeze", "Poison", "Paralysis"))

        img = "sleep.png"
        description = "Sleep"

    }

    private fun random_event(prob:Double): Boolean{
        val oneNums = (prob * 100).toInt()
        val randomIndex = Random.nextInt(100)
        if (randomIndex < oneNums) {
            return true
        }
        return false
    }

    fun extend(extendedState: State){
        effectiveRound += extendedState.effectiveRound
    }

    fun apply(target: Character){
        target.removeState(releaseList)
        // check if the state can be released

        if (releaseProbability > 0 && random_event(releaseProbability)) {
            target.removeState(mutableListOf(stateName))
            return
        }

        // check if player can move
        if (moveProbability == 0.0) {
            target.endRound()
        } else if (moveProbability < 1.0 && !random_event(moveProbability)) {
            target.endRound()
        }

        // damage
        val damageAmount = (damage * target.HP).toInt()
        target.updateHealth(damageAmount)

        // strength effect
        val strengthAmount = (strengthEffect * target.strength).toInt()
        target.updateStrength(strengthAmount)

        // energy effect
        val energyAmount = (energyEffect * target.strength).toInt()
        target.updateEnergy(energyAmount)

        effectiveRound -= 1

        // check effective round
        if (effectiveRound <= 0){
            target.removeState(mutableListOf(stateName))
        }
    }

}