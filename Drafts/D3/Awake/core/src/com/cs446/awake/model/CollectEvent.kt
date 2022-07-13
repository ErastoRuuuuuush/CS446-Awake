package com.cs446.awake.model

class CollectEvent (backImg: String, frontImg: String, val material: MaterialCard) : Event(backImg, frontImg)
{
    override fun trigger() {
        super.trigger()
        backPackMaterial.add(material)
        // TODO: notify viewer that material is added
    }
}