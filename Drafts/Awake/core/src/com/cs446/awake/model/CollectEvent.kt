package com.cs446.awake.model

class CollectEvent (backImg: String, frontImg: String, val material: MaterialCard) : Event(backImg, frontImg)
{
    override fun trigger() : Int {
        super.trigger()
        backPackMaterial.add(material)
        return COLLECT
    }
}