package com.cs446.awake.model

import com.badlogic.gdx.utils.Array

abstract class Data {
    abstract fun getStored(): Array<Any>;
    open fun randomSelect(): Any? {
        val all : Array<Any> = getStored();
        val rad = (0..all.size).random()
        return all[rad]
    }
}