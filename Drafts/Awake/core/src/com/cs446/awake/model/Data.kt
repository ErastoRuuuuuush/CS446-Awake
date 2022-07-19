package com.cs446.awake.model

import com.google.gson.Gson


open class Data<T> (var list: MutableList<T>){
    // add an element into the list
    open fun add(element: T){
        list.add(element)
    }
    // remove an element
    open fun remove(element: T){
        list.remove(element)
    }
    // combine another data list
    fun append(newList: Data<T>){
        for (newElement in newList.getStored()){
            add(newElement)
        }
    }
    // get all elements in the list
    open fun getStored(): MutableList<T>{
        return list
    }
    // select a random element in the data list
    open fun randomSelect(): T? {
        val all : MutableList<T> = getStored()
        if (all.size == 0){
            return null
        }
        val rad = (0 until all.size).random()
        return all[rad]
    }
}